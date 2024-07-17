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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Репозиторий на основе JPA для работы с книгами")
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Import({BookRepositoryCustomImpl.class})
@Transactional(propagation = Propagation.NEVER)
class BookRepositoryCustomImplTest {
    private static final int BOOK_LIST_Size = 3;

    @Autowired
    private BookRepositoryCustom bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3})
    @Transactional(readOnly = true)
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
        Book expectedFirstBook = bookRepository.findById(1L).get();

        assertFalse(actualBooks.isEmpty());
        assertTrue(
                actualBooks.size() >= BOOK_LIST_Size);
        assertTrue(actualBooks.contains(expectedFirstBook));
        assertDoesNotThrow(() -> actualBooks.get(0).getAuthor().getFullName());
        assertDoesNotThrow(() -> actualBooks.get(0).getGenres().get(0));
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        Author author = authorRepository.findById(1L).get();
        Genre genre = genreRepository.findAllByIds(Set.of(1L)).get(0);
        Book expectedBook = new Book(0, "BookTitle" + Integer.MAX_VALUE, author,
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
        Book oldConditionBook = bookRepository.findById(1L).get();
        System.out.println("old-conditional: " + oldConditionBook);
        Book book = new Book(
                oldConditionBook.getId(),
                UPDATED_TITLE,
                oldConditionBook.getAuthor(),
                oldConditionBook.getGenres());

        Book updatedBook = bookRepository.save(book);
        Book actualBook = bookRepository.findById(1L).get();

        assertNotEquals(oldConditionBook, actualBook);
        assertEquals(updatedBook, actualBook);
        assertEquals(UPDATED_TITLE, actualBook.getTitle());
    }
}