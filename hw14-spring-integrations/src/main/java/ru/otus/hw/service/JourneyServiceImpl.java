package ru.otus.hw.service;

import org.springframework.stereotype.Service;
import ru.otus.hw.model.Adventurer;

import java.util.Random;

@Service
public class JourneyServiceImpl implements JourneyService {
    private static final int SURVIVAL_CHANCE = 70;

    @Override
    public Adventurer magicalBomb(Adventurer adventurer) {
        System.out.println("Взрыв магической бомбы");
        Random random = new Random();
        int dice = random.nextInt(0, 101);
        if (dice > SURVIVAL_CHANCE) {
            adventurer.setAlive(false);
            System.out.println("Герой погиб от взрыва магической бомбы...");
        }
        return adventurer;
    }
}
