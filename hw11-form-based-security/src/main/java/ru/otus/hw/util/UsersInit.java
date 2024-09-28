package ru.otus.hw.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.otus.hw.config.UserConfig;
import ru.otus.hw.services.UserService;
import ru.otus.hw.services.dto.UserDto;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UsersInit {
    private final UserService userService;

    private final UserConfig userConfig;

    @EventListener(ContextRefreshedEvent.class)
    private void initializeUsers() {
        List<UserDto> users = userService.findAll();
        if (users.isEmpty()) {
            userService.create(userConfig.getAdminLogin(), userConfig.getAdminPassword(), Set.of(1L));
            userService.create(userConfig.getUserLogin(), userConfig.getUserPassword(), Set.of(2L));
        }
    }
}
