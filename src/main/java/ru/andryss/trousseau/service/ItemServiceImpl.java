package ru.andryss.trousseau.service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import ru.andryss.trousseau.model.EventEntity.EventType;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.model.ItemStatus;
import ru.andryss.trousseau.repository.BookingRepository;
import ru.andryss.trousseau.repository.ItemRepository;
import ru.andryss.trousseau.security.UserData;

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
    private static final String PUBLISHED_ITEMS_FILTER = "status=PUBLISHED";

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final TimeService timeService;
    private final TransactionTemplate transactionTemplate;
    private final SearchHelper searchHelper;
    private final EventService eventService;

    private final DateTimeFormatter itemIdFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");

    private final List<ItemStatus> ITEM_EDITABLE_STATUSES = List.of(
            DRAFT, READY
    );

    private final List<ItemStatus> ITEM_READABLE_PUBLIC_STATUSES = List.of(
            PUBLISHED, BOOKED, ARCHIVED
    );

    private final Table<ItemStatus, ItemStatus, Transit> sellerTransitions =
            ImmutableTable.<ItemStatus, ItemStatus, Transit>builder()
                    .put(READY, PUBLISHED, publishTransit())
                    .put(PUBLISHED, READY, unpublishTransit())
                    .put(BOOKED, PUBLISHED, unbookTransit())
                    .put(BOOKED, ARCHIVED, closeTransit())
                    .build();

    private final Table<ItemStatus, ItemStatus, Transit> publicTransitions =
            ImmutableTable.<ItemStatus, ItemStatus, Transit>builder()
                    .put(PUBLISHED, BOOKED, bookTransit())
                    .put(BOOKED, PUBLISHED, unbookTransit())
                    .build();

    @Override
    public ItemEntity createItem(UserData user, ItemInfoRequest info) {
        log.info("Creating item for user {}: title={}, mediaIds={}, description={}, category={}",
                user.getId(), info.getTitle(), info.getMedia(), info.getDescription(), info.getCategory());

        ItemEntity item = new ItemEntity();
        patchItem(item, info);

        ZonedDateTime now = timeService.now();
        item.setId(itemIdFormatter.format(now));
        item.setOwner(user.getId());
        item.setCreatedAt(now.toInstant());

        return itemRepository.save(item);
    }

    @Override
    public ItemEntity updateItem(String id, UserData user, ItemInfoRequest info) {
        log.info("Updating item with id {} as user {}: title={}, mediaIds={}, description={}, category={}",
                id, user.getId(), info.getTitle(), info.getMedia(), info.getDescription(), info.getCategory());

        return transactionTemplate.execute((status) -> {
            ItemEntity item = findByIdAndOwnerOrThrow(id, user.getId());

            if (!ITEM_EDITABLE_STATUSES.contains(item.getStatus())) {
                throw Errors.itemNotEditable(item.getStatus());
            }

            patchItem(item, info);
            return itemRepository.update(item);
        });
    }

    @Override
    public List<ItemEntity> getItems(UserData user) {
        log.info("Getting items as user {}", user.getId());

        return itemRepository.findAllByOwnerOrderByCreatedAtDesc(user.getId());
    }

    @Override
    public ItemEntity getItem(String id) {
        log.info("Getting item {}", id);

        return findByIdOrThrow(id);
    }

    @Override
    public ItemEntity getItem(String id, UserData user) {
        log.info("Getting item {} as user {}", id, user.getId());

        return findByIdAndOwnerOrThrow(id, user.getId());
    }

    @Override
    public void changeSellerItemStatus(String id, UserData user, ItemStatus status) {
        log.info("Changing item {} status to {} as seller {}", id, status, user.getId());

        changeStatusInternal(id, user, status, sellerTransitions);
    }

    @Override
    public void changePublicItemStatus(String id, UserData user, ItemStatus status) {
        log.info("Changing item {} status to {} as public {}", id, status, user.getId());

        changeStatusInternal(id, user, status, publicTransitions);
    }

    @Override
    public List<ItemEntity> searchItems(SearchInfo search) {
        log.info("Searching items with info by {}", search);

        if (search.getFilter() == null) {
            search.setFilter(new FilterInfo());
        }
        search.getFilter().addConditionsItem(PUBLISHED_ITEMS_FILTER);

        String query = searchHelper.formQuery(search);

        return itemRepository.executeQuery(query);
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
    public List<ItemEntity> getBooked(UserData user) {
        log.info("Getting booked items as user {}", user.getId());

        return itemRepository.findAllBookedBy(user.getId());
    }

    private ItemEntity findByIdOrThrow(String itemId) {
        Optional<ItemEntity> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw Errors.itemNotFound(itemId);
        }
        return item.get();
    }

    private ItemEntity findByIdAndOwnerOrThrow(String itemId, String owner) {
        Optional<ItemEntity> item = itemRepository.findByIdAndOwner(itemId, owner);
        if (item.isEmpty()) {
            throw Errors.itemNotFound(itemId);
        }
        return item.get();
    }

    private void changeStatusInternal(
            String id,
            UserData user,
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
            transit.transit(user, item);

            item.setStatus(status);
            itemRepository.update(item);
        }));
    }

    private interface Transit {
        void transit(UserData user, ItemEntity item) throws TrousseauException; // must be called inside transaction
    }

    private Transit publishTransit() {
        return (user, item) -> {
            if (!Objects.equals(item.getOwner(), user.getId())) {
                throw Errors.itemNotFound(item.getId());
            }
            item.setPublishedAt(timeService.now().toInstant());
            eventService.push(EventType.ITEM_PUBLISHED, Map.of("itemId", item.getId()));
        };
    }

    private Transit unpublishTransit() {
        return (user, item) -> {
            if (!Objects.equals(item.getOwner(), user.getId())) {
                throw Errors.itemNotFound(item.getId());
            }
        };
    }

    private Transit bookTransit() {
        return (user, item) -> {
            List<BookingEntity> bookings = bookingRepository.findAllByUserId(user.getId());

            if (bookings.size() >= MAX_BOOKINGS_PER_USER) {
                throw Errors.maximumBookingsCountReached(MAX_BOOKINGS_PER_USER);
            }

            ZonedDateTime now = timeService.now();

            BookingEntity booking = new BookingEntity();
            booking.setId(itemIdFormatter.format(now));
            booking.setItemId(item.getId());
            booking.setUserId(user.getId());
            booking.setBookedAt(now.toInstant());

            bookingRepository.save(booking);
        };
    }

    private Transit unbookTransit() {
        return (user, item) -> {
            if (Objects.equals(item.getOwner(), user.getId())) {
                deleteBookingOrThrow(item);
                return;
            }

            if (bookingRepository.deleteByItemIdAndUserId(item.getId(), user.getId()) == 0) {
                throw Errors.bookingNotFound(item.getId());
            }
        };
    }

    private Transit closeTransit() {
        return (user, item) -> {
            if (!Objects.equals(item.getOwner(), user.getId())) {
                throw Errors.itemNotFound(item.getId());
            }

            deleteBookingOrThrow(item);
        };
    }

    private void deleteBookingOrThrow(ItemEntity item) {
        if (bookingRepository.deleteByItemId(item.getId()) == 0) {
            throw Errors.bookingNotFound(item.getId());
        }
    }

    private static void patchItem(ItemEntity item, ItemInfoRequest info) {
        item.setTitle(info.getTitle());
        item.setMediaIds(info.getMedia());
        item.setDescription(info.getDescription());
        item.setCategoryId(info.getCategory());

        item.setStatus(isFilledRequiredFields(item) ? READY : DRAFT);
    }

    private static boolean isFilledRequiredFields(ItemEntity item) {
        return !StringUtils.isBlank(item.getTitle())
                && !CollectionUtils.isEmpty(item.getMediaIds())
                && !StringUtils.isBlank(item.getDescription())
                && !StringUtils.isBlank(item.getCategoryId());
    }
}
