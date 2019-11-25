package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.model.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class FactsCommand extends MessageCommand {
    public static final int TYPE = 2;

    private static Random random;
    private List<String> facts;
    private String fileName;

    public FactsCommand(final int id, final boolean secure, final Permission permission, final String name,
                        final Random random) {
        super(id, secure, permission);
        facts = readFacts(String.format("/facts/%s.txt", name));
        fileName = name;
        this.random = random;
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public String getName() {
        return fileName + " Command";
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitFactsCommand(this);
    }

    @Override
    public void perform(final Message message, final String arguments) {
        String fact = facts.get(random.nextInt(facts.size()));
        MessageCommand.say(message, fact);
    }

    public String getFileName() {
        return fileName;
    }

    private static List<String> readFacts(final String resource) {
        Scanner scanner = new Scanner(FactsCommand.class.getResourceAsStream(resource));
        List<String> facts = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.length() > 0) {
                facts.add(line);
            }
        }
        return facts;
    }
}
