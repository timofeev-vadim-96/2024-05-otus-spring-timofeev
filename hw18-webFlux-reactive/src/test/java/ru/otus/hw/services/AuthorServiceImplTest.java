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
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.ReactiveAuthorRepository;
import ru.otus.hw.services.dto.AuthorDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Сервис для работы с авторами")
@DataMongoTest
@Import({AuthorServiceImpl.class})
@Transactional(propagation = Propagation.NEVER)
class AuthorServiceImplTest {
    @Autowired
    private AuthorService authorService;

    @Autowired
    private ReactiveAuthorRepository authorRepository;

    @Test
    void findAll() {
        Flux<AuthorDto> authors = authorService.findAll();

        StepVerifier.FirstStep<AuthorDto> verifier = StepVerifier.create(authors);

        for (int i = 0; i < 3; i++) {
            verifier.expectNextMatches(a -> a != null && Strings.isNotEmpty(a.getFullName()));
        }
        verifier.verifyComplete();
    }

    @Test
    void findById() {
        Author expected = authorRepository.findAll()
                .collectList()
                .block()
                .get(0);

        AuthorDto actual = authorService.findById(expected.getId()).block();

        assertNotNull(actual);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}