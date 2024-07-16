package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Репозиторий на основе JPA для работы с авторами")
@DataJpaTest
@Import(value = {JpaAuthorRepository.class})
@Transactional(propagation = Propagation.NEVER)
class JpaAuthorRepositoryTest {
    private static final long AUTHORS_LIST_MAX_SIZE = 3;

    @Autowired
    private JpaAuthorRepository authorRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void findAll() {
        List<Author> authors = authorRepository.findAll();
        Author author = authorRepository.findById(1L).get();

        assertFalse(authors.isEmpty());
        assertEquals(AUTHORS_LIST_MAX_SIZE, authors.size());
        assertTrue(authors.contains(author));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3})
    @Transactional(readOnly = true)
    void findById(long id) {
        Author expected = em.find(Author.class, id);

        Optional<Author> actual = authorRepository.findById(id);

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    void findByIdNegative() {
        Optional<Author> author = authorRepository.findById(Long.MAX_VALUE);

        assertTrue(author.isEmpty());
    }
}