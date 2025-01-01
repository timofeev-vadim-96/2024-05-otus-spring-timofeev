package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.ReactiveBookRepository;
import ru.otus.hw.repositories.ReactiveCommentRepository;
import ru.otus.hw.services.dto.CommentDto;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final ReactiveCommentRepository commentRepository;

    private final ReactiveBookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public Flux<CommentDto> findAllByBookId(String bookId) {
        return commentRepository.findAllByBookId(bookId)
                .map(CommentDto::new);
    }

    @Override
    @Transactional
    public Mono<CommentDto> create(String text, String bookId) {
        return bookRepository
                .findById(bookId)
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException("Book with id = %s is not found".formatted(bookId))))
                .flatMap(book -> {
                    Comment comment = new Comment(null, text, book);
                    return commentRepository.save(comment);
                })
                .map(CommentDto::new);
    }

    @Override
    @Transactional
    public Mono<CommentDto> update(String text, String id) {
        return commentRepository
                .findById(id)
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException("Comment with id = %s is not found".formatted(id))))
                .flatMap(comment -> {
                    comment.setText(text);
                    return commentRepository.save(comment);
                }).map(CommentDto::new);
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(String id) {
        return commentRepository.deleteById(id);
    }
}