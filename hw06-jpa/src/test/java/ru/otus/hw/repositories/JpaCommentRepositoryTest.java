package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Репозиторий на основе JPA для работы с комментариями")
@DataJpaTest
@Import({JpaBookRepository.class, JpaCommentRepository.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Transactional(propagation = Propagation.NEVER)
class JpaCommentRepositoryTest {
    @Autowired
    private JpaCommentRepository commentRepository;

    @Autowired
    private JpaBookRepository bookRepository;

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
    @Transactional(readOnly = true)
    void findById(long id) {
        Comment expected = em.find(Comment.class, id);

        Optional<Comment> actual = commentRepository.findById(id);

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    void findByIdNegative() {
        Optional<Comment> comment = commentRepository.findById(Long.MAX_VALUE);

        assertTrue(comment.isEmpty());
    }

    @Test
    void save() {
        Book book = bookRepository.findById(1L).get();
        Comment comment = new Comment(0, "some text", book);

        Comment actual = commentRepository.save(comment);

        assertEquals(comment.getText(), actual.getText());
        assertEquals(comment.getBook().getId(), actual.getBook().getId());
    }

    @Test
    void deleteById() {
        assertTrue(commentRepository.findById(3L).isPresent());
        commentRepository.deleteById(3L);
        assertTrue(commentRepository.findById(3L).isEmpty());
    }
}