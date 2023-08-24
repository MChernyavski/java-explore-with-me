package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.exception.NameConflictException;
import ru.practicum.user.model.User;
import ru.practicum.user.dto.NewUserRequestDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(NewUserRequestDto newUserRequestDto) {
        if (userRepository.existsByName(newUserRequestDto.getName())) {
            log.warn("Can't create user with name: {} , the name was used by another user", newUserRequestDto.getName());
            throw new NameConflictException("Can't create user with this name: the name was used by another user");
        }
        User user = UserMapper.toUser(newUserRequestDto);
        user = userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers(List<Long> ids, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(pageRequest).stream().map(UserMapper::toUserDto).collect(Collectors.toList());
        } else {
            return userRepository.findAllById(ids).stream().map(UserMapper::toUserDto).collect(Collectors.toList());
        }
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }
}
