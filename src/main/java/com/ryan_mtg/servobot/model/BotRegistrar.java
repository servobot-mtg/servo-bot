package com.ryan_mtg.servobot.model;

import java.util.Arrays;
import java.util.List;

public class BotRegistrar {
    private Bot bot;

    public BotRegistrar(final Bot bot) {
        this.bot = bot;
    }

    public BotHome getBotHome(final String botHomeName) {
        return bot.getHome(botHomeName);
    }

    public BotHome getBotHome(final int botHomeId) {
        return bot.getHome(botHomeId);
    }

    public List<Bot> getBots() {
        return Arrays.asList(bot);
    }

    public BotEditor getBotEditor(final int botHomeId) {
        return bot.getBotEditor();
    }

    public HomeEditor getHomeEditor(final int botHomeId) {
        return bot.getHomeEditor(botHomeId);
    }
}
