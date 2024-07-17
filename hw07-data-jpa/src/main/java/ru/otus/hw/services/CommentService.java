package ru.otus.hw.services;

import ru.otus.hw.models.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> findAllByBookId(long bookId);

    Comment insert(String text, long bookId);

    Comment update(String text, long id);

    void deleteById(long id);
}
