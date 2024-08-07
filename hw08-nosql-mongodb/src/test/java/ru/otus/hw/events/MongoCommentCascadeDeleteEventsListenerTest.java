package ru.otus.hw.events;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@Import(MongoCommentCascadeDeleteEventsListener.class)
class MongoCommentCascadeDeleteEventsListenerTest {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @Description("Должен удалить все комментарии при удалении книги, на которую они ссылаются")
    void onAfterDelete() {
        Book book = bookRepository.findAll().get(0);
        List<Comment> comments = commentRepository.findAllByBookId(book.getId());
        assertFalse(comments.isEmpty());

        bookRepository.deleteById(book.getId());

        List<Comment> expectedEmpty = commentRepository.findAllByBookId(book.getId());
        assertTrue(expectedEmpty.isEmpty());
    }
}