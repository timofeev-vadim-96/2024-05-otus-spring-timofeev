package ru.otus.hw.services;

import ru.otus.hw.services.dto.CommentDto;

import java.util.List;

public interface CommentService {
    List<CommentDto> findAllByBookId(long bookId);

    CommentDto create(String text, long bookId);

    CommentDto update(String text, long id);

    void deleteById(long id);
}