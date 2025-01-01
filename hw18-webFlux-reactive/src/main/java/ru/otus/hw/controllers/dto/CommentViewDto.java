package ru.otus.hw.controllers.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentViewDto {
    @NotNull
    @Size(min = 10)
    private String text;

    @NotNull
    private String bookId;
}
