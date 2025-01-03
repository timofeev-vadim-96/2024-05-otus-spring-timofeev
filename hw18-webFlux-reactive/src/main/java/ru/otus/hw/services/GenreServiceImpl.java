package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import ru.otus.hw.repositories.ReactiveGenreRepository;
import ru.otus.hw.services.dto.GenreDto;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final ReactiveGenreRepository genreRepository;

    @Override
    @Transactional(readOnly = true)
    public Flux<GenreDto> findAll() {
        return genreRepository.findAll().map(GenreDto::new);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<GenreDto> findAllByIds(Set<String> ids) {
        return genreRepository.findAllByIds(ids).map(GenreDto::new);
    }
}