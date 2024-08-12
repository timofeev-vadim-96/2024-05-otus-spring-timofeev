package ru.otus.hw.repositories;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("Репозиторий на основе MongoRepo для работы с жанрами")
@DataMongoTest
class GenreRepositoryTest {
    @Autowired
    private GenreRepository genreRepository;

    @Test
    void findAllByIds() {
        List<Genre> all = genreRepository.findAll();
        String firstGenreId = all.get(0).getId();
        String secondGenreId = all.get(1).getId();

        List<Genre> genresByIds = genreRepository.findAllByIds(Set.of(firstGenreId, secondGenreId));

        assertFalse(genresByIds.isEmpty());
        Assertions.assertThat(genresByIds)
                .filteredOn(g -> g.getId().equals(firstGenreId) || g.getId().equals(secondGenreId))
                .size()
                .isEqualTo(2);
    }
}