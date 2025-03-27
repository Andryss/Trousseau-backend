package ru.andryss.trousseau.service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import ru.andryss.trousseau.exception.Errors;
import ru.andryss.trousseau.exception.TrousseauException;
import ru.andryss.trousseau.generated.model.FilterInfo;
import ru.andryss.trousseau.generated.model.ItemInfoRequest;
import ru.andryss.trousseau.generated.model.SearchInfo;
import ru.andryss.trousseau.model.BookingEntity;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.model.ItemStatus;
import ru.andryss.trousseau.repository.BookingRepository;
import ru.andryss.trousseau.repository.ItemRepository;

import static ru.andryss.trousseau.model.ItemStatus.ARCHIVED;
import static ru.andryss.trousseau.model.ItemStatus.BOOKED;
import static ru.andryss.trousseau.model.ItemStatus.DRAFT;
import static ru.andryss.trousseau.model.ItemStatus.PUBLISHED;
import static ru.andryss.trousseau.model.ItemStatus.READY;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private static final int MAX_BOOKINGS_PER_USER = 2;
    private static final int MAX_ITEMS_IN_FEED = 5;
    private static final String PUBLISHED_ITEMS_FILTER = "status=PUBLISHED";

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final TimeService timeService;
    private final TransactionTemplate transactionTemplate;

    private final DateTimeFormatter itemIdFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");

    private final List<ItemStatus> ITEM_EDITABLE_STATUSES = List.of(
            DRAFT, READY
    );

    private final List<ItemStatus> ITEM_READABLE_PUBLIC_STATUSES = List.of(
            PUBLISHED, BOOKED, ARCHIVED
    );

    private final Table<ItemStatus, ItemStatus, Transit> sellerTransitions =
            ImmutableTable.<ItemStatus, ItemStatus, Transit>builder()
                    .put(READY, PUBLISHED, emptyTransit())
                    .put(PUBLISHED, READY, emptyTransit())
                    .put(BOOKED, PUBLISHED, unbookTransit())
                    .put(BOOKED, ARCHIVED, closeTransit())
                    .build();

    private final Table<ItemStatus, ItemStatus, Transit> publicTransitions =
            ImmutableTable.<ItemStatus, ItemStatus, Transit>builder()
                    .put(PUBLISHED, BOOKED, bookTransit())
                    .put(BOOKED, PUBLISHED, unbookTransit())
                    .build();

    @Override
    public ItemEntity createItem(ItemInfoRequest info) {
        log.info("Creating item title={}, mediaIds={}, description={}", info.getTitle(), info.getMedia(),
                info.getDescription());

        ItemEntity item = new ItemEntity();
        patchItem(item, info);

        ZonedDateTime now = timeService.now();
        item.setId(itemIdFormatter.format(now));
        item.setCreatedAt(now.toInstant());

        return itemRepository.save(item);
    }

    @Override
    public ItemEntity updateItem(String id, ItemInfoRequest info) {
        log.info("Updating item with id={}, title={}, mediaIds={}, description={}", id, info.getTitle(),
                info.getMedia(), info.getDescription());

        return transactionTemplate.execute((status) -> {
            ItemEntity item = findByIdOrThrow(id);

            if (!ITEM_EDITABLE_STATUSES.contains(item.getStatus())) {
                throw Errors.itemNotEditable(item.getStatus());
            }

            patchItem(item, info);
            return itemRepository.update(item);
        });
    }

    @Override
    public List<ItemEntity> getItems() {
        log.info("Getting items");

        return itemRepository.findAllOrderByCreatedAtDesc();
    }

    @Override
    public ItemEntity getItem(String id) {
        log.info("Getting item {}", id);

        return findByIdOrThrow(id);
    }

    @Override
    public void changeSellerItemStatus(String id, ItemStatus status) {
        log.info("Changing item {} status to {} as seller", id, status);

        changeStatusInternal(id, status, sellerTransitions);
    }

    @Override
    public void changePublicItemStatus(String id, ItemStatus status) {
        log.info("Changing item {} status to {} as public", id, status);

        changeStatusInternal(id, status, publicTransitions);
    }

    @Override
    public List<ItemEntity> searchItems(SearchInfo search) {
        log.info("Searching items with info by {}", search);

        if (search.getFilter() == null) {
            search.setFilter(new FilterInfo());
        }
        search.getFilter().addConditionsItem(PUBLISHED_ITEMS_FILTER);

        return itemRepository.findAll(search);
    }

    @Override
    public ItemEntity getPublicItem(String itemId) {
        log.info("Getting public item {}", itemId);

        return transactionTemplate.execute(status -> {
            ItemEntity item = findByIdOrThrow(itemId);

            if (!ITEM_READABLE_PUBLIC_STATUSES.contains(item.getStatus())) {
                throw Errors.itemNotFound(itemId);
            }

            return item;
        });
    }

    @Override
    public List<ItemEntity> getBooked() {
        log.info("Getting booked items");

        return itemRepository.findAllByStatusOrderByCreatedAtDesc(BOOKED);
    }

    @Override
    public List<ItemEntity> getFeed() {
        log.info("Getting feed items");

        return itemRepository.findAllByStatusOrderByCreatedAtDesc(PUBLISHED, MAX_ITEMS_IN_FEED);
    }

    private ItemEntity findByIdOrThrow(String itemId) {
        Optional<ItemEntity> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw Errors.itemNotFound(itemId);
        }
        return item.get();
    }

    private void changeStatusInternal(
            String id,
            ItemStatus status,
            Table<ItemStatus, ItemStatus, Transit> transitions
    ) {
        transactionTemplate.executeWithoutResult((transactionStatus -> {
            ItemEntity item = findByIdOrThrow(id);
            ItemStatus currentStatus = item.getStatus();

            Transit transit = transitions.get(currentStatus, status);
            if (transit == null) {
                throw Errors.illegalItemStatusTransition(currentStatus, status);
            }
            transit.transit(item);

            item.setStatus(status);
            itemRepository.update(item);
        }));
    }

    private interface Transit {
        void transit(ItemEntity item) throws TrousseauException; // must be called inside transaction
    }

    private Transit emptyTransit() {
        return item -> {

        };
    }

    private Transit bookTransit() {
        return item -> {
            List<BookingEntity> bookings = bookingRepository.findAll();

            if (bookings.size() >= MAX_BOOKINGS_PER_USER) {
                throw Errors.maximumBookingsCountReached(MAX_BOOKINGS_PER_USER);
            }

            ZonedDateTime now = timeService.now();

            BookingEntity booking = new BookingEntity();
            booking.setId(itemIdFormatter.format(now));
            booking.setItemId(item.getId());
            booking.setBookedAt(now.toInstant());

            bookingRepository.save(booking);
        };
    }

    private Transit unbookTransit() {
        return this::deleteBookingOrThrow;
    }

    private Transit closeTransit() {
        return this::deleteBookingOrThrow;
    }

    private void deleteBookingOrThrow(ItemEntity item) {
        int deleted = bookingRepository.deleteByItemId(item.getId());

        if (deleted == 0) {
            throw Errors.bookingNotFound(item.getId());
        }
    }

    private static void patchItem(ItemEntity item, ItemInfoRequest info) {
        item.setTitle(info.getTitle());
        item.setMediaIds(info.getMedia());
        item.setDescription(info.getDescription());

        item.setStatus(isFilledRequiredFields(item) ? READY : DRAFT);
    }

    private static boolean isFilledRequiredFields(ItemEntity item) {
        return !StringUtils.isBlank(item.getTitle())
                && !CollectionUtils.isEmpty(item.getMediaIds())
                && !StringUtils.isBlank(item.getDescription());
    }
}
