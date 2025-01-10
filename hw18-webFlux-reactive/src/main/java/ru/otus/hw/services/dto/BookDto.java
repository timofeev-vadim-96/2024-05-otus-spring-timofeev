package ru.otus.hw.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {
    private String id;

    private String title;

    private AuthorDto author;

    private List<GenreDto> genres;

    public BookDto(Book book) {
        id = book.getId();
        title = book.getTitle();
        author = new AuthorDto(book.getAuthor());
        genres = book.getGenres().stream().map(GenreDto::new).toList();
    }

    public static Book fromDto(BookDto dto) {
        return new Book(
                dto.getId(),
                dto.title,
                AuthorDto.fromDto(dto.getAuthor()),
                dto.getGenres().stream().map(GenreDto::fromDto).collect(Collectors.toSet()));
    }
}