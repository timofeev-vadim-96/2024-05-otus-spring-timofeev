package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Сервис для работы с книгами")
@DataMongoTest
@Import({BookServiceImpl.class})
@Transactional(propagation = Propagation.NEVER)
class BookServiceImplTest {
    private static final long BOOK_LIST_MIN_SIZE = 2;

    @Autowired
    private BookServiceImpl bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void findById() {
        Book expected = bookRepository.findAll().get(0);

        Optional<Book> actual = bookService.findById(expected.getId());

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    void findByIdNegative() {
        String notExpectedId = "notExpectedId";

        Optional<Book> actual = bookService.findById(notExpectedId);

        assertTrue(actual.isEmpty());
    }

    @Test
    void findAll() {
        List<Book> actualBooks = bookService.findAll();

        assertFalse(actualBooks.isEmpty());
        assertTrue(actualBooks.size() >= BOOK_LIST_MIN_SIZE);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void create() {
        Author expectedAuthor = authorRepository.findAll().get(0);
        String expectedTitle = "titleToInsert";
        Set<Genre> expectedGenres = new HashSet<>(genreRepository.findAll());
        Book actual = bookService.create(expectedTitle, expectedAuthor.getId(),
                expectedGenres.stream()
                        .map(Genre::getId)
                        .collect(Collectors.toSet()));

        Optional<Book> insertedBook = bookRepository.findById(actual.getId());

        assertTrue(insertedBook.isPresent());
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("genres")
                .isEqualTo(insertedBook.get());
        assertThat(insertedBook.get().getGenres()).containsAll(expectedGenres);
    }

    @Test
    void insertNegative() {
        Author expectedAuthor = authorRepository.findAll().get(0);
        List<Genre> genres = genreRepository.findAll().stream().limit(2).toList();

        assertThrows(
                IllegalArgumentException.class,
                () -> bookService.create("someTitle", expectedAuthor.getId(), Set.of()));
        assertThrows(
                EntityNotFoundException.class,
                () -> bookService.create(
                        "someTitle",
                        "unexpectedAuthorId",
                        Set.of(genres.get(0).getId(), genres.get(1).getId())));
        assertThrows(
                EntityNotFoundException.class,
                () -> bookService.create(
                        "someTitle",
                        expectedAuthor.getId(),
                        Set.of(genres.get(0).getId(), "unexpectedGenreId")));
    }

    @Test
    void updateNegative() {
        Author expectedAuthor = authorRepository.findAll().get(0);
        List<Genre> genres = genreRepository.findAll().stream().limit(2).toList();
        Book book = bookRepository.findAll().get(0);

        assertThrows(
                EntityNotFoundException.class,
                () -> bookService.update("unexpectedBookId", "someTitle", expectedAuthor.getId(),
                        Set.of(genres.get(0).getId(), genres.get(1).getId())));
        assertThrows(
                IllegalArgumentException.class,
                () -> bookService.update(
                        book.getId(),
                        "someTitle",
                        expectedAuthor.getId(),
                        Set.of()));
        assertThrows(
                EntityNotFoundException.class,
                () -> bookService.update(
                        book.getId(),
                        "someTitle",
                        "unexpectedAuthorId",
                        Set.of(genres.get(0).getId(), genres.get(1).getId())));
        assertThrows(
                EntityNotFoundException.class,
                () -> bookService.update(
                        book.getId(),
                        "someTitle",
                        expectedAuthor.getId(),
                        Set.of(genres.get(0).getId(), "unexpectedGenreId")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void update() {
        String expectedTitle = "titleToUpdate";
        Author expectedAuthor = authorRepository.findAll().get(0);
        Set<Genre> expectedGenres = genreRepository.findAll()
                .stream()
                .limit(2)
                .collect(Collectors.toSet());
        Book oldConditionBook = bookRepository.findAll().get(0);

        Book actual = bookService.update(
                oldConditionBook.getId(),
                expectedTitle,
                expectedAuthor.getId(),
                expectedGenres.stream()
                        .limit(2)
                        .map(Genre::getId)
                        .collect(Collectors.toSet()));

        Book bookById = bookRepository.findById(oldConditionBook.getId()).get();

        assertEquals(actual, bookById);
        assertThat(oldConditionBook)
                .usingRecursiveComparison()
                .isNotEqualTo(actual);
        assertEquals(expectedTitle, actual.getTitle());
        assertEquals(expectedAuthor, actual.getAuthor());
        assertThat(actual.getGenres()).containsExactlyInAnyOrderElementsOf(expectedGenres);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void deleteById() {
        String bookId = bookRepository.findAll().get(0).getId();

        bookService.deleteById(bookId);
        assertTrue(bookRepository.findById(bookId).isEmpty());
    }
}