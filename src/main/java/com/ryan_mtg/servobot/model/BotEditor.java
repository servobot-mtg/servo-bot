package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.chat.AddCommand;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.RateLimit;
import com.ryan_mtg.servobot.commands.trigger.CommandAlias;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.CommandTableEdit;
import com.ryan_mtg.servobot.commands.chat.DeleteCommand;
import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.commands.chat.TextCommand;
import com.ryan_mtg.servobot.controllers.ApiController.CreateBotHomeRequest;
import com.ryan_mtg.servobot.controllers.ApiController.TextCommandRequest;
import com.ryan_mtg.servobot.data.factories.SerializerContainer;
import com.ryan_mtg.servobot.data.models.BotHomeRow;
import com.ryan_mtg.servobot.data.models.ServiceHomeRow;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.books.BookTable;
import com.ryan_mtg.servobot.model.giveaway.Giveaway;
import com.ryan_mtg.servobot.model.reaction.ReactionTable;
import com.ryan_mtg.servobot.model.storage.StorageTable;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import com.ryan_mtg.servobot.twitch.model.TwitchUserStatus;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.user.HomedUserTable;
import com.ryan_mtg.servobot.user.User;
import com.ryan_mtg.servobot.user.UserTable;
import com.ryan_mtg.servobot.utility.Strings;
import com.ryan_mtg.servobot.utility.Validation;
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

    public void setArenaUsername(final int userId, final String username) throws BotErrorException {
        Validation.validateStringLength(username, Validation.MAX_USERNAME_LENGTH, "Arena username");
        serializers.getUserTable().modifyUser(userId, user -> user.setArenaUsername(username));
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

    @Transactional(rollbackOn = BotErrorException.class)
    public BotHome createBotHome(final int userId, final CreateBotHomeRequest request) throws BotErrorException {
        try {
            UserTable userTable = serializers.getUserTable();
            User user = userTable.getById(userId);

            if (!user.hasInvite()) {
                throw new BotErrorException(String.format("%s can't create home without an invite.", user.getName()));
            }

            if (!userTable.getHomesStreamed(userId).isEmpty()) {
                throw new BotErrorException(String.format("%s already has a home.", user.getName()));
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

            CommandTable commandTable = new CommandTable(false);
            ReactionTable reactionTable = new ReactionTable();
            StorageTable storageTable = new StorageTable();

            Map<Integer, ServiceHome> serviceHomes = new HashMap<>();

            Service twitchService = bot.getService(TwitchService.TYPE);
            ServiceHomeRow serviceHomeRow = new ServiceHomeRow();
            serviceHomeRow.setBotHomeId(botHomeId);
            serviceHomeRow.setServiceType(TwitchService.TYPE);
            serviceHomeRow.setLongValue(user.getTwitchId());
            serializers.getServiceHomeRepository().save(serviceHomeRow);
            ServiceHome twitchServiceHome =
                    serializers.getServiceSerializer().createServiceHome(serviceHomeRow, twitchService);
            serviceHomes.put(TwitchService.TYPE, twitchServiceHome);

            CommandTableEdit commandTableEdit = new CommandTableEdit();
            if (!Strings.isBlank(request.getAddCommandName())) {
                MessageCommand addCommand = new AddCommand(Command.UNREGISTERED_ID,
                        new CommandSettings(Command.DEFAULT_FLAGS, Permission.MOD, new RateLimit()));
                commandTableEdit.merge(commandTable.addCommand(request.getAddCommandName(), addCommand));
            }

            if (!Strings.isBlank(request.getDeleteCommandName())) {
                MessageCommand deleteCommand = new DeleteCommand(Command.UNREGISTERED_ID,
                    new CommandSettings(Command.DEFAULT_FLAGS, Permission.MOD, new RateLimit()));
                commandTableEdit.merge(commandTable.addCommand(request.getDeleteCommandName(), deleteCommand));
            }

            if (!Strings.isBlank(request.getShowCommandsName())) {
                String text = String.format("Commands are listed at http://servobot.info/home/%s", homeName);
                MessageCommand showCommandsCommand = new TextCommand(Command.UNREGISTERED_ID,
                        new CommandSettings(Command.DEFAULT_FLAGS, Permission.ANYONE, new RateLimit()), text);
                commandTableEdit.merge(commandTable.addCommand(request.getShowCommandsName(), showCommandsCommand));
            }

            for (TextCommandRequest textCommandRequest : request.getTextCommands()) {
                MessageCommand textCommand = new TextCommand(Command.UNREGISTERED_ID, new CommandSettings(
                        Command.DEFAULT_FLAGS, Permission.ANYONE, new RateLimit()), textCommandRequest.getValue());
                commandTableEdit.merge(commandTable.addCommand(textCommandRequest.getName(), textCommand));
            }
            serializers.getCommandTableSerializer().commit(botHomeId, commandTableEdit);

            user.removeInvite();
            HomedUserTable homedUserTable = new HomedUserTable(serializers.getUserSerializer(), userTable, botHomeId);
            HomedUser homedUser = homedUserTable.getByUser(user);
            homedUser.getUserStatus().merge(new TwitchUserStatus(true, false, false, true));
            homedUserTable.save(homedUser);

            List<GameQueue> gameQueues = new ArrayList<>();
            List<Giveaway> giveaways = new ArrayList<>();
            BookTable bookTable = new BookTable();
            BotHome botHome = new BotHome(botHomeId, homeName, botName, timeZone, homedUserTable, bookTable,
                    commandTable, reactionTable, storageTable, serviceHomes, gameQueues, giveaways);
            bot.addHome(botHome);
            botHome.start(bot.getHomeEditor(botHomeId), bot.getAlertQueue());
            return botHome;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
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
