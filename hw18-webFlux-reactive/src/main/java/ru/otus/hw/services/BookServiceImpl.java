package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.ReactiveBookRepository;
import ru.otus.hw.repositories.ReactiveCommentRepository;
import ru.otus.hw.services.dto.AuthorDto;
import ru.otus.hw.services.dto.BookDto;
import ru.otus.hw.services.dto.GenreDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
@Slf4j
public class BookServiceImpl implements BookService {
    private final AuthorService authorService;

    private final GenreService genreService;

    private final ReactiveBookRepository bookRepository;

    private final ReactiveCommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true)
    public Mono<BookDto> findById(String id) {
        return bookRepository.findById(id).map(BookDto::new)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Book with id = %s is not found".formatted(id))));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<BookDto> findAll() {
        return bookRepository.findAll()
                .map(BookDto::new)
                .doOnEach(book -> log.info("Found book: {}", book.get()));
    }

    @Override
    @Transactional
    public Mono<BookDto> create(String title, String authorId, Set<String> genresIds) {
        return save(null, title, authorId, genresIds).map(BookDto::new)
                .doOnError(e -> log.error(e.getMessage()));
    }

    @Override
    @Transactional
    public Mono<BookDto> update(String id, String title, String authorId, Set<String> genresIds) {
        return save(id, title, authorId, genresIds).map(BookDto::new)
                .doOnError(e -> log.error(e.getMessage()));
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(String id) {
        commentRepository.deleteAllByBookId(id);
        return bookRepository.deleteById(id);
    }

    private Mono<Book> save(String id, String title, String authorId, Set<String> genresIds) {
        if (isEmpty(genresIds)) {
            return Mono.error(new IllegalArgumentException("Genres ids must not be null"));
        }

        return Mono.zip(authorService.findById(authorId), getGenres(genresIds))
                .flatMap(tuple -> Mono.defer(() -> {
                    if (id != null) {
                        return bookRepository.findById(id)
                                .switchIfEmpty(Mono.error(
                                        new EntityNotFoundException("Book with id = %s is not found".formatted(id))))
                                .flatMap(book -> {
                                    book.setTitle(title);
                                    book.setAuthor(AuthorDto.fromDto(tuple.getT1()));
                                    book.setGenres(new HashSet<>(tuple.getT2()));
                                    return bookRepository.save(book);
                                });
                    } else {
                        Book book = new Book(null, title,
                                AuthorDto.fromDto(tuple.getT1()),
                                new HashSet<>(tuple.getT2()));
                        return bookRepository.save(book);
                    }
                }));
    }

    private Mono<List<Genre>> getGenres(Set<String> genresIds) {
        return genreService.findAllByIds(genresIds).collectList()
                .flatMap(genreDtos -> {
                    return Mono.just(genreDtos.stream()
                            .map(GenreDto::fromDto)
                            .toList());
                })
                .flatMap(genres -> {
                    if (genres.size() != genresIds.size()) {
                        return Mono.error(new EntityNotFoundException(
                                "One or all genres with ids %s not found".formatted(genresIds)));
                    } else {
                        return Mono.just(genres);
                    }
                });
    }
}