package ru.otus.hw.controllers.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.controllers.dto.CommentViewDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.dto.AuthorDto;
import ru.otus.hw.services.dto.BookDto;
import ru.otus.hw.services.dto.CommentDto;
import ru.otus.hw.services.dto.GenreDto;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = {CommentController.class})
@TestPropertySource(properties = {"mongock.enabled=false"})
@DisplayName("Контроллер для работы с комментариями")
class CommentControllerTest {
    @Autowired
    private WebTestClient webClient;

    @MockBean
    private CommentService commentService;

    private CommentDto comment;

    private CommentViewDto dto;

    @BeforeEach
    void setUp() {
        String bookId = UUID.randomUUID().toString();
        AuthorDto author = new AuthorDto(UUID.randomUUID().toString(), "Author_1");
        BookDto book = new BookDto(
                bookId,
                "Book_1",
                author,
                List.of(new GenreDto(UUID.randomUUID().toString(), "Genre_1")));
        comment = new CommentDto(
                UUID.randomUUID().toString(),
                "Larger or equals 10 symbols",
                book);
        dto = new CommentViewDto(comment.getText(), bookId);
    }

    @Test
    void createComment() {
        String bookId = dto.getBookId();
        when(commentService.create(dto.getText(), bookId))
                .thenReturn(Mono.just(comment));

        CommentDto responseBody = webClient.post().uri("/api/v1/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dto))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CommentDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).isNotNull().usingRecursiveComparison().isEqualTo(comment);
        verify(commentService, Mockito.times(1)).create(dto.getText(), bookId);
    }

    @Test
    void createCommentInvalid() {
        String unexpectedBookId = "unexpectedBookId";
        CommentViewDto viewDto = new CommentViewDto("some text larger then 10 symbols", unexpectedBookId);
        when(commentService.create(viewDto.getText(), viewDto.getBookId()))
                .thenReturn(Mono.error(new EntityNotFoundException("an exception")));

        webClient.post().uri("/api/v1/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(viewDto))
                .exchange()
                .expectStatus().isBadRequest();

        Mockito.verify(commentService, Mockito.times(1))
                .create(viewDto.getText(), viewDto.getBookId());
    }

    @Test
    void deleteComment() {
        String commentId = comment.getId();
        when(commentService.deleteById(commentId)).thenReturn(Mono.empty());

        webClient.delete().uri("/api/v1/comment/{id}", commentId)
                .exchange()
                .expectStatus().isOk();

        verify(commentService, times(1)).deleteById(commentId);
    }

    @Test
    void getAllByBookId() {
        String bookId = dto.getBookId();
        when(commentService.findAllByBookId(bookId)).thenReturn(Flux.just(comment));

        List<CommentDto> responseBody = webClient.get().uri("/api/v1/comment/{bookId}", bookId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CommentDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).isNotEmpty()
                .hasSize(1)
                .contains(comment);
        verify(commentService, times(1)).findAllByBookId(bookId);
    }
}