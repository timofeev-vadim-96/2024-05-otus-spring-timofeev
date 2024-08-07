package ru.otus.hw.services;

import ru.otus.hw.services.dto.BookDto;

import java.util.List;
import java.util.Set;

public interface BookService {
    BookDto findById(String id);

    List<BookDto> findAll();

    BookDto create(String title, String authorId, Set<String> genresIds);

    BookDto update(String id, String title, String authorId, Set<String> genresIds);

    void deleteById(String id);
}
