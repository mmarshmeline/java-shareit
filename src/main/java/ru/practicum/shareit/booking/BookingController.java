package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingListDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingDtoResponse> addBooking(@RequestHeader("X-Sharer-User-Id") @Min(1) Long bookerId,
                                                         @Valid @RequestBody BookingDto bookingDto) {
        log.debug("Пользователем с id " + bookerId + " создано новое бронирование...");
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.addBooking(bookerId, bookingDto));
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<BookingDtoResponse> setApprove(@RequestHeader("X-Sharer-User-Id") @Min(1) Long ownerId,
                                                         @RequestParam String approved,
                                                         @PathVariable @Min(1) Long bookingId) {
        log.debug("Владельцем товара подтверждено бронирование с id " + bookingId + "...");
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.setApprove(ownerId, bookingId, approved));
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<BookingDtoResponse> readBooking(@PathVariable @Min(1) Long bookingId,
                                                          @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId) {
        log.debug("Получаем инфо о бронировании...");
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookingService.readBooking(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<BookingListDto> readUsersBooking(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                           @RequestParam(defaultValue = "ALL") String state) {
        log.debug("Предоставляем инфо о бронированиях пользователя с id " + userId + "...");
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.readUsersBookings(userId, state));
    }

   /*@GetMapping
    public ResponseEntity<BookingListDto> getAllBookingsForUser(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                                @RequestParam(defaultValue = "ALL") String state) {
        log.debug("Предоставляем инфо о бронированиях пользователя...");
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getAllBookingsForUser(userId, state));
    }*/


    @GetMapping("owner")
    public ResponseEntity<BookingListDto> readOwnersBooking(
            @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId, @RequestParam(defaultValue = "ALL") String state) {
        log.debug("Предоставляем инфо о бронированиях, созданных для товаров пользователя с id " + userId + "...");
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.readOwnersBooking(userId, state));
    }
}
