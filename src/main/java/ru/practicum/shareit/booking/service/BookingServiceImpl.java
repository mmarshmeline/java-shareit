package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingListDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.handler.exception.StateException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;


    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository, BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingMapper = bookingMapper;
    }

    /*1 - Добавление нового запроса на бронирование. Запрос может быть создан любым пользователем, а затем
    подтверждён владельцем вещи. После создания запрос находится в статусе WAITING — «ожидает подтверждения».
    Эндпоинт — POST /bookings */
    @Override
    @Transactional
    public BookingDtoResponse addBooking(Long bookerId, BookingDto bookingDto) {
        if (bookingDto.getEnd().equals(bookingDto.getStart()) || bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Дата окончания бронирования не может быть раньше даты начала бронирования.");
        }
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Такого товара в приложении не существует."));
        if (!item.getOwner().getId().equals(bookerId)) {
            if (item.getAvailable()) {
                User user = userRepository.findById(bookerId).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Такого пользователя в приложении не существует."));
                Booking booking = bookingMapper.mapToBookingFromBookingDto(bookingDto);
                booking.setItem(item);
                booking.setBooker(user);
                log.info("Пользователем с id " + bookerId + " создано новое бронирование...");
                return bookingMapper.mapToBookingDtoResponse(bookingRepository.save(booking));
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Товар с id " + item.getId()
                        + " недоступен для бронирования.");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Владелец не может бронировать свою же вещь.");
        }
    }

    /*2 - Подтверждение или отклонение запроса на бронирование. Может быть выполнено только владельцем вещи. Затем
    статус бронирования становится либо APPROVED, либо REJECTED.
    Эндпоинт — PATCH /bookings/{bookingId}?approved={approved} */
    @Override
    @Transactional
    public BookingDtoResponse setApprove(Long ownerId, Long bookingId, String approved) {
        String approve = approved.toLowerCase();
        if (!(approve.equals("true") || approve.equals("false"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неккоректный параметр строки approved.");
        }
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Такого бронирования в приложении нет."));
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Невозможно изменить статус бронирования со статусом " + booking.getStatus());
        }
        if (booking.getItem().getOwner().getId().equals(ownerId)) {
            if (approve.equals("true")) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
            log.info("Владельцем товара подтверждено бронирование с id " + bookingId + "...");
            return bookingMapper.mapToBookingDtoResponse(bookingRepository.save(booking));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Пользователь с id " + ownerId + " не является владельцем вещи с id " + booking.getItem().getOwner().getId());
        }
    }

    /*3 - Получение данных о конкретном бронировании (включая его статус). Может быть выполнено либо автором
    бронирования, либо владельцем вещи, к которой относится бронирование.
    Эндпоинт — GET /bookings/{bookingId} */
    @Override
    @Transactional(readOnly = true)
    public BookingDtoResponse readBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Бронирования с id " + bookingId + " не существует."));
        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id " + userId
                    + " не является автором бронирования или владельцем вещи, к которой относится бронирование.");
        }
        log.debug("Получаем инфо о бронировании...");
        return bookingMapper.mapToBookingDtoResponse(booking);
    }

    /*4 - Получение списка всех бронирований текущего пользователя. Бронирования должны возвращаться
    отсортированными по дате от более новых к более старым.
    Эндпоинт — GET /bookings?state={state}*/
    @Override
    @Transactional(readOnly = true)
    public BookingListDto readUsersBookings(Long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Такого пользователя в приложении не существует.");
        }
        log.debug("Предоставляем инфо о бронированиях пользователя с id " + userId + "...");
        return readBookingList(state, userId, false);
    }


    /* 5 - Получение списка бронирований для всех вещей текущего пользователя. Этот запрос имеет смысл для
    владельца хотя бы одной вещи. Работа параметра state аналогична его работе в предыдущем сценарии.
    Эндпоинт — GET /bookings/owner?state={state}*/
    @Override
    @Transactional(readOnly = true)
    public BookingListDto readOwnersBooking(Long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Такого пользователя в приложении не существует.");
        }
        if (!itemRepository.existsItemByOwnerId(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Пользователь с id " + userId + " ещё не добавлял товары.");
        } else {
            log.debug("Предоставляем инфо о бронированиях, созданных для товаров пользователя с id " + userId + "...");
            return readBookingList(state, userId, true);
        }

    }

    private BookingListDto readBookingList(String state, Long userId, Boolean isOwner) {
        List<Long> itemsId;
        switch (State.fromValue(state.toUpperCase())) {
            case ALL:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    log.debug("Получаем инфо обо всех бронированиях пользователя с id " + userId + "...");
                    return BookingListDto.builder()
                            .bookings(bookingRepository.findAllByItemIdInOrderByStartDesc(itemsId).stream()
                                    .map(bookingMapper::mapToBookingDtoResponse).collect(Collectors.toList())).build();
                } else {
                    log.debug("Получаем инфо обо всех бронированиях,созданных для товаров пользователя с id " + userId + "...");
                    return BookingListDto.builder()
                            .bookings(bookingRepository.findAllByBookerIdOrderByStartDesc(userId).stream()
                                    .map(bookingMapper::mapToBookingDtoResponse).collect(Collectors.toList())).build();
                }
            case CURRENT:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    log.debug("Получаем инфо о текущих бронированиях пользователя с id " + userId + "...");
                    return BookingListDto.builder().bookings(
                            bookingRepository.findAllByItemIdInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                                            itemsId, LocalDateTime.now(), LocalDateTime.now()).stream()
                                    .map(bookingMapper::mapToBookingDtoResponse).collect(Collectors.toList())).build();
                } else {
                    log.debug("Получаем инфо о текущих бронированиях,созданных для товаров пользователя с id " + userId + "...");
                    return BookingListDto.builder().bookings(
                            bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                                            userId, LocalDateTime.now(), LocalDateTime.now()).stream()
                                    .map(bookingMapper::mapToBookingDtoResponse).collect(Collectors.toList())).build();
                }
            case PAST:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    log.debug("Получаем инфо о прошедших бронированиях пользователя с id " + userId + "...");
                    return BookingListDto.builder()
                            .bookings(bookingRepository
                                    .findAllByItemIdInAndEndIsBeforeOrderByStartDesc(itemsId, LocalDateTime.now())
                                    .stream().map(bookingMapper::mapToBookingDtoResponse).collect(Collectors.toList()))
                            .build();
                } else {
                    log.debug("Получаем инфо о прошедших бронированиях,созданных для товаров пользователя с id " + userId + "...");
                    return BookingListDto.builder()
                            .bookings(bookingRepository
                                    .findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now())
                                    .stream().map(bookingMapper::mapToBookingDtoResponse).collect(Collectors.toList()))
                            .build();
                }
            case FUTURE:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    log.debug("Получаем инфо о предстоящих бронированиях пользователя с id " + userId + "...");
                    return BookingListDto.builder()
                            .bookings(bookingRepository
                                    .findAllByItemIdInAndStartIsAfterOrderByStartDesc(itemsId, LocalDateTime.now())
                                    .stream().map(bookingMapper::mapToBookingDtoResponse).collect(Collectors.toList()))
                            .build();
                } else {
                    log.debug("Получаем инфо о предстоящих бронированиях,созданных для товаров пользователя с id " + userId + "...");
                    return BookingListDto.builder()
                            .bookings(bookingRepository
                                    .findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now())
                                    .stream().map(bookingMapper::mapToBookingDtoResponse).collect(Collectors.toList()))
                            .build();
                }
            case WAITING:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    log.debug("Получаем инфо о бронированиях пользователя с id " + userId + ", ожидающих подтверждения...");
                    return BookingListDto.builder()
                            .bookings(bookingRepository
                                    .findAllByItemIdInAndStatusIsOrderByStartDesc(itemsId, Status.WAITING)
                                    .stream().map(bookingMapper::mapToBookingDtoResponse).collect(Collectors.toList()))
                            .build();
                } else {
                    log.debug("Получаем инфо о бронированиях, созданных для товаров пользователя с id " + userId +
                            " и ожидающих подтверждения...");
                    return BookingListDto.builder()
                            .bookings(bookingRepository
                                    .findAllByBookerIdAndStatusIsOrderByStartDesc(userId, Status.WAITING)
                                    .stream().map(bookingMapper::mapToBookingDtoResponse).collect(Collectors.toList()))
                            .build();
                }
            case REJECTED:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    log.debug("Получаем инфо об отклоненных бронированиях пользователя с id " + userId + "...");
                    return BookingListDto.builder()
                            .bookings(bookingRepository
                                    .findAllByItemIdInAndStatusIsOrderByStartDesc(itemsId, Status.REJECTED)
                                    .stream().map(bookingMapper::mapToBookingDtoResponse).collect(Collectors.toList()))
                            .build();
                } else {
                    log.debug("Получаем инфо об отклоненных бронированиях,созданных для товаров пользователя с id " + userId + "...");
                    return BookingListDto.builder()
                            .bookings(bookingRepository
                                    .findAllByBookerIdAndStatusIsOrderByStartDesc(userId, Status.REJECTED)
                                    .stream().map(bookingMapper::mapToBookingDtoResponse).collect(Collectors.toList()))
                            .build();
                }
            default:
                throw new StateException("Unknown state: " + state);
        }
    }
}
