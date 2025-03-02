package ru.otus.hw.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.models.Genre;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenreDto {
    private String id;

    private String name;

    public GenreDto(Genre genre) {
        id = genre.getId();
        name = genre.getName();
    }

    public static Genre fromDto(GenreDto dto) {
        return new Genre(dto.getId(), dto.getName());
    }
}