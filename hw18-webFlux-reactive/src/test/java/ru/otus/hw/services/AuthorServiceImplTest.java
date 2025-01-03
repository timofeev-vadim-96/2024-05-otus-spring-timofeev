package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.ReactiveAuthorRepository;
import ru.otus.hw.services.dto.AuthorDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Сервис для работы с авторами")
@DataMongoTest
@Import({AuthorServiceImpl.class})
@Transactional(propagation = Propagation.NEVER)
class AuthorServiceImplTest {
    private static final int EXPECTED_AUTHORS_SIZE = 3;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private ReactiveAuthorRepository authorRepository;

    @Test
    void findAll() {
        List<AuthorDto> authors = authorService.findAll().collectList().block();

        assertThat(authors).isNotEmpty().hasSize(EXPECTED_AUTHORS_SIZE);
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