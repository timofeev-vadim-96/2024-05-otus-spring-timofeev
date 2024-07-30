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
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaCommentRepository;
import ru.otus.hw.repositories.JpaGenreRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Сервис для работы с комментариями")
@DataJpaTest
@Import({CommentServiceImpl.class, JpaCommentRepository.class, JpaBookRepository.class, JpaGenreRepository.class})
@Transactional(propagation = Propagation.NEVER)
class CommentServiceImplTest {
    private static final long COMMENT_LIST_SIZE_BY_1_BOOK = 1;

    @Autowired
    private CommentServiceImpl commentService;

    @Test
    void findAllByBookId() {
        List<Comment> actualComments = commentService.findAllByBookId(1L);

        assertEquals(COMMENT_LIST_SIZE_BY_1_BOOK, actualComments.size());
        assertEquals(1L, actualComments.get(0).getBook().getId());
        assertDoesNotThrow(() -> actualComments.get(0).getBook().getTitle());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void insert() {
        String expectedText = "someText";
        long expectedBookId = 1L;

        Comment actual = commentService.insert(expectedText, expectedBookId);

        assertEquals(expectedText, actual.getText());
        assertEquals(expectedBookId, actual.getBook().getId());
        assertDoesNotThrow(() -> actual.getBook().getTitle());
    }

    @Test
    void insertNegative() {
        assertThrows(
                EntityNotFoundException.class,
                () -> commentService.insert("someText", Long.MIN_VALUE)
        );
    }

    @Test
    void update() {
        String expectedText = "someText";
        long expectedId = 3L;

        Comment actual = commentService.update(expectedText, expectedId);

        assertEquals(expectedId, actual.getId());
        assertEquals(expectedText, actual.getText());
        assertDoesNotThrow(() -> actual.getBook().getTitle());
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