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
import ru.otus.hw.services.GenreService;
import ru.otus.hw.services.dto.GenreDto;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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

        List<GenreDto> responseBody = webClient.get().uri("/api/v1/genre")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(GenreDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).isNotEmpty()
                .hasSize(1)
                .contains(expected);
        verify(genreService, times(1)).findAll();
    }
}