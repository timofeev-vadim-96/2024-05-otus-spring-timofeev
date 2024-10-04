package ru.otus.hw.model;

import lombok.Getter;
import lombok.Setter;
import ru.otus.hw.util.Profession;
import ru.otus.hw.util.Race;

@Getter
public class Adventurer {
    private String fullName;

    private Profession profession;

    private Race race;

    @Setter
    private boolean isAlive = true;

    @Setter
    private int level = 1;

    public Adventurer(String fullName, Profession profession, Race race) {
        this.fullName = fullName;
        this.profession = profession;
        this.race = race;
    }
}
