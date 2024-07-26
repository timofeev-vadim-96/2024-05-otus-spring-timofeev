package ru.otus.hw.repositories;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Репозиторий на основе JPA для работы с жанрами")
@DataJpaTest
class GenreRepositoryTest {
    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @Transactional(readOnly = true)
    void findAllByIds() {
        Genre genre = em.find(Genre.class, 1L);

        List<Genre> genresByIds = genreRepository.findAllByIds(Set.of(1L, 2L));

        assertFalse(genresByIds.isEmpty());
        Assertions.assertThat(genresByIds)
                .filteredOn(g -> g.getId() == 1L || g.getId() == 2L)
                .size()
                .isEqualTo(2);
        assertTrue(genresByIds.contains(genre));
    }
}