package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.services.dto.CommentDto;

@Component
@RequiredArgsConstructor
public class CommentConverter {
    public String commentToString(CommentDto comment) {
        return "Id: %d, text: %s, book's title: %s".formatted(
                comment.getId(),
                comment.getText(),
                comment.getBook().getTitle());
    }
}