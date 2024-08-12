package ru.otus.hw.services;

import lombok.extern.slf4j.Slf4j;
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
import ru.otus.hw.services.dto.BookDto;
import ru.otus.hw.services.dto.CommentDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Сервис для работы с комментариями")
@DataMongoTest
@Import({CommentServiceImpl.class})
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

        List<CommentDto> actualComments = commentService.findAllByBookId(expectedBook.getId());

        assertEquals(COMMENT_LIST_SIZE_BY_1_BOOK, actualComments.size());
        assertThat(actualComments.get(0).getBook())
                .usingRecursiveComparison()
                .isEqualTo(expectedBook);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void create() {
        String expectedText = "someText";
        Book expectedBook = bookRepository.findAll().get(0);

        CommentDto actual = commentService.create(expectedText, expectedBook.getId());
        List<CommentDto> allByBookId = commentService.findAllByBookId(expectedBook.getId());

        assertEquals(expectedText, actual.getText());
        assertThat(actual.getBook())
                .usingRecursiveComparison()
                .isEqualTo(expectedBook);
        assertTrue(allByBookId.stream()
                .anyMatch(c -> c.getBook().equals(new BookDto(expectedBook)) &&
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

        CommentDto actual = commentService.update(expectedText, comment.getId());

        assertNotEquals(comment.getText(), actual.getText());
        assertThat(actual)
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