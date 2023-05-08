package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;


public interface ItemService {
    ItemDtoResponse addItem(ItemDto item, Long userId);

    ItemDtoResponse updateItem(Long itemId, Long ownerId, ItemDtoUpdate itemDtoUpdate);
    ItemDtoResponse getItem(Long userId, Long itemId);

    ItemListDto readUsersItems(Long ownerId);

    ItemListDto readFoundItems(String text);

    CommentDtoResponse addComment(Long itemId, Long userId, CommentDto commentDto);
}
