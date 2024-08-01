package ru.otus.hw.services;

import ru.otus.hw.models.Book;
import ru.otus.hw.services.dto.BookDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookService {
    Optional<Book> findById(long id);

    List<BookDto> findAll();

    Book create(String title, long authorId, Set<Long> genresIds);

    BookDto update(long id, String title, long authorId, Set<Long> genresIds);

    void deleteById(long id);
}
