package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select i from Item i where i.owner.id = ?1")
    List<Item> getAllItemsByUser(long userId);

    @Query("select i from Item i where i.available=true and (lower(i.name) like concat('%',lower(?1),'%') " +
            "or lower(i.description) like concat('%',lower(?1),'%'))")
    List<Item> searchItemsByText(String text);
}
