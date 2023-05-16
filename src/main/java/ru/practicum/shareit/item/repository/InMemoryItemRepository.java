package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository {

    private volatile Long id = 1L;

    private final Map<Long, Item> itemsData = new HashMap<>();


    public synchronized Item save(Item item) {
        item.setId(id++);
        itemsData.put(item.getId(), item);
        return item;
    }

    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(itemsData.get(id));
    }

    public void updateById(Item item, Long id) {
        itemsData.put(id, item);
    }

    public List<Item> findByUserId(Long userId) {
        return itemsData.values()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Item> findByText(String text) {
        return itemsData.values()
                .stream()
                .filter(item -> item.getAvailable().equals(true) &&
                        (item.getDescription().toLowerCase().contains(text.toLowerCase()) ||
                                item.getName().toLowerCase().contains(text.toLowerCase()))
                )
                .collect(Collectors.toList());
    }
}
