package ru.otus.hw.services;

import ru.otus.hw.models.Authority;

import java.util.Set;

public interface AuthorityService {
    Set<Authority> finalAllByIds(Set<Long> ids);
}
