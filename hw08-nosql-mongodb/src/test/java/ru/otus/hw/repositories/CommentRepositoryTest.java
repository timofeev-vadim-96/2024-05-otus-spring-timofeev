package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Репозиторий на основе MongoRepo для работы с комментариями")
@DataMongoTest
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void findAllByBookId() {
        List<Book> books = bookRepository.findAll();
        Book firstBook = books.get(0);

        List<Comment> allByBookId = commentRepository.findAllByBookId(firstBook.getId());

        assertFalse(allByBookId.isEmpty());
        assertTrue(allByBookId.stream().allMatch(c -> c.getBook().equals(firstBook)));
    }

    @Test
    void deleteAllByBook_Id() {
        List<Book> books = bookRepository.findAll();
        Book firstBook = books.get(0);

        List<Comment> allByBookId = commentRepository.findAllByBookId(firstBook.getId());
        assertFalse(allByBookId.isEmpty());
        commentRepository.deleteAllByBookId(firstBook.getId());
        List<Comment> mustBeEmptyList = commentRepository.findAllByBookId(firstBook.getId());

        assertTrue(mustBeEmptyList.isEmpty());
    }
}