package ru.otus.hw.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.otus.hw.controllers.dto.BookViewDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.dto.BookDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BookController {
    private final BookService bookService;

    @GetMapping(value = "/api/v1/book/{id}")
    public ResponseEntity<BookDto> get(@PathVariable("id") long id) {
        try {
            BookDto book = bookService.findById(id);
            return new ResponseEntity<>(book, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/api/v1/book")
    public ResponseEntity<BookDto> update(@Valid @RequestBody BookViewDto book) {
        if (book.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id can't be null");
        }
        try {
            BookDto updated = bookService.update(book.getId(), book.getTitle(), book.getAuthorId(), book.getGenres());
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/api/v1/book")
    public ResponseEntity<BookDto> create(@Valid @RequestBody BookViewDto book) {
        try {
            BookDto created = bookService.create(book.getTitle(), book.getAuthorId(), book.getGenres());
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/api/v1/book")
    public ResponseEntity<List<BookDto>> getAll() {
        List<BookDto> books = bookService.findAll();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @DeleteMapping(value = "/api/v1/book/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        bookService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
