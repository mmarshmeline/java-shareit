package ru.practicum.shareit.item.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.handler.NotFoundException;
import ru.practicum.shareit.handler.NotFoundUserForItemException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        try {
            userRepository.readUser(userId);
        } catch (NotFoundException e) {
            throw new NotFoundUserForItemException("Такого пользователя в приложении нет!");
        }
        return itemRepository.addItem(UserMapper.fromUserDto(userRepository.readUser(userId)), ItemMapper.fromItemDto(itemDto));
    }

    @Override
    public ItemDto editItem(Long ownerId, Long itemId, ItemDto itemDto) {
        return itemRepository.editItem(ownerId, itemId, itemDto);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return itemRepository.getItem(itemId);
    }

    @Override
    public List<ItemDto> readUsersItems(Long ownerId) {
        return itemRepository.readUsersItems(ownerId);
    }

    @Override
    public List<ItemDto> readFoundItems(String text) {
        return itemRepository.readFoundItems(text);
    }

}
