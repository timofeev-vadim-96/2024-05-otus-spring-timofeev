package ru.otus.hw.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.CustomUser;
import ru.otus.hw.repositories.UserRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserPasswordCryptographer {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @EventListener(ContextRefreshedEvent.class)
    private void encodeUsersPasswords() {
        List<CustomUser> users = userRepository.findAll();

        if (!users.isEmpty()) {
            String password = users.get(0).getPassword();
            if (isAlreadyEncoded(password)) {
                return;
            }
        }

        log.info("encoding passwords...");
        for (CustomUser user : users) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
        }
    }

    private boolean isAlreadyEncoded(String password) {
        String encoded = passwordEncoder.encode(password);
        boolean matches = password.equals(encoded);
        log.info("passwords already encoded? {}", matches);
        return matches;
    }
}
