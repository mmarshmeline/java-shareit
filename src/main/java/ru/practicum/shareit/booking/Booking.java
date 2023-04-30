package ru.practicum.shareit.booking;

/**
 * TODO Sprint add-bookings.
 */

import lombok.Data;
import lombok.Generated;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Table(name="bookings")
@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    @Column(name="start_time")
    private LocalDateTime start;
    @Column(name="end_time")
    private LocalDateTime end;

    @JoinColumn(name="items_ids")
    @ManyToOne(fetch=FetchType.LAZY)
    private Item item;

    @JoinColumn(name="owners_ids")
    @ManyToOne(fetch = FetchType.LAZY)
    private User booker;

    @Enumerated
    private Status status;
}
