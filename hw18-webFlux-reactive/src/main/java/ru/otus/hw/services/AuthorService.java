package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import ru.otus.hw.services.dto.AuthorDto;

public interface AuthorService {
    Flux<AuthorDto> findAll();
}
