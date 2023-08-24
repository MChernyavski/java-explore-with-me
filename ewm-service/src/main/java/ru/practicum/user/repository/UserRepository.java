package ru.practicum.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByName(String name);
}
