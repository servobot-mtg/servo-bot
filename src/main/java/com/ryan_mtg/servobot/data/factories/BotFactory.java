package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.BotHomeRow;
import com.ryan_mtg.servobot.data.models.BotRow;
import com.ryan_mtg.servobot.data.repositories.BotHomeRepository;
import com.ryan_mtg.servobot.discord.bot.Bot;
import com.ryan_mtg.servobot.discord.bot.BotHome;
import com.ryan_mtg.servobot.discord.bot.Streamer;
import com.ryan_mtg.servobot.discord.commands.CommandTable;
import com.ryan_mtg.servobot.discord.reaction.ReactionTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BotFactory {
    private static Logger LOGGER = LoggerFactory.getLogger(BotFactory.class);

    @Autowired
    private BotHomeRepository botHomeRepository;

    @Autowired
    private CommandTableSerializer commandTableSerializer;

    @Autowired
    private ReactionTableSerializer reactionTableSerializer;

    public Bot createBot(final BotRow botRow) {
        Bot mooseBot = new Bot(botRow.getToken());

        Iterable<BotHomeRow> botHomeRows = botHomeRepository.findAll();

        for (BotHomeRow botHomeRow : botHomeRows) {
            String homeName = botHomeRow.getHomeName();
            Streamer streamer  = new Streamer(botHomeRow.getStreamerId());
            int botHomeId = botHomeRow.getId();
            CommandTable commandTable = commandTableSerializer.createCommandTable(botHomeId);
            ReactionTable reactionTable = reactionTableSerializer.createReactionTable(botHomeId);
            BotHome botHome = new BotHome(homeName, botHomeId, streamer, commandTable, reactionTable);
            mooseBot.addHome(botHome);
        }

        return mooseBot;
    }
}
