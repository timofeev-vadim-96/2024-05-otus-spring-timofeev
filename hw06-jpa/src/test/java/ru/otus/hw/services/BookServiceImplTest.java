package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.repositories.JpaAuthorRepository;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaGenreRepository;
import ru.otus.hw.services.dto.AuthorDto;
import ru.otus.hw.services.dto.BookDto;
import ru.otus.hw.services.dto.GenreDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Сервис для работы с книгами")
@DataJpaTest
@Import({BookServiceImpl.class, JpaAuthorRepository.class, JpaGenreRepository.class, JpaBookRepository.class})
@Transactional(propagation = Propagation.NEVER)
class BookServiceImplTest {
    private static final long BOOK_LIST_SIZE = 3;

    @Autowired
    private BookServiceImpl bookService;

    private List<BookDto> dbBooks;

    @BeforeEach
    void setUp() {
        dbBooks = getDbBooks();
    }

    @ParameterizedTest
    @MethodSource("getDbBooks")
    void findById(BookDto book) {
        Optional<BookDto> actual = bookService.findById(book.getId());

        assertTrue(actual.isPresent());
        assertThat(actual.get()).usingRecursiveComparison()
                .isEqualTo(book);
    }

    @Test
    void findAll() {
        List<BookDto> actualBooks = bookService.findAll();

        assertFalse(actualBooks.isEmpty());
        assertEquals(BOOK_LIST_SIZE, actualBooks.size());
        assertThat(actualBooks).containsExactlyInAnyOrderElementsOf(dbBooks);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void create() {
        long expectedAuthorId = 1L;
        String expectedTitle = "titleToInsert";
        Set<Long> expectedGenresIds = Set.of(1L, 2L, 5L);

        BookDto actual = bookService.create(expectedTitle, expectedAuthorId, expectedGenresIds);

        assertEquals(expectedTitle, actual.getTitle());
        assertEquals(expectedGenresIds, actual.getGenres()
                .stream()
                .map(GenreDto::getId)
                .collect(Collectors.toSet()));
        assertEquals(expectedAuthorId, actual.getAuthor().getId());
    }

    @Test
    void insertNegative() {
        assertThrows(
                IllegalArgumentException.class,
                () -> bookService.create("someTitle", 1L, Set.of()));
        assertThrows(
                EntityNotFoundException.class,
                () -> bookService.create("someTitle", Long.MAX_VALUE, Set.of(1L, 2L)));
        assertThrows(
                EntityNotFoundException.class,
                () -> bookService.create("someTitle", 1L, Set.of(1L, Long.MAX_VALUE)));
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
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void update() {
        long expectedAuthorId = 1L;
        String expectedTitle = "titleToUpdate";
        Set<Long> expectedGenresIds = Set.of(1L, 3L, 6L);

        BookDto actual = bookService.update(
                1L,
                expectedTitle,
                expectedAuthorId,
                expectedGenresIds);

        assertEquals(expectedGenresIds, actual.getGenres()
                .stream()
                .map(GenreDto::getId)
                .collect(Collectors.toSet()));
        assertEquals(expectedTitle, actual.getTitle());
        assertEquals(expectedAuthorId, actual.getAuthor().getId());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void deleteById() {
        assertTrue(bookService.findById(3L).isPresent());

        bookService.deleteById(3L);

        assertTrue(bookService.findById(3L).isEmpty());
    }

    private static List<AuthorDto> getDbAuthors() {
        return LongStream.range(1, 4).boxed()
                .map(id -> new AuthorDto(id, "Author_" + id))
                .toList();
    }

    private static List<GenreDto> getDbGenres() {
        return LongStream.range(1, 7).boxed()
                .map(id -> new GenreDto(id, "Genre_" + id))
                .toList();
    }

    private static List<BookDto> getDbBooks(List<AuthorDto> dbAuthors, List<GenreDto> dbGenres) {
        return LongStream.range(1, 4).boxed()
                .map(id -> new BookDto(id,
                        "BookTitle_" + id,
                        dbAuthors.get((int) (id - 1)),
                        dbGenres.subList((int) ((id - 1) * 2), (int) ((id - 1) * 2 + 2))
                ))
                .toList();
    }

    private static List<BookDto> getDbBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }
}