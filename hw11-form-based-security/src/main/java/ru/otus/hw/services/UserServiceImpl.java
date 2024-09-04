package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.CustomUser;
import ru.otus.hw.repositories.UserRepository;
import ru.otus.hw.services.dto.UserDto;
import ru.otus.hw.util.Authority;

import java.util.List;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDto findById(long id) {
        CustomUser user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User with id = %d not found".formatted(id)));

        return new UserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(UserDto::new).toList();
    }

    @Override
    @Transactional
    public UserDto create(String principal, String credentials, Authority authority) {
        CustomUser user = new CustomUser(null, principal, passwordEncoder.encode(credentials), authority.name());

        CustomUser saved = userRepository.save(user);
        return new UserDto(saved);
    }

    @Override
    @Transactional
    public UserDto update(long id, String principal, String credentials, Authority authority) {
        CustomUser user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User with id = %d not found".formatted(id)));
        user.setPrincipal(principal);
        user.setCredentials(passwordEncoder.encode(credentials));
        user.setAuthority(authority.name());

        CustomUser updated = userRepository.save(user);
        return new UserDto(updated);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        userRepository.deleteById(id);
    }
}
