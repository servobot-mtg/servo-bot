package com.ryan_mtg.servobot.model;

import java.util.Collections;
import java.util.List;

public class BotRegistrar {
    //TODO: make this usable with multiple bots.
    private Bot bot;

    public BotRegistrar(final Bot bot) {
        this.bot = bot;
    }

    public Bot getDefaultBot() {
        return bot;
    }

    public List<Bot> getBots() {
        return Collections.singletonList(bot);
    }

    public BotEditor getBotEditor(final int botHomeId) {
        return bot.getBotEditor();
    }

    public BotEditor getBotEditor(final String botName) {
        return bot.getBotEditor();
    }

    public BotHome getBotHome(final String botHomeName) {
        return bot.getHome(botHomeName);
    }

    public BotHome getBotHome(final int botHomeId) {
        return bot.getHome(botHomeId);
    }

    public HomeEditor getHomeEditor(final int botHomeId) {
        return bot.getHomeEditor(botHomeId);
    }
}
