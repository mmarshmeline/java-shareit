package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;
import lombok.Generated;

import javax.persistence.*;

@Data
@Builder
@Table(name="users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="email")
    private String email;
}
