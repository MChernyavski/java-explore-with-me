package ru.practicum.user.service;

import ru.practicum.user.dto.NewUserRequestDto;
import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(NewUserRequestDto newUserRequestDto);

    List<UserDto> getAllUsers(List<Long> ids, int from, int size);

    void deleteUser(long userId);
}
