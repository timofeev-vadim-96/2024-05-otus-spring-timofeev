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

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findAllByBookId(String bookId) {
        return commentRepository.findAllByBookId(bookId);
    }

    @Override
    @Transactional
    public Comment insert(String text, String bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id = %s not found".formatted(bookId)));

        Comment comment = new Comment(null, text, book);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment update(String text, String id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id = %s not found".formatted(id)));
        comment.setText(text);

        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        commentRepository.deleteById(id);
    }
}
