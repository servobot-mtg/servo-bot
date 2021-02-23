package com.ryan_mtg.servobot.game.fortune;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

@Component
public class FortuneCookieResponder {
    private static final String FORTUNE_FILE = "fortune/fortunes.txt";

    public String respond() {
        List<String> fortunes = readFortunes();
        return fortunes.get(new Random().nextInt(fortunes.size()));
    }

    private List<String> readFortunes() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(FORTUNE_FILE);
        Scanner scanner = new Scanner(inputStream);
        List<String> fortunes = new ArrayList<>();
        while (scanner.hasNextLine()) {
            fortunes.add(scanner.nextLine().trim());
        }
        return fortunes;
    }
}