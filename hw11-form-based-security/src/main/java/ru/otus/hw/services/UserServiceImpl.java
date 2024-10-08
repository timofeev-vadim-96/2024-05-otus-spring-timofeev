package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Authority;
import ru.otus.hw.models.CustomUser;
import ru.otus.hw.repositories.UserRepository;
import ru.otus.hw.services.dto.UserDto;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final AuthorityService authorityService;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDto findById(long id) {
        CustomUser user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User with id = %d not found".formatted(id)));

        return new UserDto(user);
    }

    @Override
    public UserDto findByUserName(String username) {
        CustomUser user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new EntityNotFoundException("User with username: %s is not found".formatted(username)));

        return new UserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(UserDto::new).toList();
    }

    @Override
    @Transactional
    public UserDto create(String username, String password, Set<Long> authoritiesIds) {
        Set<Authority> authorities = authorityService.finalAllByIds(authoritiesIds);

        CustomUser user = new CustomUser(null, username, passwordEncoder.encode(password), authorities);

        CustomUser saved = userRepository.save(user);
        return new UserDto(saved);
    }

    @Override
    @Transactional
    public UserDto update(long id, String username, String password, Set<Long> authoritiesIds) {
        Set<Authority> authorities = authorityService.finalAllByIds(authoritiesIds);

        CustomUser user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User with id = %d not found".formatted(id)));
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setAuthorities(authorities);

        CustomUser updated = userRepository.save(user);
        return new UserDto(updated);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        userRepository.deleteById(id);
    }
}
