package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
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
}
