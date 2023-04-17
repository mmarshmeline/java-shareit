package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UserMapper {
    private UserMapper() {
    }

    public static UserDto toUserDto(User user) {

        if (user == null) {
            return null; //TODO пробросить исключение
        }

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User fromUserDto(UserDto userDto) {

        if (userDto == null) {
            return null; //TODO пробросить исключение
        }

        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static List<UserDto> usersToUserDto(Collection<User> users) {
        List<UserDto> usersDtoList = new ArrayList<>();
        for (User element : users) {
            usersDtoList.add(toUserDto(element));
        }
        return usersDtoList;
    }
}
