package ru.otus.hw.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.models.Book;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {
    private Long id;

    private String title;

    private AuthorDto author;

    private List<GenreDto> genres;

    public BookDto(Book book) {
        id = book.getId();
        title = book.getTitle();
        author = new AuthorDto(book.getAuthor());
        genres = book.getGenres().stream().map(GenreDto::new).toList();
    }
}