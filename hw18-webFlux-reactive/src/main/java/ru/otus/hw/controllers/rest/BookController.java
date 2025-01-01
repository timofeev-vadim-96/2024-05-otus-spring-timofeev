package ru.otus.hw.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.controllers.dto.BookViewDto;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.dto.BookDto;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BookController {
    private final BookService bookService;

    @GetMapping(value = "/api/v1/book/{id}")
    public Mono<ResponseEntity<BookDto>> get(@PathVariable("id") String id) {
        return bookService.findById(id)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    @PutMapping("/api/v1/book")
    public Mono<ResponseEntity<BookDto>> update(@Valid @RequestBody BookViewDto book) {
        if (book.getId() == null) {
            log.info("(book update) book id is null");
            return Mono.just(ResponseEntity.badRequest().build());
        }
        return bookService.update(book.getId(), book.getTitle(), book.getAuthorId(), book.getGenres())
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PostMapping("/api/v1/book")
    public Mono<ResponseEntity<BookDto>> create(@Valid @RequestBody BookViewDto book) {
        return bookService.create(book.getTitle(), book.getAuthorId(), book.getGenres())
                .map(b -> new ResponseEntity<>(b, HttpStatus.CREATED))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @GetMapping("/api/v1/book")
    public ResponseEntity<Flux<BookDto>> getAll() {
        return new ResponseEntity<>(bookService.findAll(), HttpStatus.OK);
    }

    @DeleteMapping(value = "/api/v1/book/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") String id) {
        return bookService.deleteById(id).then(Mono.just(ResponseEntity.ok().build()));
    }
}
