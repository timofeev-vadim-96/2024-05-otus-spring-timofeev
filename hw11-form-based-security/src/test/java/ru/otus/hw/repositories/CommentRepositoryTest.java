package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Репозиторий на основе JPA для работы с комментариями")
@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @Transactional(readOnly = true)
    void findAllByBookId() {
        Comment comment = em.find(Comment.class, 1L);
        Book book = bookRepository.findById(1L).get();

        List<Comment> allByBookId = commentRepository.findAllByBook(book);

        assertFalse(allByBookId.isEmpty());
        assertTrue(allByBookId.stream().anyMatch(c -> c.getBook().getId() == 1L));
        assertTrue(allByBookId.contains(comment));
    }
}