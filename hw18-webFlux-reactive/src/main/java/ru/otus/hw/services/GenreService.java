package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import ru.otus.hw.services.dto.GenreDto;

import java.util.Set;

public interface GenreService {
    Flux<GenreDto> findAll();

    Flux<GenreDto> findAllByIds(Set<String> ids);
}