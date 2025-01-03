package ru.otus.hw.controllers.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.controllers.dto.BookViewDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.dto.AuthorDto;
import ru.otus.hw.services.dto.BookDto;
import ru.otus.hw.services.dto.GenreDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = {BookController.class})
@TestPropertySource(properties = {"mongock.enabled=false"})
@DisplayName("Контроллер для работы с книгами")
class BookControllerTest {
    @Autowired
    private WebTestClient webClient;

    @MockBean
    private BookService bookService;

    private BookDto book;

    private BookViewDto dto;

    @BeforeEach
    void setUp() {
        String bookId = UUID.randomUUID().toString();
        AuthorDto author = new AuthorDto(UUID.randomUUID().toString(), "Author_1");
        List<GenreDto> genres = List.of(new GenreDto(UUID.randomUUID().toString(), "Genre_1"));
        book = new BookDto(
                bookId,
                "Book_1",
                author,
                genres);
        dto = new BookViewDto(bookId, book.getTitle(), author.getId(), Set.of(genres.get(0).getId()));
    }

    @Test
    void get() {
        String id = book.getId();
        when(bookService.findById(id)).thenReturn(Mono.just(book));

        BookDto responseBody = webClient.get().uri("/api/v1/book/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertEquals(id, responseBody.getId());
        verify(bookService, times(1)).findById(id);
    }

    @Test
    void getNegative() {
        String unexpectedId = "unexpected";
        when(bookService.findById(unexpectedId)).thenReturn(Mono.error(new EntityNotFoundException("exception")));

        webClient.get().uri("/api/v1/book/{id}", unexpectedId)
                .exchange()
                .expectStatus()
                .isNotFound();

        verify(bookService, times(1)).findById(unexpectedId);
    }

    @Test
    void update() {
        when(bookService.update(dto.getId(), dto.getTitle(), dto.getAuthorId(), dto.getGenres()))
                .thenReturn(Mono.just(book));

        BookDto responseBody = webClient.put().uri("/api/v1/book")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).isNotNull().usingRecursiveComparison().isEqualTo(book);
        verify(bookService, times(1))
                .update(book.getId(), dto.getTitle(), dto.getAuthorId(), dto.getGenres());
    }

    @Test
    void updateInvalid() {
        when(bookService.update(dto.getId(), dto.getTitle(), dto.getAuthorId(), dto.getGenres()))
                .thenReturn(Mono.error(new EntityNotFoundException("exception")));

        webClient.put().uri("/api/v1/book")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dto))
                .exchange()
                .expectStatus().isBadRequest();

        verify(bookService, times(1))
                .update(dto.getId(), dto.getTitle(), dto.getAuthorId(), dto.getGenres());
    }

    @Test
    void create() {
        when(bookService.create(dto.getTitle(), dto.getAuthorId(), dto.getGenres()))
                .thenReturn(Mono.just(book));

        BookDto responseBody = webClient.post().uri("/api/v1/book")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dto))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BookDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).isNotNull().usingRecursiveComparison().isEqualTo(book);
        verify(bookService, times(1))
                .create(dto.getTitle(), dto.getAuthorId(), dto.getGenres());
    }

    @Test
    void createInvalid() {
        when(bookService.create(dto.getTitle(), dto.getAuthorId(), dto.getGenres()))
                .thenReturn(Mono.error(new EntityNotFoundException("exception")));

        webClient.post().uri("/api/v1/book")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dto))
                .exchange()
                .expectStatus().isBadRequest();

        verify(bookService, times(1))
                .create(dto.getTitle(), dto.getAuthorId(), dto.getGenres());
    }

    @Test
    void getAll() {
        when(bookService.findAll()).thenReturn(Flux.just(book));

        List<BookDto> responseBody = webClient.get().uri("/api/v1/book")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookDto.class)
                .returnResult()
                .getResponseBody();
        assertThat(responseBody).isNotEmpty().hasSize(1).contains(book);

        verify(bookService, times(1)).findAll();
    }

    @Test
    void delete() {
        String id = book.getId();
        when(bookService.deleteById(id)).thenReturn(Mono.empty());

        webClient.delete().uri("/api/v1/book/{id}", id)
                .exchange()
                .expectStatus().isOk();

        verify(bookService, times(1)).deleteById(id);
    }
}