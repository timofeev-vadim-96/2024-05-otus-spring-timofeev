package ru.otus.hw.service;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.AppParams;
import ru.otus.hw.model.Adventurer;
import ru.otus.hw.util.Profession;
import ru.otus.hw.util.Race;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TeamMemberShaperImpl implements TeamMemberShaper {
    private final AppParams params;

    private final JourneyGateway journeyGateway;

    private final Faker nameGenerator;

    public void formTeam() {
        final List<Adventurer> adventurers = new ArrayList<>();

        for (int i = 0; i < params.getTeamSize(); i++) {
            adventurers.add(new Adventurer(
                    nameGenerator.name().fullName(),
                    getRandomProfession(),
                    getRandomRace()));
        }

        journeyGateway.startJourney(adventurers);
    }

    private Profession getRandomProfession() {
        Profession[] professions = Profession.values();
        Random random = new Random();
        int index = random.nextInt(0, professions.length);
        return professions[index];
    }

    private Race getRandomRace() {
        Race[] races = Race.values();
        Random random = new Random();
        int index = random.nextInt(0, races.length);
        return races[index];
    }
}
