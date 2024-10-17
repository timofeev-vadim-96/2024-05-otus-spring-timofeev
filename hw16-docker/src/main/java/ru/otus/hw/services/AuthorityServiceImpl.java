package ru.otus.hw.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Authority;
import ru.otus.hw.repositories.AuthorityRepository;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class AuthorityServiceImpl implements AuthorityService {
    private final AuthorityRepository authorityRepository;

    @Override
    public Set<Authority> finalAllByIds(Set<Long> ids) {
        Set<Authority> authorities = authorityRepository.findAllByIds(ids);

        if (ids.size() != authorities.size()) {
            throw new EntityNotFoundException("One or more authorities not found");
        }

        return authorities;
    }
}
