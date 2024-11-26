package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.services.dto.CommentDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findAllByBookId(long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id = %d not found".formatted(bookId)));

        return commentRepository.findAllByBook(book).stream().map(CommentDto::new).toList();
    }

    @Override
    @Transactional
    public CommentDto create(String text, long bookId) {
        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id = %d not found".formatted(bookId)));

        Comment comment = new Comment(null, text, book);
        Comment created = commentRepository.save(comment);

        return new CommentDto(created);
    }

    @Override
    @Transactional
    public CommentDto update(String text, long id) {
        Comment comment = commentRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id = %d not found".formatted(id)));
        comment.setText(text);
        Comment updated = commentRepository.save(comment);

        return new CommentDto(updated);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }
}