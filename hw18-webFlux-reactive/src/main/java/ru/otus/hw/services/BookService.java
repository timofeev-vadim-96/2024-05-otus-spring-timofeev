package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.services.dto.BookDto;

import java.util.List;
import java.util.Set;

public interface BookService {
    Mono<BookDto> findById(String id);

    Flux<BookDto> findAll();

    Mono<BookDto> create(String title, String authorId, Set<String> genresIds);

    Mono<BookDto> update(String id, String title, String authorId, Set<String> genresIds);

    Mono<Void> deleteById(String id);
}