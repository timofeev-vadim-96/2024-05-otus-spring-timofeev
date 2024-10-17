package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.otus.hw.service.TeamMemberShaper;

@Component
@RequiredArgsConstructor
public class AppRunner implements CommandLineRunner {
    private final TeamMemberShaper teamMemberShaper;

    @Override
    public void run(String... args) {
        teamMemberShaper.formTeam();
    }
}
