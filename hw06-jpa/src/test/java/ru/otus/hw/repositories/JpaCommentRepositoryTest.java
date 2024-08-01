package ru.otus.hw.repositories;

import lombok.extern.slf4j.Slf4j;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Репозиторий на основе JPA для работы с комментариями")
@DataJpaTest
@Import({JpaCommentRepository.class})
@Slf4j
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
        Optional<Comment> actual = commentRepository.findById(Long.MAX_VALUE);

        assertTrue(actual.isEmpty());
    }

    @Test
    void saveWhenInsert() {
        Book book = em.find(Book.class, 1L);
        Comment expected = new Comment(null, "some text", book);

        Comment actual = commentRepository.save(expected);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    void saveWhenUpdate() {
        Comment expected = em.find(Comment.class, 1L);
        expected.setText("updated text");

        Comment actual = commentRepository.save(expected);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void deleteById() {
        assertNotNull(em.find(Comment.class, 3L));

        commentRepository.deleteById(3L);

        assertNull(em.find(Comment.class, 3L));
    }
}