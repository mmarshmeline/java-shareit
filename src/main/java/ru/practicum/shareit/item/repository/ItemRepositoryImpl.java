package ru.practicum.shareit.item.repository;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.handler.NotFoundException;
import ru.practicum.shareit.handler.NotFoundUserForItemException;
import ru.practicum.shareit.handler.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.*;

@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 1L;

    @Override
    public ItemDto addItem(User owner, Item item) {
        validateItem(item);
        item.setOwner(owner);
        item.setId(id);
        item.setAvailable(true);
        items.put(id++, item);
        log.info("Товар успешно добавлен!");
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto editItem(Long ownerId, Long itemId, ItemDto itemDto) {
        if (!items.containsKey(itemId)) {
            log.warn("Что-то пошло не так!");
            throw new NotFoundException("Такого товара в приложении нет!");
        }
        if (!items.get(itemId).getOwner().getId().equals(ownerId)) {
            log.warn("Что-то пошло не так!");
            throw new NotFoundUserForItemException("Редактировать товар может только его владелец!");
        }
        if (itemDto.getName() != null) {
            log.info("Изменено имя товара " + itemDto.getName());
            items.get(itemId).setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            log.info("Изменено описание товара " + itemDto.getName());
            items.get(itemId).setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            log.info("Статус доступа к аренде товара " + itemDto.getName() + " изменен на " + itemDto.getAvailable());
            items.get(itemId).setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public ItemDto getItem(Long itemId) {
        if (!items.containsKey(itemId)) {
            log.warn("Что-то пошло не так!");
            throw new NotFoundException("Такого товара в приложении нет!");
        }
        log.info("По id " + itemId + " был найден товар " + items.get(itemId).getName());
        return ItemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public List<ItemDto> readUsersItems(Long ownerId) {
        List<ItemDto> ownersItemsList = new ArrayList<>();
        for (Item element : items.values()) {
            if (Objects.equals(element.getOwner().getId(), ownerId)) {
                ownersItemsList.add(ItemMapper.toItemDto(element));
            }
        }
        log.info("Пользователь с id " + ownerId + " разместил следующие товары: " + ownersItemsList);
        return ownersItemsList;
    }

    @Override
    public List<ItemDto> readFoundItems(String text) {
        List<ItemDto> foundItemsList = new ArrayList<>();
        if (text.isBlank()) {
            return foundItemsList;
        }
        text = text.toLowerCase();
        for (Item element : items.values()) {
            if ((element.getName().toLowerCase().contains(text) || element.getDescription().toLowerCase().contains(text)) && element.getAvailable()) {
                foundItemsList.add(ItemMapper.toItemDto(element));
            }
        }
        log.info("По вашему запросу " + text + " были найдены следующие вещи: " + foundItemsList);
        return foundItemsList;
    }

    private void validateItem(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            log.warn("Что-то пошло не так!");
            throw new ValidationException("Имя товара не может быть пустым!");
        }
        if (item.getDescription() == null) {
            log.warn("Что-то пошло не так!");
            throw new ValidationException("Описание товара не может быть пустым!");
        }
        if (item.getAvailable() == null) {
            log.warn("Что-то пошло не так!");
            throw new ValidationException("Задайте статус аренды.");
        }
    }
}
