package ru.otus.hw.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.models.Author;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorDto {
    private String id;

    private String fullName;

    public AuthorDto(Author author) {
        id = author.getId();
        fullName = author.getFullName();
    }

    public static Author fromDto(AuthorDto dto) {
        return new Author(dto.getId(), dto.getFullName());
    }
}