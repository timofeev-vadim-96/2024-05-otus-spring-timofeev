package ru.otus.hw.controllers.classic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@WebFluxTest(controllers = PageController.class)
@TestPropertySource(properties = {"mongock.enabled=false"})
@DisplayName("Классический mvc контроллер для выдачи html-страниц")
class PageControllerTest {
    @Autowired
    private WebTestClient webClient;

    @Test
    void create() {
        webClient.get()
                .uri("/create")
                .accept(MediaType.TEXT_HTML)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(body -> {
                    assertTrue(body.contains("Book creation"));
                });
    }

    @Test
    void edit() {
        String id = UUID.randomUUID().toString();

        webClient.get()
                .uri("/edit/{id}", id)
                .accept(MediaType.TEXT_HTML)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(body -> {
                    assertTrue(body.contains("Book editing"));
                });
    }

    @Test
    void list() {
        webClient.get()
                .uri("/")
                .accept(MediaType.TEXT_HTML)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(body -> {
                    assertTrue(body.contains("Book List"));
                });
    }

    @Test
    void book() {
        String id = UUID.randomUUID().toString();

        webClient.get()
                .uri("/book/{id}", id)
                .accept(MediaType.TEXT_HTML)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(body -> {
                    assertTrue(body.contains("Book info"));
                });
    }
}