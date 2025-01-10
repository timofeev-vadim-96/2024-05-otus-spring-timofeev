package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.services.dto.CommentDto;

public interface CommentService {
    Flux<CommentDto> findAllByBookId(String bookId);

    Mono<CommentDto> create(String text, String bookId);

    Mono<CommentDto> update(String text, String id);

    Mono<Void> deleteById(String id);
}