package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Genre {
    private long id;

    private String name;

    @Override
    public String toString() {
        return "Id: %d, Name: %s".formatted(id, name);
    }
}
