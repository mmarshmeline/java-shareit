package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import lombok.Generated;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Data
@Builder
@Table(name="items")
@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    @Column(name="name", nullable = false)
    private String name;
    @Column(name="description", nullable = false)
    private String description;
    @Column(name="items_available")
    private Boolean available;
    @JoinColumn(name="owner_ids")
    private User owner;

    //TODO перепроверить - нужно ли оставить JoinColumn или нужно прописать OneToMany или ManyToOne
    private ItemRequest request;
}
