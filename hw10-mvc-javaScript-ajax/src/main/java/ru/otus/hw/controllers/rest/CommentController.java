package ru.otus.hw.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.otus.hw.controllers.dto.CommentViewDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.dto.CommentDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/api/v1/comment")
    public ResponseEntity<CommentDto> create(@Valid @RequestBody CommentViewDto comment) {
        try {
            CommentDto created = commentService.create(comment.getText(), comment.getBookId());
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/api/v1/comment/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        commentService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/api/v1/comment/{bookId}")
    public ResponseEntity<List<CommentDto>> getAll(@PathVariable("bookId") long bookId) {
        try {
            List<CommentDto> comments = commentService.findAllByBookId(bookId);
            return new ResponseEntity<>(comments, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
