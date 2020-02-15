package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.data.factories.SerializerContainer;
import com.ryan_mtg.servobot.events.BotErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotEditor {
    private static Logger LOGGER = LoggerFactory.getLogger(BotEditor.class);
    private Bot bot;
    private SerializerContainer serializers;

    public BotEditor(final Bot bot) {
        this.bot = bot;
        this.serializers = bot.getSerializers();
    }

    public void setArenaUsername(final User user, final String username)  {
        serializers.getUserSerializer().setArenaUsername(user.getHomedUser().getId(), username);
    }

    public void stopHome(final int botHomeId) {
        bot.getHome(botHomeId).stop(bot.getAlertQueue());
    }

    public void restartHome(final int botHomeId) throws BotErrorException {
        bot.removeHome(bot.getHome(botHomeId));

        BotHome botHome = this.serializers.getBotFactory().createBotHome(botHomeId);
        bot.addHome(botHome);
        botHome.start(bot.getHomeEditor(botHomeId), bot.getAlertQueue());
    }
}
