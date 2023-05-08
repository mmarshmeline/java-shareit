package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingListDto;


public interface BookingService {

    BookingDtoResponse addBooking(Long bookerId, BookingDto bookingDto);

    BookingDtoResponse setApprove(Long ownerId, Long bookingId, String approved);

    BookingDtoResponse readBooking(Long bookingId, Long userId);

    BookingListDto readUsersBookings(Long userId, String state);

    BookingListDto readOwnersBooking(Long userId, String state);
}
