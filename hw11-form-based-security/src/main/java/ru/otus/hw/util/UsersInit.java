package ru.otus.hw.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.otus.hw.services.UserService;
import ru.otus.hw.services.dto.UserDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UsersInit {
    private final UserService userService;

    @EventListener(ContextRefreshedEvent.class)
    private void initializeUsers() {
        List<UserDto> users = userService.findAll();
        if (users.isEmpty()) {
            userService.create("admin", "admin", Authority.ROLE_ADMIN);
            userService.create("user", "user", Authority.ROLE_USER);
        }
    }
}
