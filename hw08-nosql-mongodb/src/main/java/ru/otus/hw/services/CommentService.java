package ru.otus.hw.services;

import ru.otus.hw.models.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> findAllByBookId(String bookId);

    Comment insert(String text, String bookId);

    Comment update(String text, String id);

    void deleteById(String id);
}
