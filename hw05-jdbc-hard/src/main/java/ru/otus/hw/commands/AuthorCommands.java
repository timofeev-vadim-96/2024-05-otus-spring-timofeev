package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import ru.otus.hw.models.Author;
import ru.otus.hw.services.AuthorService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Command(group = "author-commands")
public class AuthorCommands {

    private final AuthorService authorService;

    @Command(description = "Find all authors", command = "aa")
    public String findAllAuthors() {
        return authorService.findAll().stream()
                .map(Author::toString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }
}
