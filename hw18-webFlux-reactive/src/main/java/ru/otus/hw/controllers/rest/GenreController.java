package ru.otus.hw.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.services.GenreService;
import ru.otus.hw.services.dto.GenreDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class GenreController {
    private final GenreService genreService;

    @GetMapping("/api/v1/genre")
    public ResponseEntity<List<GenreDto>> getAll() {
        List<GenreDto> genres = genreService.findAll();
        return new ResponseEntity<>(genres, HttpStatus.OK);
    }
}
