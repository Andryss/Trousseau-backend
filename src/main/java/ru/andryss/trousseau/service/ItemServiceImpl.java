package ru.andryss.trousseau.service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import ru.andryss.trousseau.exception.Errors;
import ru.andryss.trousseau.generated.model.ItemInfoRequest;
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

    private static final Map<ItemStatus, List<ItemStatus>> allowedTransitions = Map.of(
            ItemStatus.READY, List.of(ItemStatus.PUBLISHED),
            ItemStatus.PUBLISHED, List.of(ItemStatus.READY)
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
            ItemEntity item = itemRepository.findById(id);
            patchItem(item, info);

            return itemRepository.update(item);
        });
    }

    @Override
    public List<ItemEntity> getItems() {
        log.info("Getting items");

        return itemRepository.findAll();
    }

    @Override
    public ItemEntity getItem(String id) {
        log.info("Getting item {}", id);

        return itemRepository.findById(id);
    }

    @Override
    public void changeItemStatus(String id, ItemStatus status) {
        log.info("Changing item {} status to {}", id, status);

        transactionTemplate.executeWithoutResult((transactionStatus -> {
            ItemEntity item = itemRepository.findById(id);
            ItemStatus currentStatus = item.getStatus();

            if (!allowedTransitions.get(currentStatus).contains(status)) {
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
