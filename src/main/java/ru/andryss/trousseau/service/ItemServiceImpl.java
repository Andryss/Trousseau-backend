package ru.andryss.trousseau.service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import ru.andryss.trousseau.exception.Errors;
import ru.andryss.trousseau.generated.model.ItemInfoRequest;
import ru.andryss.trousseau.generated.model.SearchInfo;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.model.ItemStatus;
import ru.andryss.trousseau.repository.ItemRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final TimeService timeService;
    private final TransactionTemplate transactionTemplate;

    private final DateTimeFormatter itemIdFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");

    private final List<ItemStatus> ITEM_EDITABLE_STATUSES = List.of(
            ItemStatus.DRAFT, ItemStatus.READY
    );

    private final List<ItemStatus> ITEM_READABLE_PUBLIC_STATUSES = List.of(
            ItemStatus.PUBLISHED, ItemStatus.BOOKED, ItemStatus.ARCHIVED
    );

    private static final Map<ItemStatus, List<ItemStatus>> allowedSellerTransitions = Map.of(
            ItemStatus.READY, List.of(ItemStatus.PUBLISHED),
            ItemStatus.PUBLISHED, List.of(ItemStatus.READY),
            ItemStatus.BOOKED, List.of(ItemStatus.PUBLISHED, ItemStatus.ARCHIVED)
    );

    private static final Map<ItemStatus, List<ItemStatus>> allowedPublicTransitions = Map.of(
            ItemStatus.PUBLISHED, List.of(ItemStatus.BOOKED),
            ItemStatus.BOOKED, List.of(ItemStatus.PUBLISHED)
    );

    @Override
    public ItemEntity createItem(ItemInfoRequest info) {
        log.info("Creating item title={}, mediaIds={}, description={}", info.getTitle(), info.getMedia(),
                info.getDescription());

        ItemEntity item = new ItemEntity();
        patchItem(item, info);

        item.setId(itemIdFormatter.format(timeService.now()));
        item.setCreatedAt(Instant.now());

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

        changeStatusInternal(id, status, allowedSellerTransitions);
    }

    @Override
    public void changePublicItemStatus(String id, ItemStatus status) {
        log.info("Changing item {} status to {} as public", id, status);

        changeStatusInternal(id, status, allowedPublicTransitions);
    }

    @Override
    public List<ItemEntity> searchItems(SearchInfo search) {
        log.info("Searching items with info by {}", search);

        return itemRepository.findAllByStatusOrderByCreatedAtDesc(ItemStatus.PUBLISHED);
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

        return itemRepository.findAllByStatusOrderByCreatedAtDesc(ItemStatus.BOOKED);
    }

    private ItemEntity findByIdOrThrow(String itemId) {
        Optional<ItemEntity> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw Errors.itemNotFound(itemId);
        }
        return item.get();
    }

    private void changeStatusInternal(String id, ItemStatus status, Map<ItemStatus, List<ItemStatus>> transitions) {
        transactionTemplate.executeWithoutResult((transactionStatus -> {
            ItemEntity item = findByIdOrThrow(id);
            ItemStatus currentStatus = item.getStatus();

            if (!transitions.get(currentStatus).contains(status)) {
                throw Errors.illegalItemStatusTransition(currentStatus, status);
            }

            item.setStatus(status);
            itemRepository.update(item);
        }));
    }

    private static void patchItem(ItemEntity item, ItemInfoRequest info) {
        item.setTitle(info.getTitle());
        item.setMediaIds(info.getMedia());
        item.setDescription(info.getDescription());

        item.setStatus(isFilledRequiredFields(item) ? ItemStatus.READY : ItemStatus.DRAFT);
    }

    private static boolean isFilledRequiredFields(ItemEntity item) {
        return !StringUtils.isBlank(item.getTitle())
                && !CollectionUtils.isEmpty(item.getMediaIds())
                && !StringUtils.isBlank(item.getDescription());
    }
}
