package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.CommandAlias;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.MessageCommand;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.data.factories.SerializerContainer;
import com.ryan_mtg.servobot.data.models.BotHomeRow;
import com.ryan_mtg.servobot.data.repositories.BotHomeRepository;
import com.ryan_mtg.servobot.events.AlertEvent;
import com.ryan_mtg.servobot.events.BotHomeAlertEvent;
import com.ryan_mtg.servobot.reaction.Reaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;

public class HomeEditor {
    private static Logger LOGGER = LoggerFactory.getLogger(HomeEditor.class);
    private Bot bot;
    private BotHome botHome;
    private SerializerContainer serializers;

    public HomeEditor(final Bot bot, final BotHome botHome) {
        this.bot = bot;
        this.botHome = botHome;
        this.serializers = bot.getSerializers();
    }

    @Transactional
    public void setTimeZone(final String timeZone) {
        botHome.setTimeZone(timeZone);
        botHome.getReactionTable().setTimeZone(timeZone);
        botHome.getCommandTable().setTimeZone(timeZone);

        bot.getAlertQueue().update(botHome);

        BotHomeRepository botHomeRepository = serializers.getBotHomeRepository();
        BotHomeRow botHomeRow = botHomeRepository.findById(botHome.getId());
        botHomeRow.setTimeZone(timeZone);
        botHomeRepository.save(botHomeRow);
    }

    public boolean secureCommand(int commandId, boolean secure) {
        Command command = botHome.getCommandTable().secureCommand(commandId, secure);
        serializers.getCommandSerializer().saveCommand(botHome.getId(), command);
        return command.isSecure();
    }

    public boolean secureReaction(int reactionId, boolean secure) {
        Reaction reaction = botHome.getReactionTable().secureReaction(reactionId, secure);
        serializers.getReactionSerializer().saveReaction(botHome.getId(), reaction);
        return reaction.isSecure();
    }

    public void addCommand(final String alias, final MessageCommand command) {
        CommandTable commandTable = botHome.getCommandTable();
        LOGGER.info("Adding command " + alias);
        if (commandTable.hasAlias(alias)) {
            LOGGER.info("Already seen :( " + alias);
            throw new IllegalArgumentException("Already has alias " + alias);
        }
        CommandAlias commandAlias = new CommandAlias(CommandAlias.UNREGISTERED_ID, alias);
        botHome.getCommandTable().registerCommand(command, commandAlias);

        LOGGER.info("Saving command");
        serializers.getCommandSerializer().saveCommand(botHome.getId(), command);
        LOGGER.info("Saved command with id: " + command.getId());
        serializers.getCommandSerializer().saveCommandAlias(command.getId(), commandAlias);
    }

    public void alert(final String alertToken) {
        Home home = new MultiServiceHome(botHome.getServiceHomes(), this);
        AlertEvent alertEvent = new BotHomeAlertEvent(botHome.getId(), alertToken, home);
        botHome.getListener().onAlert(alertEvent);
    }

    public Permission setCommandPermission(final int commandId, final Permission permission) {
        Command command = botHome.getCommandTable().setCommandPermission(commandId, permission);
        serializers.getCommandSerializer().saveCommand(botHome.getId(), command);
        return command.getPermission();
    }
}
