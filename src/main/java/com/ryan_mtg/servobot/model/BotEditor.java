package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.data.factories.SerializerContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
}
