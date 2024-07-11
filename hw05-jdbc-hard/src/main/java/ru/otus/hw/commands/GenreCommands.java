package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.GenreService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Command(group = "genre-commands")
public class GenreCommands {

    private final GenreService genreService;

    @Command(description = "Find all genres", command = "genres", alias = "ag")
    public String findAllGenres() {
        return genreService.findAll().stream()
                .map(Genre::toString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }
}
