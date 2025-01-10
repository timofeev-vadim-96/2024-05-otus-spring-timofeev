package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.repositories.ReactiveAuthorRepository;
import ru.otus.hw.services.dto.AuthorDto;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final ReactiveAuthorRepository authorRepository;

    @Override
    @Transactional(readOnly = true)
    public Flux<AuthorDto> findAll() {
        return authorRepository.findAll()
                .map(AuthorDto::new);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AuthorDto> findById(String id) {
        return authorRepository.findById(id).map(AuthorDto::new)
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException("Author with id = %s is not found".formatted(id))));
    }
}
