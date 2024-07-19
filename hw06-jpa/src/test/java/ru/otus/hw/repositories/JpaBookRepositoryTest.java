package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Репозиторий на основе JPA для работы с книгами")
@DataJpaTest
@Import({JpaBookRepository.class, JpaGenreRepository.class, JpaAuthorRepository.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class JpaBookRepositoryTest {
    private static final int BOOK_LIST_MIN_SIZE = 2;

    @Autowired
    private JpaBookRepository bookRepository;

    @Autowired
    private JpaGenreRepository genreRepository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @ValueSource(longs = {1, 2})
    void shouldReturnCorrectBookById(long bookId) {
        Book expected = em.find(Book.class, bookId);
        Optional<Book> actual = bookRepository.findById(bookId);

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
        assertDoesNotThrow(() -> actual.get().getAuthor().getFullName());
        assertDoesNotThrow(() -> actual.get().getGenres().get(0));
    }

    @DisplayName("должен возвращать Optional.empty() при поиске по несущест. id")
    @Test
    void shouldReturnOptionalEmpty() {
        Optional<Book> actual = bookRepository.findById(Long.MAX_VALUE);

        assertTrue(actual.isEmpty());
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        List<Book> actualBooks = bookRepository.findAll();
        Book expectedFirstBook = em.find(Book.class, 1L);

        assertFalse(actualBooks.isEmpty());
        assertTrue(
                actualBooks.size() >= BOOK_LIST_MIN_SIZE);
        assertTrue(actualBooks.contains(expectedFirstBook));
        assertDoesNotThrow(() -> actualBooks.get(0).getAuthor().getFullName());
        assertDoesNotThrow(() -> actualBooks.get(0).getGenres().get(0));
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        Author author = em.find(Author.class, 1L);
        Genre genre = genreRepository.findAllByIds(Set.of(1L)).get(0);
        Book expectedBook = new Book(0L, "BookTitle" + Integer.MAX_VALUE, author,
                List.of(genre));
        Book actualBook = bookRepository.save(expectedBook);

        assertEquals(expectedBook.getTitle(), actualBook.getTitle());
        assertEquals(author, actualBook.getAuthor());
        assertTrue(actualBook.getGenres().contains(genre));
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldUpdateBook() {
        final String UPDATED_TITLE = "updated title";
        Book oldConditionBook = em.find(Book.class, 1L);
        Book book = new Book(
                oldConditionBook.getId(),
                UPDATED_TITLE,
                oldConditionBook.getAuthor(),
                oldConditionBook.getGenres());

        Book updatedBook = bookRepository.save(book);
        Book actualBook = em.find(Book.class, 1L);

        assertEquals(updatedBook, actualBook);
        assertEquals(UPDATED_TITLE, actualBook.getTitle());
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        assertNotNull(em.find(Book.class, 3L));
        bookRepository.deleteById(3L);
        assertNull(em.find(Book.class, 3L));
    }
}