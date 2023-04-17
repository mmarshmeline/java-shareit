package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

@Repository
public interface ItemRepository {
    ItemDto addItem(User owner, Item item);
    ItemDto editItem(Long ownerId, Long itemId, ItemDto itemDto);
    ItemDto getItem(Long itemId);
    List<ItemDto> readUsersItems(Long ownerId);
    List<ItemDto> readFoundItems(String text);
}
