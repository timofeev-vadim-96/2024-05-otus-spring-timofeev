package ru.otus.hw.services;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Сервис для работы с комментариями")
@DataMongoTest
@Import({CommentServiceImpl.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Transactional(propagation = Propagation.NEVER)
@Slf4j
class CommentServiceImplTest {
    private static final long COMMENT_LIST_SIZE_BY_1_BOOK = 1;

    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void findAllByBookId() {
        Book expectedBook = bookRepository.findAll().get(0);

        List<Comment> actualComments = commentService.findAllByBookId(expectedBook.getId());

        assertEquals(COMMENT_LIST_SIZE_BY_1_BOOK, actualComments.size());
        assertEquals(expectedBook, actualComments.get(0).getBook());
    }

    @Test
    void create() {
        String expectedText = "someText";
        Book expectedBook = bookRepository.findAll().get(0);

        Comment actual = commentService.create(expectedText, expectedBook.getId());
        List<Comment> allByBookId = commentService.findAllByBookId(expectedBook.getId());

        assertEquals(expectedText, actual.getText());
        assertEquals(expectedBook, actual.getBook());
        assertTrue(allByBookId.stream()
                .anyMatch(c -> c.getBook().equals(expectedBook) &&
                        c.getText().equals(expectedText)));
    }

    @Test
    void createNegative() {
        assertThrows(
                EntityNotFoundException.class,
                () -> commentService.create("someText", "unexpectedBookId")
        );
    }

    @Test
    void update() {
        String expectedText = "someText";
        Comment comment = commentRepository.findAll().get(0);
        log.info("update() before. " + comment.toString());

        Comment actual = commentService.update(expectedText, comment.getId());
        log.info("update() after. " + actual.toString());

        assertNotEquals(comment.getText(), actual.getText());
        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("text")
                .isEqualTo(comment);
    }

    @Test
    void updateNegative() {
        assertThrows(
                EntityNotFoundException.class,
                () -> commentService.update("someText", "unexpectedCommentId")
        );
    }

    @Test
    void deleteById() {
        String commentId = commentRepository.findAll().get(0).getId();

        commentService.deleteById(commentId);

        assertTrue(commentRepository.findById(commentId).isEmpty());
    }
}