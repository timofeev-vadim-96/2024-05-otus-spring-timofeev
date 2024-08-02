package ru.otus.hw.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.models.Comment;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;

    private String text;

    private BookDto book;

    public CommentDto(Comment comment) {
        id = comment.getId();
        text = comment.getText();
        book = new BookDto(comment.getBook());
    }
}