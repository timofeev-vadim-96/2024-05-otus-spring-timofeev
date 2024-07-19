package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.JpaAuthorRepository;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaGenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Сервис для работы с книгами")
@DataJpaTest
@Import({BookServiceImpl.class, JpaGenreRepository.class, JpaAuthorRepository.class, JpaBookRepository.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Transactional(propagation = Propagation.NEVER)
class BookServiceImplTest {
    private static final long BOOK_LIST_MIN_SIZE = 2;

    @Autowired
    private BookServiceImpl bookService;

    @Autowired
    private TestEntityManager em;

    @ParameterizedTest
    @ValueSource(longs = {1, 2})
    @Transactional(readOnly = true)
    void findById(long id) {
        Book expected = em.find(Book.class, id);

        Optional<Book> actual = bookService.findById(id);

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
        assertEquals(id, actual.get().getId());
        assertDoesNotThrow(() -> actual.get().getAuthor().getFullName());
        assertDoesNotThrow(() -> actual.get().getGenres().get(0));
    }

    @Test
    void findAll() {
        Optional<Book> expectedBook = bookService.findById(1L);

        List<Book> actualBooks = bookService.findAll();

        assertFalse(actualBooks.isEmpty());
        assertTrue(actualBooks.size() >= BOOK_LIST_MIN_SIZE);
        assertTrue(actualBooks.contains(expectedBook.get()));
    }

    @Test
    void insert() {
        long expectedAuthorId = 1L;
        String expectedTitle = "titleToInsert";
        Set<Long> expectedGenresIds = Set.of(1L, 2L, 5L);
        Book actual = bookService.insert(expectedTitle, expectedAuthorId, expectedGenresIds);
        List<Book> books = bookService.findAll();

        assertEquals(expectedTitle, actual.getTitle());
        assertEquals(expectedGenresIds, actual.getGenres()
                .stream()
                .map(Genre::getId)
                .collect(Collectors.toSet()));
        assertEquals(expectedAuthorId, actual.getAuthor().getId());
        assertTrue(books.stream()
                .anyMatch(b -> b.getAuthor().getId() == expectedAuthorId &&
                        b.getTitle().equals(expectedTitle) &&
                        b.getGenres().size() == expectedGenresIds.size()));
        assertTrue(expectedGenresIds
                .containsAll(books
                        .stream()
                        .filter(b -> b.getTitle().equals(expectedTitle))
                        .findFirst().get()
                        .getGenres()
                        .stream()
                        .map(Genre::getId)
                        .toList()));
    }

    @Test
    void insertNegative() {
        assertThrows(
                IllegalArgumentException.class,
                () -> bookService.insert("someTitle", 1L, Set.of()));
        assertThrows(
                EntityNotFoundException.class,
                () -> bookService.insert("someTitle", Long.MAX_VALUE, Set.of(1L, 2L)));
        assertThrows(
                EntityNotFoundException.class,
                () -> bookService.insert("someTitle", 1L, Set.of(1L, Long.MAX_VALUE)));
    }

    @Test
    void updateNegative() {
        assertThrows(
                EntityNotFoundException.class,
                () -> bookService.update(Long.MIN_VALUE, "someTitle", 1L, Set.of(1L, 2L)));
        assertThrows(
                IllegalArgumentException.class,
                () -> bookService.update(1L, "someTitle", 1L, Set.of()));
        assertThrows(
                EntityNotFoundException.class,
                () -> bookService.update(1L, "someTitle", Long.MAX_VALUE, Set.of(1L, 2L)));
        assertThrows(
                EntityNotFoundException.class,
                () -> bookService.update(1L, "someTitle", 1L, Set.of(1L, Long.MAX_VALUE)));
    }

    @Test
    void update() {
        long expectedAuthorId = 1L;
        String expectedTitle = "titleToUpdate";
        Set<Long> expectedGenresIds = Set.of(1L, 3L, 6L);
        Book oldConditionBook = bookService.findById(1L).get();

        Book actual = bookService.update(
                oldConditionBook.getId(),
                expectedTitle,
                expectedAuthorId,
                expectedGenresIds);
        Book book = bookService.findById(1L).get();

        assertEquals(actual, book);
        assertEquals(expectedGenresIds, actual.getGenres()
                .stream()
                .map(Genre::getId)
                .collect(Collectors.toSet()));
        assertEquals(expectedTitle, actual.getTitle());
        assertEquals(expectedAuthorId, actual.getAuthor().getId());
    }

    @Test
    void deleteById() {
        assertTrue(bookService.findById(3L).isPresent());
        bookService.deleteById(3L);
        assertTrue(bookService.findById(3L).isEmpty());
    }
}