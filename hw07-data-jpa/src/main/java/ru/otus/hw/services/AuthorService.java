package ru.otus.hw.services;

import ru.otus.hw.services.dto.AuthorDto;

import java.util.List;

public interface AuthorService {
    List<AuthorDto> findAll();
}
