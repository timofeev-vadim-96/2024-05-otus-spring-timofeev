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
import ru.otus.hw.services.GenreService;
import ru.otus.hw.services.dto.GenreDto;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = {GenreController.class})
@TestPropertySource(properties = {"mongock.enabled=false"})
@DisplayName("Контроллер для работы с жанрами")
class GenreControllerTest {
    @Autowired
    private WebTestClient webClient;

    @MockBean
    private GenreService genreService;

    @Test
    void getAll() {
        GenreDto expected = new GenreDto(UUID.randomUUID().toString(), "Genre_1");
        when(genreService.findAll()).thenReturn(Flux.just(expected));

        Flux<GenreDto> response = webClient.get().uri("/api/v1/genre")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(GenreDto.class)
                .getResponseBody();

        StepVerifier.create(response)
                .expectNext(expected)
                .expectComplete()
                .verify();
        verify(genreService, times(1)).findAll();
    }
}