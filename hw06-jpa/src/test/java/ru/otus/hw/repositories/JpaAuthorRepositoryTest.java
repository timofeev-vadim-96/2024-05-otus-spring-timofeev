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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Репозиторий на основе JPA для работы с авторами")
@DataJpaTest
@Import(value = {JpaAuthorRepository.class})
class JpaAuthorRepositoryTest {
    private static final long AUTHORS_LIST_SIZE = 3;

    @Autowired
    private JpaAuthorRepository authorRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void findAll() {
        Author expectedToContain = em.find(Author.class, 1L);

        List<Author> authors = authorRepository.findAll();

        assertFalse(authors.isEmpty());
        assertEquals(AUTHORS_LIST_SIZE, authors.size());
        assertTrue(authors.contains(expectedToContain));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3})
    void findById(long id) {
        Author expected = em.find(Author.class, id);

        Optional<Author> actual = authorRepository.findById(id);

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    void findByIdNegative() {
        Optional<Author> notExpected = authorRepository.findById(Long.MAX_VALUE);

        assertTrue(notExpected.isEmpty());
    }
}