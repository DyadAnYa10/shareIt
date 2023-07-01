package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(long userId, Pageable pageable);

    List<Item> findAllByOwnerId(long userId);

    @Query("select i from Item as i where i.available=true and (lower(i.name) like lower(concat('%', ?1,'%')) " +
            "or lower(i.description) like lower(concat('%',?1,'%')))")
    List<Item> searchItemsByText(String text, PageRequest pageRequest);

    @Query("select it from Item as it where it.itemRequest in ?1")
    List<Item> findAllByRequestIdIn(List<ItemRequest> requests);

    List<Item> findAllByItemRequest(ItemRequest itemRequest);
}
