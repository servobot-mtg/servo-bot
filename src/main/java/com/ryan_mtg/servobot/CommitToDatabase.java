package com.ryan_mtg.servobot;

import com.ryan_mtg.servobot.data.factories.CommandTableSerializer;
import com.ryan_mtg.servobot.data.factories.ReactionTableSerializer;
import com.ryan_mtg.servobot.discord.bot.Bot;
import com.ryan_mtg.servobot.discord.bot.BotHome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CommitToDatabase {
    private static Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Autowired
    @Qualifier("bot")
    private Bot bot;

    @Autowired
    private CommandTableSerializer commandTableSerializer;

    @Autowired
    private ReactionTableSerializer reactionTableSerializer;

    public void commit() {
        LOGGER.info("Starting to commit");
        for (BotHome botHome : bot.getHomes()) {
            int botHomeId = botHome.getId();
            commandTableSerializer.saveCommandTable(botHome.getCommandTable(), botHomeId);

            reactionTableSerializer.saveReactionTable(botHome.getReactionTable(), botHomeId);
        }
        LOGGER.info("Done committing");
    }
}
