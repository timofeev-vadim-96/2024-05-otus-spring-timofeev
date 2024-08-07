package ru.otus.hw.services;

import ru.otus.hw.services.dto.CommentDto;

import java.util.List;

public interface CommentService {
    List<CommentDto> findAllByBookId(String bookId);

    CommentDto create(String text, String bookId);

    CommentDto update(String text, String id);

    void deleteById(String id);
}
