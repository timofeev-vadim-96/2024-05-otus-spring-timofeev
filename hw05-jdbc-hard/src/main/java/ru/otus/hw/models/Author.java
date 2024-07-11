package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Author {
    private long id;

    private String fullName;

    @Override
    public String toString() {
        return "Id: %d, FullName: %s".formatted(id, fullName);
    }
}
