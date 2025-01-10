package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.ReactiveBookRepository;
import ru.otus.hw.repositories.ReactiveCommentRepository;
import ru.otus.hw.services.dto.CommentDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Сервис для работы с комментариями")
@DataMongoTest
@Import({CommentServiceImpl.class, BookServiceImpl.class, AuthorServiceImpl.class, GenreServiceImpl.class})
@Transactional(propagation = Propagation.NEVER)
class CommentServiceImplTest {
    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private ReactiveBookRepository bookRepository;

    @Autowired
    private ReactiveCommentRepository commentRepository;

    private Book book;

    @BeforeEach
    public void initBook() {
        book = bookRepository.findAll().blockFirst();
    }

    @Test
    void findAllByBookId() {
        String expectedBookId = book.getId();
        Flux<CommentDto> commentsByBookId = commentService.findAllByBookId(expectedBookId);

        StepVerifier.FirstStep<CommentDto> verifier = StepVerifier.create(commentsByBookId);

        verifier.expectNextMatches(c -> c != null
                        && !c.getText().isEmpty()
                        && c.getBook() != null
                        && c.getBook().getId().equals(expectedBookId)
                )
                .verifyComplete();

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void insert() {
        String expectedText = "someText";
        String expectedBookId = book.getId();

        CommentDto actual = commentService.create(expectedText, expectedBookId)
                .block();
        List<CommentDto> allByBookId = commentService.findAllByBookId(expectedBookId)
                .collectList()
                .block();

        assertEquals(expectedText, actual.getText());
        assertEquals(expectedBookId, actual.getBook().getId());
        assertTrue(allByBookId.stream()
                .anyMatch(c -> c.getBook().getId().equals(expectedBookId) &&
                        c.getText().equals(expectedText)));
    }

    @Test
    void insertNegative() {
        String unexpectedBookId = "unexpected";

        assertThrows(
                EntityNotFoundException.class,
                () -> commentService.create("someText", unexpectedBookId).block()
        );
    }

    @Test
    void update() {
        String expectedText = "someText";
        String expectedCommentId = commentRepository.findAll().blockFirst().getId();

        CommentDto actual = commentService.update(expectedText, expectedCommentId).block();

        assertEquals(expectedCommentId, actual.getId());
        assertEquals(expectedText, actual.getText());
    }

    @Test
    void updateNegative() {
        String unexpectedBookId = "unexpected";

        assertThrows(
                EntityNotFoundException.class,
                () -> commentService.update("someText", unexpectedBookId).block()
        );
    }

    @Test
    void deleteById() {
        List<CommentDto> comments = commentService.findAllByBookId(book.getId()).collectList().block();
        assertNotNull(comments);

        String expectedCommentId = comments.get(0).getId();
        String expectedBookId = comments.get(0).getBook().getId();

        commentService.deleteById(expectedCommentId).block();

        assertThat(commentService.findAllByBookId(expectedBookId).collectList().block()).isEmpty();
    }
}