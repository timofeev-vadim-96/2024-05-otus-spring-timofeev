package ru.otus.hw.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.dto.AuthorDto;

@RequiredArgsConstructor
@RestController
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping("/api/v1/author")
    public ResponseEntity<Flux<AuthorDto>> getAll() {
        return new ResponseEntity<>(authorService.findAll(), HttpStatus.OK);
    }
}
