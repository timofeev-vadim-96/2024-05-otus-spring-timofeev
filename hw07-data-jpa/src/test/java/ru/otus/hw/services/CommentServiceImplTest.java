package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.dto.CommentDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Сервис для работы с комментариями")
@DataJpaTest
@Import({CommentServiceImpl.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Transactional(propagation = Propagation.NEVER)
class CommentServiceImplTest {
    private static final long COMMENT_LIST_SIZE_BY_1_BOOK = 1;

    @Autowired
    private CommentServiceImpl commentService;

    @Test
    void findAllByBookId() {
        List<CommentDto> actualComments = commentService.findAllByBookId(1L);

        assertEquals(COMMENT_LIST_SIZE_BY_1_BOOK, actualComments.size());
        assertEquals(1L, actualComments.get(0).getBook().getId());
    }

    @Test
    void insert() {
        String expectedText = "someText";
        long expectedBookId = 1L;

        CommentDto actual = commentService.create(expectedText, expectedBookId);
        List<CommentDto> allByBookId = commentService.findAllByBookId(expectedBookId);

        assertEquals(expectedText, actual.getText());
        assertEquals(expectedBookId, actual.getBook().getId());
        assertTrue(allByBookId.stream()
                .anyMatch(c -> c.getBook().getId() == expectedBookId &&
                        c.getText().equals(expectedText)));
    }

    @Test
    void insertNegative() {
        assertThrows(
                EntityNotFoundException.class,
                () -> commentService.create("someText", Long.MIN_VALUE)
        );
    }

    @Test
    void update() {
        String expectedText = "someText";
        long expectedId = 3L;

        CommentDto actual = commentService.update(expectedText, expectedId);

        assertEquals(expectedId, actual.getId());
        assertEquals(expectedText, actual.getText());
    }

    @Test
    void updateNegative() {
        assertThrows(
                EntityNotFoundException.class,
                () -> commentService.update("someText", Long.MIN_VALUE)
        );
    }

    @Test
    void deleteById() {
        assertFalse(commentService.findAllByBookId(3L).isEmpty());

        commentService.deleteById(3L);

        assertTrue(commentService.findAllByBookId(3L).isEmpty());
    }
}