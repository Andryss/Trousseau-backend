package ru.andryss.trousseau.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.repository.ItemRepository;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public void createItem(String title, List<String> mediaIds, String description) {
        ItemEntity item = new ItemEntity();
        item.setTitle(title);
        item.setMediaIds(mediaIds);
        item.setDescription(description);

        itemRepository.save(item);
    }
}
