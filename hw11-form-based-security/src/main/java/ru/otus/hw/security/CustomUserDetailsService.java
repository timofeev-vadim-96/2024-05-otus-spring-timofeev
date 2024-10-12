package ru.otus.hw.security;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.CustomUser;
import ru.otus.hw.repositories.UserRepository;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUser user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new EntityNotFoundException("user with username: %s not found".formatted(username)));

        return new User(
                user.getUsername(),
                user.getPassword(),
                user.getAuthorities().stream()
                        .map(a -> new SimpleGrantedAuthority(a.getAuthority()))
                        .collect(Collectors.toSet()));
    }
}
