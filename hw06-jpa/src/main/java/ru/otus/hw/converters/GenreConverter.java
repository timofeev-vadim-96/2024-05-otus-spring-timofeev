package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.models.Genre;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class GenreConverter {
    public String genreToString(Genre genre) {
        return "Id: %d, Name: %s".formatted(genre.getId(), genre.getName());
    }

    public String genresToString(Collection<Genre> genres) {
        return genres.stream().map(this::genreToString)
                .collect(Collectors.joining(", "));
    }
}
