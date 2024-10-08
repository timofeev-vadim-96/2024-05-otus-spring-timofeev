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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserPasswordCryptographer {
    private static final Pattern BCRYPT_PATTERN =
            Pattern.compile("\\A\\$2(a|y|b)?\\$(\\d\\d)\\$[./0-9A-Za-z]{53}");

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
        Matcher matcher = BCRYPT_PATTERN.matcher(password);

        boolean matches = matcher.matches();
        log.info("passwords already encoded? {}", matches);
        return matches;
    }
}