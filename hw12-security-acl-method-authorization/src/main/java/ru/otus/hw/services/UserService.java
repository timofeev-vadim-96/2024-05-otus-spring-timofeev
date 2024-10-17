package ru.otus.hw.services;

import ru.otus.hw.services.dto.UserDto;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserDto findById(long id);

    UserDto findByUserName(String username);

    List<UserDto> findAll();

    UserDto create(String username, String password, Set<Long> authoritiesIds);

    UserDto update(long id, String username, String password, Set<Long> authoritiesIds);

    void deleteById(long id);
}
