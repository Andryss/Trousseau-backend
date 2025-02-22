package ru.andryss.trousseau.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.repository.ItemRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public ItemEntity createItem(String title, List<String> mediaIds, String description) {
        log.info("Creating item title={}, mediaIds={}, description={}", title, mediaIds, description);

        ItemEntity item = new ItemEntity();
        item.setTitle(title);
        item.setMediaIds(mediaIds);
        item.setDescription(description);

        return itemRepository.save(item);
    }
}
