package ru.otus.hw.controllers.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.otus.hw.controllers.dto.CommentViewDto;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.dto.CommentDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/api/v1/comment")
    public Mono<ResponseEntity<CommentDto>> create(@Valid @RequestBody CommentViewDto comment) {
        return commentService.create(comment.getText(), comment.getBookId())
                .map(c -> new ResponseEntity<>(c, HttpStatus.CREATED))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @DeleteMapping("/api/v1/comment/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") String id) {
        return commentService.deleteById(id)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @GetMapping("/api/v1/comment/{bookId}")
    public Mono<ResponseEntity<List<CommentDto>>> getAllByBookId(@NotNull @PathVariable("bookId") String bookId) {
        return commentService.findAllByBookId(bookId)
                .collectList()
                .map(comments -> new ResponseEntity<>(comments, HttpStatus.OK));
    }
}
