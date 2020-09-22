package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.error.SystemError;

import java.util.List;

public class BotRegistrar {
    private static int DEFAULT_BOT_CONTEXT_ID = -1;

    private List<Bot> bots;

    public BotRegistrar(final List<Bot> bots) {
        this.bots = bots;
    }

    public Bot getDefaultBot() {
        Bot bot = getBot(DEFAULT_BOT_CONTEXT_ID);
        if (bot == null) {
            throw new SystemError("No default Bot?");
        }
        return bot;
    }

    public List<Bot> getBots() {
        return bots;
    }

    public BotEditor getBotEditor(final int contextId) {
        return getBot(contextId).getBotEditor();
    }

    public Bot getBot(final String botName) {
        for (Bot bot : bots) {
            if (bot.getName().equalsIgnoreCase(botName)) {
                return bot;
            }
        }
        return null;
    }

    public BotEditor getBotEditor(final String botName) {
        return getBot(botName).getBotEditor();
    }

    public BotHome getBotHome(final String botHomeName) {
        for (Bot bot : bots) {
            BotHome botHome = bot.getHome(botHomeName);
            if (botHome != null) {
                return botHome;
            }
        }
        return null;
    }

    public BotHome getBotHome(final int botHomeId) {
        return getBot(botHomeId).getHome(botHomeId);
    }

    public HomeEditor getHomeEditor(final int botHomeId) {
        return getBot(botHomeId).getHomeEditor(botHomeId);
    }

    private Bot getBot(final int contextId) {
        for (Bot bot : bots) {
            if (bot.getContextId() == contextId) {
                return bot;
            }

            if (contextId > 0) {
                BotHome botHome = bot.getHome(contextId);
                if (botHome != null) {
                    return bot;
                }
            }
        }
        return null;
    }
}
