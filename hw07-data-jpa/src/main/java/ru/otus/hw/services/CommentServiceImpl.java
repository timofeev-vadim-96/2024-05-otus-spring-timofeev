package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findAllByBookId(long bookId) {
        return commentRepository.findAllByBookId(bookId);
    }

    @Override
    @Transactional
    public Comment insert(String text, long bookId) {
        Optional<Book> book = bookRepository.findById(bookId);
        if (book.isEmpty()) {
            throw new EntityNotFoundException("Book with id = %d not found".formatted(bookId));
        }

        Comment comment = new Comment(0, text, book.get());
        return commentRepository.save(comment);
    }

    @Override
    public Comment update(String text, long id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isEmpty()) {
            throw new EntityNotFoundException("Comment with id = %d not found".formatted(id));
        }
        Comment comment = optionalComment.get();
        comment.setText(text);
        return commentRepository.save(comment);
    }

    @Override
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }
}
