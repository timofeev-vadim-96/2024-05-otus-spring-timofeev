package ru.otus.hw.repositories;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Репозиторий на основе JPA для работы с жанрами")
@DataJpaTest
@Import(value = {JpaGenreRepository.class})
@Transactional(propagation = Propagation.NEVER)
class JpaGenreRepositoryTest {
    private static final long GENRES_LIST_SIZE = 6;

    @Autowired
    private JpaGenreRepository genreRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @Transactional(readOnly = true)
    void findAll() {
        Genre expectedGenreToContain = em.find(Genre.class, 5L);;

        List<Genre> genres = genreRepository.findAll();

        assertFalse(genres.isEmpty());
        assertEquals(GENRES_LIST_SIZE, genres.size());
        assertTrue(genres.contains(expectedGenreToContain));
    }

    @Test
    void findAllByIds() {
        List<Genre> genresByIds = genreRepository.findAllByIds(Set.of(1L, 2L));

        assertFalse(genresByIds.isEmpty());
        Assertions.assertThat(genresByIds)
                .filteredOn(g-> g.getId() == 1L || g.getId() == 2L)
                .size()
                .isEqualTo(2);
    }
}