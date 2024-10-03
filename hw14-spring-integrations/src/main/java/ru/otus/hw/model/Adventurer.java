package ru.otus.hw.model;

import lombok.Getter;
import lombok.Setter;
import ru.otus.hw.util.Profession;
import ru.otus.hw.util.Race;

@Getter
public class Adventurer {
    private Profession profession;

    private Race race;

    @Setter
    private boolean isAlive = true;

    @Setter
    private int level = 1;

    public Adventurer(Profession profession, Race race) {
        this.profession = profession;
        this.race = race;
    }
}
