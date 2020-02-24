package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.commands.AddCommand;
import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.CommandAlias;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.CommandTableEdit;
import com.ryan_mtg.servobot.commands.DeleteCommand;
import com.ryan_mtg.servobot.commands.MessageCommand;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.commands.TextCommand;
import com.ryan_mtg.servobot.controllers.ApiController.CreateBotHomeRequest;
import com.ryan_mtg.servobot.controllers.ApiController.TextCommandRequest;
import com.ryan_mtg.servobot.data.factories.SerializerContainer;
import com.ryan_mtg.servobot.data.factories.ServiceSerializer;
import com.ryan_mtg.servobot.data.factories.UserSerializer;
import com.ryan_mtg.servobot.data.models.BotHomeRow;
import com.ryan_mtg.servobot.data.models.ServiceHomeRow;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.reaction.ReactionTable;
import com.ryan_mtg.servobot.model.storage.StorageTable;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import com.ryan_mtg.servobot.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BotEditor {
    private static Logger LOGGER = LoggerFactory.getLogger(BotEditor.class);
    private Bot bot;
    private SerializerContainer serializers;

    public BotEditor(final Bot bot) {
        this.bot = bot;
        this.serializers = bot.getSerializers();
    }

    public void setArenaUsername(final int userId, final String username)  {
        serializers.getUserSerializer().setArenaUsername(userId, username);
    }

    private void validateNewCommandName(final Set<String> commandNames, final String commandName)
            throws BotErrorException {
        if (commandName != null) {
            CommandAlias.validateAlias(commandName);
            if (commandNames.contains(commandName)) {
                throw new BotErrorException(
                        String.format("%s is a duplicated command name.", commandName));
            }
            commandNames.add(commandName);
        }
    }

    @Transactional
    public BotHome createBotHome(final int userId, final CreateBotHomeRequest request) throws BotErrorException {
        UserSerializer userSerializer = serializers.getUserSerializer();
        User user = userSerializer.lookupById(userId);
        if (!user.hasInvite()) {
            throw new BotErrorException(String.format("%s can't create home without an invite.", user.getName()));
        }

        if (!userSerializer.getHomesStreamed(userId).isEmpty()) {
            throw new BotErrorException(String.format("%s already has a home.", user.getName()));
        }

        if (user.getTwitchUsername().isEmpty()) {
            throw new BotErrorException(String.format("%s does not have a twitch name.", user.getName()));
        }

        Set<String> commandNames = new HashSet<>();
        validateNewCommandName(commandNames, request.getAddCommandName());
        validateNewCommandName(commandNames, request.getDeleteCommandName());
        validateNewCommandName(commandNames, request.getShowCommandsName());
        for (TextCommandRequest textCommandRequest : request.getTextCommands()) {
            validateNewCommandName(commandNames, textCommandRequest.getName());
        }

        BotHomeRow botHomeRow = new BotHomeRow();
        String botName = bot.getName();
        botHomeRow.setBotName(botName);
        String homeName = user.getTwitchUsername();
        botHomeRow.setHomeName(homeName);
        String timeZone = request.getTimeZone();
        botHomeRow.setTimeZone(timeZone);

        serializers.getBotHomeRepository().save(botHomeRow);
        int botHomeId = botHomeRow.getId();

        List<Book> books = new ArrayList<>();

        CommandTable commandTable = new CommandTable(false);
        ReactionTable reactionTable = new ReactionTable();
        StorageTable storageTable = new StorageTable();

        Map<Integer, ServiceHome> serviceHomes = new HashMap<>();

        Service twitchService = bot.getService(TwitchService.TYPE);
        ServiceHomeRow serviceHomeRow = new ServiceHomeRow();
        serviceHomeRow.setBotHomeId(botHomeId);
        serviceHomeRow.setServiceType(TwitchService.TYPE);
        serviceHomeRow.setLong(user.getTwitchId());
        serializers.getServiceHomeRepository().save(serviceHomeRow);
        ServiceHome twitchServiceHome = serializers.getServiceSerializer().createServiceHome(serviceHomeRow, twitchService);
        serviceHomes.put(TwitchService.TYPE, twitchServiceHome);

        CommandTableEdit commandTableEdit = new CommandTableEdit();
        if (request.getAddCommandName() != null) {
            MessageCommand addCommand = new AddCommand(Command.UNREGISTERED_ID, Command.DEFAULT_FLAGS, Permission.MOD);
            commandTableEdit.merge(commandTable.addCommand(request.getAddCommandName(), addCommand));
        }
        if (request.getDeleteCommandName() != null) {
            MessageCommand deleteCommand =
                    new DeleteCommand(Command.UNREGISTERED_ID, Command.DEFAULT_FLAGS, Permission.MOD);
            commandTableEdit.merge(commandTable.addCommand(request.getDeleteCommandName(), deleteCommand));
        }
        if (request.getShowCommandsName() != null) {
            String text = String.format("Commands are listed at http://servobot.info/home/%s", homeName);
            MessageCommand showCommandsCommand =
                    new TextCommand(Command.UNREGISTERED_ID, Command.DEFAULT_FLAGS, Permission.ANYONE, text);
            commandTableEdit.merge(commandTable.addCommand(request.getShowCommandsName(), showCommandsCommand));
        }
        for (TextCommandRequest textCommandRequest : request.getTextCommands()) {
            MessageCommand textCommand = new TextCommand(
                    Command.UNREGISTERED_ID, Command.DEFAULT_FLAGS, Permission.ANYONE, textCommandRequest.getValue());
            commandTableEdit.merge(commandTable.addCommand(textCommandRequest.getName(), textCommand));
        }
        serializers.getCommandTableSerializer().commit(botHomeId, commandTableEdit);

        user.removeInvite();
        userSerializer.saveUser(user);
        userSerializer.setStreamerStatus(user.getId(), botHomeId);

        List<GameQueue> gameQueues = new ArrayList<>();
        BotHome botHome = new BotHome(botHomeId, homeName, botName, timeZone, commandTable, reactionTable, storageTable,
                serviceHomes, books, gameQueues);
        bot.addHome(botHome);
        botHome.start(bot.getHomeEditor(botHomeId), bot.getAlertQueue());
        return botHome;
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
