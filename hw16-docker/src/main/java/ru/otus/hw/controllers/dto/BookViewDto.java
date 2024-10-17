package ru.otus.hw.controllers.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookViewDto {
    @Nullable
    private Long id;

    @NotNull
    @Size(min = 3)
    private String title;

    @NotNull
    private Long authorId;

    @NotEmpty
    private Set<Long> genres;
}
