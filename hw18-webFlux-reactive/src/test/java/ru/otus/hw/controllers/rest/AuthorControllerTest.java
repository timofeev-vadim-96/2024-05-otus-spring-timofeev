package ru.otus.hw.controllers.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.dto.AuthorDto;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = {AuthorController.class})
@TestPropertySource(properties = {"mongock.enabled=false"})
@DisplayName("Контроллер для работы с авторами")
class AuthorControllerTest {
    @Autowired
    private WebTestClient webClient;

    @MockBean
    private AuthorService authorService;

    @Test
    void getAll() {
        AuthorDto expected = new AuthorDto(UUID.randomUUID().toString(), "Author_1");
        when(authorService.findAll()).thenReturn(Flux.just(expected));

        Flux<AuthorDto> response = webClient.get().uri("/api/v1/author")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(AuthorDto.class)
                .getResponseBody();

        StepVerifier.create(response)
                .expectNext(expected)
                .expectComplete()
                .verify();
        verify(authorService, times(1)).findAll();
    }
}