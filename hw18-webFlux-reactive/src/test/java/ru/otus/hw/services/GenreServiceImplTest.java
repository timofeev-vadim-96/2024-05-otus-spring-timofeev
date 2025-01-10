package ru.otus.hw.services;

import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.ReactiveGenreRepository;
import ru.otus.hw.services.dto.GenreDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с жанрами")
@DataMongoTest
@Import({GenreServiceImpl.class})
@Transactional(propagation = Propagation.NEVER)
class GenreServiceImplTest {
    @Autowired
    private GenreService genreService;

    @Autowired
    private ReactiveGenreRepository genreRepository;

    @Test
    void findAll() {
        Flux<GenreDto> genres = genreService.findAll();

        StepVerifier.FirstStep<GenreDto> verifier = StepVerifier.create(genres);

        for (int i = 0; i < 6; i++) {
            verifier.expectNextMatches(g -> g != null && Strings.isNotEmpty(g.getName()));
        }
        verifier.verifyComplete();
    }

    @Test
    void findAllByIds() {
        Set<String> genresIds = genreRepository.findAll()
                .collectList()
                .block()
                .stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        List<GenreDto> genres = genreService.findAllByIds(genresIds).collectList().block();

        assertThat(genres).isNotEmpty().hasSize(genresIds.size());
    }
}