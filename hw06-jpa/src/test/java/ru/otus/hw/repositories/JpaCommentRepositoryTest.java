package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Репозиторий на основе JPA для работы с комментариями")
@DataJpaTest
@Import({JpaBookRepository.class, JpaCommentRepository.class})
class JpaCommentRepositoryTest {
    @Autowired
    private JpaCommentRepository commentRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void findAllByBookId() {
        List<Comment> allByBookId = commentRepository.findAllByBookId(1L);

        assertFalse(allByBookId.isEmpty());
        assertTrue(allByBookId.stream().anyMatch(c -> c.getBook().getId() == 1L));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2})
    void findById(long id) {
        Comment expected = em.find(Comment.class, id);

        Optional<Comment> actual = commentRepository.findById(id);

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    void findByIdNegative() {
        Comment comment = em.find(Comment.class, Long.MAX_VALUE);

        assertNull(comment);
    }

    @Test
    void saveWhenInsert() {
        Book book = em.find(Book.class, 1L);
        Comment comment = new Comment(null, "some text", book);

        Comment actual = commentRepository.save(comment);

        assertEquals(comment.getText(), actual.getText());
        assertEquals(comment.getBook().getId(), actual.getBook().getId());
    }

    @Test
    void saveWhenUpdate() {
        Book book = em.find(Book.class, 1L);
        Comment comment = new Comment(1L, "some text", book);

        Comment actual = commentRepository.save(comment);

        assertEquals(comment.getText(), actual.getText());
        assertEquals(comment.getBook().getId(), actual.getBook().getId());
    }

    @Test
    void deleteById() {
        assertNotNull(em.find(Comment.class, 3L));
        commentRepository.deleteById(3L);
        assertNull(em.find(Comment.class, 3L));
    }
}