package ru.practicum.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.user.model.User;
import ru.practicum.user.dto.NewUserRequestDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static UserShortDto toUserShorDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static User toUser(NewUserRequestDto newUserRequestDto) {
        return User.builder()
                .name(newUserRequestDto.getName())
                .email(newUserRequestDto.getEmail())
                .build();
    }
}
