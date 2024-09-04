package ru.otus.hw.services;

import ru.otus.hw.services.dto.UserDto;
import ru.otus.hw.util.Authority;

import java.util.List;

public interface UserService {
    UserDto findById(long id);

    List<UserDto> findAll();

    UserDto create(String username, String password, Authority authority);

    UserDto update(long id, String username, String password, Authority authority);

    void deleteById(long id);
}
