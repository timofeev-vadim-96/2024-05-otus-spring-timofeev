package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import ru.otus.hw.models.Book;
import ru.otus.hw.services.BookService;

import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
@RequiredArgsConstructor
@Command(group = "book-commands")
public class BookCommands {

    private final BookService bookService;

    @Command(description = "Find all books", command = "books", alias = "ab")
    public String findAllBooks() {
        return bookService.findAll().stream()
                .map(Book::toString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @Command(description = "Find book by id. Params: id", command = "bbid")
    public String findBookById(long id) {
        return bookService.findById(id)
                .map(Book::toString)
                .orElse("Book with id %d not found".formatted(id));
    }

    // bins newBook 1 1,6
    @Command(description = "Insert book. Params: title, authorId, {genresIds}", command = "bins")
    public String insertBook(String title, long authorId, Set<Long> genresIds) {
        var savedBook = bookService.insert(title, authorId, genresIds);
        return savedBook.toString();
    }

    // bupd 4 editedBook 3 2,5
    @Command(description = "Update book. Params: id, title, authorId, {genresIds}", command = "bupd")
    public String updateBook(long id, String title, long authorId, Set<Long> genresIds) {
        var updatedBook = bookService.update(id, title, authorId, genresIds);
        return updatedBook.toString();
    }

    // bdel 4
    @Command(description = "Delete book by id. Params: id", command = "bdel")
    public void deleteBook(long id) {
        bookService.deleteById(id);
    }
}
