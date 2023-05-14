package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemDtoResponse addItem(ItemDto item, Long userId) throws ResponseStatusException {
        Item newItem = itemMapper.mapToItemFromItemDto(item);
        newItem.setOwner(userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Такого пользователя в приложении не существует.")));
        log.info("Пользователь с id " + userId + " добавил новый товар.");
        return itemMapper.mapToItemDtoResponse(itemRepository.save(newItem));
    }

    @Override
    @Transactional
    public ItemDtoResponse updateItem(Long itemId, Long ownerId, ItemDtoUpdate itemDtoUpdate) {
        Item updateItem = itemRepository.findById(itemId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Такого товара в приложении не существует."));
        if (!updateItem.getOwner().getId().equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Товар с id " + itemId + " пользователю с id " + ownerId + " не принадлежит. " +
                            "Редактировать товар может только его владелец!");
        }
        log.info("Редактируем инфо о товаре...");
        return itemMapper.mapToItemDtoResponse(itemRepository.save(itemMapper.mapToItemFromItemDtoUpdate(itemDtoUpdate, updateItem)));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDtoResponse getItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Такого товара в приложении не существует."));
        ItemDtoResponse itemDtoResponse = itemMapper.mapToItemDtoResponse(item);
        if (item.getOwner().getId().equals(userId)) {
            itemDtoResponse.setLastBooking(itemMapper
                    .mapToBookingShortDto(bookingRepository
                            .findFirstByItemIdAndStartBeforeOrderByStartDesc(
                                    itemId, LocalDateTime.now())
                    ));
            itemDtoResponse.setNextBooking(itemMapper.mapToBookingShortDto(bookingRepository
                    .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                            itemId, LocalDateTime.now(), Status.APPROVED)
            ));
            log.info("Предоставляем инфо о запрошенном вами товаре с id " + itemId + "...");
            return itemDtoResponse;
        }
        log.info("Предоставляем инфо о запрошенном вами товаре с id " + itemId + "...");
        return itemDtoResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemListDto readUsersItems(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Такого пользователя в приложении не существует.");
        }
        List<Item> ownersItemList = itemRepository.findAllByOwnerId(ownerId);
        List<Booking> bookingList = bookingRepository.findAll();
        List<ItemDtoResponse> itemDtoList = new ArrayList<>();
        for (Item element : ownersItemList) {
            if (!bookingList.isEmpty()) {
                Booking lastBooking = bookingList.stream().filter(a -> a.getStart().isBefore(LocalDateTime.now())).min((a, b) -> b.getStart().compareTo(a.getStart())).get();
                Booking nextBooking = bookingList.stream().filter(a -> a.getStart().isAfter(LocalDateTime.now())).min((a, b) -> a.getStart().compareTo(b.getStart())).get();
                ItemDtoResponse itemDtoResponse = itemMapper.mapToItemDtoResponse(element);
                itemDtoResponse.setLastBooking(itemMapper.mapToBookingShortDto(lastBooking));
                itemDtoResponse.setNextBooking(itemMapper.mapToBookingShortDto(nextBooking));
                itemDtoList.add(itemDtoResponse);
            } else {
                ItemDtoResponse itemDtoResponse = itemMapper.mapToItemDtoResponse(element);
                itemDtoResponse.setLastBooking(itemMapper.mapToBookingShortDto(null));
                itemDtoResponse.setNextBooking(itemMapper.mapToBookingShortDto(null));
                itemDtoList.add(itemDtoResponse);
            }
        }
        /*List<ItemDtoResponse> ownersItemsList = itemRepository.findAllByOwnerId(ownerId).stream()
                .map(itemMapper::mapToItemDtoResponse).collect(Collectors.toList());
        for (ItemDtoResponse item : ownersItemsList) {
            item.setLastBooking(itemMapper.mapToBookingShortDto(bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(
                    item.getId(), LocalDateTime.now())));
            item.setNextBooking(itemMapper.mapToBookingShortDto(bookingRepository
                    .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                            item.getId(), LocalDateTime.now(), Status.APPROVED)
            ));
        }*/
        log.info("Предоставляем инфо о товарах пользователя с id " + ownerId + "...");
        //return ItemListDto.builder().items(ownersItemsList).build();
        return ItemListDto.builder().items(itemDtoList).build();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemListDto readFoundItems(String text) {
        if (text.isBlank()) {
            log.info("Запрос пуст. Попробуйте задать поисковой запрос.");
            return ItemListDto.builder().items(new ArrayList<>()).build();
        }
        log.info("Предоставляем инфо о найденных товарах по запросу " + text + "...");
        return ItemListDto.builder()
                .items(itemRepository.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text).stream()
                        .map(itemMapper::mapToItemDtoResponse).collect(Collectors.toList())).build();
    }

    @Override
    @Transactional
    public CommentDtoResponse addComment(Long itemId, Long userId, CommentDto commentDto) {
        if (!bookingRepository.existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(itemId, userId,
                Status.APPROVED, LocalDateTime.now())) {
            log.error("Пользователь с id " + userId + " пока ни разу не бронировал товар с id " + itemId + ".");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пользователь с id "
                    + userId + " пока ни разу не бронировал товар с id " + itemId + ".");
        } else {
            User author = userRepository.findById(userId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Такого пользователя в приложении не существует."));
            Item item = itemRepository.findById(itemId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Такого товара в приложении не существует."));
            Comment comment = itemMapper.mapToCommentFromCommentDto(commentDto);
            comment.setItem(item);
            comment.setAuthor(author);
            comment.setCreated(LocalDateTime.now());
            log.info("Пользователь с id " + userId + " оставил отзыв к товару с id " + itemId + ".");
            return itemMapper.mapToCommentDtoResponseFromComment(commentRepository.save(comment));
        }
    }
}