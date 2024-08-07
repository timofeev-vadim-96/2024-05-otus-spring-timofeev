package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.dto.BookDto;

import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
@RequiredArgsConstructor
@Command(group = "book-commands")
public class BookCommands {
    private final BookService bookService;

    private final BookConverter bookConverter;

    @Command(description = "Find all books", command = "books", alias = "ab")
    public String findAllBooks() {
        return bookService.findAll().stream()
                .map(bookConverter::bookToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @Command(description = "Find book by id. Params: id", command = "bbid")
    public String findBookById(String id) {
        BookDto book = bookService.findById(id);
        return bookConverter.bookToString(book);
    }

    // bins newBook 1 1,6
    @Command(description = "Insert book. Params: title, authorId, {genresIds}", command = "bins")
    public String createBook(String title, String authorId, Set<String> genresIds) {
        var savedBook = bookService.create(title, authorId, genresIds);
        return bookConverter.bookToString(savedBook);
    }

    // bupd 4 editedBook 3 2,5
    @Command(description = "Update book. Params: id, title, authorId, {genresIds}", command = "bupd")
    public String updateBook(String id, String title, String authorId, Set<String> genresIds) {
        var savedBook = bookService.update(id, title, authorId, genresIds);
        return bookConverter.bookToString(savedBook);
    }

    // bdel 4
    @Command(description = "Delete book by id. Params: id", command = "bdel")
    public void deleteBook(String id) {
        bookService.deleteById(id);
    }
}