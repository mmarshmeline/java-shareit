package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStatusIsOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findAllByItemIdInOrderByStartDesc(Collection<Long> itemId);

    List<Booking> findAllByItemIdInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            Collection<Long> itemId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemIdInAndEndIsBeforeOrderByStartDesc(Collection<Long> itemId, LocalDateTime end);

    List<Booking> findAllByItemIdInAndStartIsAfterOrderByStartDesc(Collection<Long> itemId, LocalDateTime start);

    List<Booking> findAllByItemIdInAndStatusIsOrderByStartDesc(Collection<Long> itemId, Status status);

    Booking findFirstByItemIdAndStartBeforeOrderByStartDesc(Long idItem, LocalDateTime now);

    Booking findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime start, Status status);

    Boolean existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(
            Long itemId, Long bookerId, Status status, LocalDateTime end);

    //@Query("SELECT b.item.id, b FROM bookings b WHERE b.item.id IN :itemIds AND b.status = :status AND b.endTime > :now ORDER BY b.startTime ASC")
    //List<Object[]> findBookingsForItems(@Param("itemIds") List<Long> itemIds, @Param("now") LocalDateTime now, @Param("status") Status status);

}