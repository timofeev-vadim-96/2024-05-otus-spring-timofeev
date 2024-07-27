package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Репозиторий на основе JPA для работы с книгами")
@DataJpaTest
@Import({JpaBookRepository.class})
class JpaBookRepositoryTest {
    private static final int BOOK_LIST_SIZE = 3;

    @Autowired
    private JpaBookRepository bookRepository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @ValueSource(longs = {1, 2})
    void shouldReturnCorrectBookById(long bookId) {
        Book expected = em.find(Book.class, bookId);

        Optional<Book> actual = bookRepository.findById(bookId);

        assertTrue(actual.isPresent());
        assertThat(actual.get())
                .usingRecursiveComparison()
                .isEqualTo(expected);
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
        Book expectedFirstBook = em.find(Book.class, 1L);

        List<Book> actualBooks = bookRepository.findAll();

        assertFalse(actualBooks.isEmpty());
        assertEquals(BOOK_LIST_SIZE, actualBooks.size());
        assertTrue(actualBooks.contains(expectedFirstBook));
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        Author author = em.find(Author.class, 1L);
        Genre genre = em.find(Genre.class, 1L);
        Book expected = new Book(null, "BookTitle_" + Integer.MAX_VALUE, author,
                List.of(genre));

        Book actual = bookRepository.save(expected);

        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldUpdateBook() {
        String updatedTitle = "updated title";
        Book oldConditionBook = em.find(Book.class, 1L);
        Book book = new Book(
                oldConditionBook.getId(),
                updatedTitle,
                oldConditionBook.getAuthor(),
                oldConditionBook.getGenres());

        Book updatedBook = bookRepository.save(book);
        Book actualBook = em.find(Book.class, 1L);

        assertThat(updatedBook).usingRecursiveComparison()
                .isEqualTo(actualBook);
        assertEquals(updatedTitle, actualBook.getTitle());
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        assertNotNull(em.find(Book.class, 3L));

        bookRepository.deleteById(3L);

        assertNull(em.find(Book.class, 3L));
    }
}