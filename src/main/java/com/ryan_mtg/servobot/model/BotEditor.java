package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.chat.AddCommand;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedCommand;
import com.ryan_mtg.servobot.commands.hierarchy.RateLimit;
import com.ryan_mtg.servobot.commands.trigger.CommandAlias;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.CommandTableEdit;
import com.ryan_mtg.servobot.commands.chat.DeleteCommand;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.commands.chat.TextCommand;
import com.ryan_mtg.servobot.controllers.ApiController.CreateBotHomeRequest;
import com.ryan_mtg.servobot.controllers.ApiController.TextCommandRequest;
import com.ryan_mtg.servobot.data.factories.SerializerContainer;
import com.ryan_mtg.servobot.data.models.BotHomeRow;
import com.ryan_mtg.servobot.data.models.ServiceHomeRow;
import com.ryan_mtg.servobot.data.models.SuggestionRow;
import com.ryan_mtg.servobot.data.repositories.SuggestionRepository;
import com.ryan_mtg.servobot.error.BotError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.game.GameManager;
import com.ryan_mtg.servobot.model.books.BookTable;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraftTable;
import com.ryan_mtg.servobot.model.editors.BookTableEditor;
import com.ryan_mtg.servobot.model.editors.CommandTableEditor;
import com.ryan_mtg.servobot.model.editors.StorageValueEditor;
import com.ryan_mtg.servobot.model.game_queue.GameQueueTable;
import com.ryan_mtg.servobot.model.giveaway.Giveaway;
import com.ryan_mtg.servobot.model.reaction.ReactionTable;
import com.ryan_mtg.servobot.model.roles.RoleTable;
import com.ryan_mtg.servobot.model.schedule.Schedule;
import com.ryan_mtg.servobot.model.schedule.ScheduleEdit;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.model.storage.StorageTable;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import com.ryan_mtg.servobot.twitch.model.TwitchUserStatus;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.user.HomedUserTable;
import com.ryan_mtg.servobot.user.User;
import com.ryan_mtg.servobot.user.UserTable;
import com.ryan_mtg.servobot.utility.Strings;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(BotEditor.class);
    private final Bot bot;
    private final SerializerContainer serializers;

    @Getter
    private final CommandTableEditor commandTableEditor;

    @Getter
    private final BookTableEditor bookTableEditor;

    @Getter
    private final StorageValueEditor storageValueEditor;

    public BotEditor(final Bot bot) {
        this.bot = bot;
        this.serializers = bot.getSerializers();
        this.commandTableEditor = new CommandTableEditor(bot.getBookTable(), bot.getCommandTable(),
                serializers.getCommandSerializer(), serializers.getCommandTableSerializer(), null);

        this.bookTableEditor =
                new BookTableEditor(-bot.getId(), bot.getBookTable(), serializers.getBookSerializer());

        this.storageValueEditor =
                new StorageValueEditor(-bot.getId(), bot.getStorageTable(), serializers.getStorageValueSerializer());
    }

    public Scope getScope() {
        return bot.getBotScope();
    }

    public User getUserById(final int userId) {
        return serializers.getUserTable().getById(userId);
    }

    public void setArenaUsername(final int userId, final String username) throws UserError {
        Validation.validateStringLength(username, Validation.MAX_USERNAME_LENGTH, "Arena username");
        serializers.getUserTable().modifyUser(userId, user -> user.setArenaUsername(username));
    }

    public void sendMessage(final User receiver, final String message, final int serviceType) {
        bot.getService(serviceType).whisper(receiver, message);
    }

    @Transactional(rollbackOn = Exception.class)
    public BotHome createBotHome(final int userId, final CreateBotHomeRequest request) throws UserError {
        try {
            UserTable userTable = serializers.getUserTable();
            User user = userTable.getById(userId);

            if (!user.hasInvite()) {
                throw new UserError("%s can't create home without an invite.", user.getName());
            }

            if (!userTable.getHomesStreamed(userId).isEmpty()) {
                throw new UserError("%s already has a home.", user.getName());
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
            botHomeRow.setBotId(bot.getId());
            String homeName = user.getTwitchUsername();
            botHomeRow.setHomeName(homeName);
            String timeZone = request.getTimeZone();
            botHomeRow.setTimeZone(timeZone);

            serializers.getBotHomeRepository().save(botHomeRow);
            int botHomeId = botHomeRow.getId();

            CommandTable commandTable = new CommandTable(botHomeId, false);
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
                InvokedCommand addCommand = new AddCommand(Command.UNREGISTERED_ID,
                        new CommandSettings(Command.DEFAULT_FLAGS, Permission.MOD, new RateLimit()));
                commandTableEdit.merge(commandTable.addCommand(request.getAddCommandName(), addCommand));
            }

            if (!Strings.isBlank(request.getDeleteCommandName())) {
                InvokedCommand deleteCommand = new DeleteCommand(Command.UNREGISTERED_ID,
                    new CommandSettings(Command.DEFAULT_FLAGS, Permission.MOD, new RateLimit()));
                commandTableEdit.merge(commandTable.addCommand(request.getDeleteCommandName(), deleteCommand));
            }

            if (!Strings.isBlank(request.getShowCommandsName())) {
                String text = String.format("Commands are listed at http://servobot.info/home/%s", homeName);
                InvokedCommand showCommandsCommand = new TextCommand(Command.UNREGISTERED_ID,
                        new CommandSettings(Command.DEFAULT_FLAGS, Permission.ANYONE, new RateLimit()), text);
                commandTableEdit.merge(commandTable.addCommand(request.getShowCommandsName(), showCommandsCommand));
            }

            for (TextCommandRequest textCommandRequest : request.getTextCommands()) {
                InvokedCommand textCommand = new TextCommand(Command.UNREGISTERED_ID, new CommandSettings(
                        Command.DEFAULT_FLAGS, Permission.ANYONE, new RateLimit()), textCommandRequest.getValue());
                commandTableEdit.merge(commandTable.addCommand(textCommandRequest.getName(), textCommand));
            }
            serializers.getCommandTableSerializer().commit(commandTableEdit);

            user.removeInvite();
            HomedUserTable homedUserTable = new HomedUserTable(serializers.getUserSerializer(), userTable, botHomeId);
            HomedUser homedUser = homedUserTable.getByUser(user);
            homedUser.getUserStatus().update(new TwitchUserStatus(true, false, false, true));
            homedUserTable.save(homedUser);

            Schedule schedule = new Schedule(timeZone);
            ScheduleEdit scheduleEdit = new ScheduleEdit();
            scheduleEdit.saveSchedule(botHomeId, schedule);
            serializers.getScheduleSerializer().commit(scheduleEdit);

            GameQueueTable gameQueueTable = new GameQueueTable();
            List<Giveaway> giveaways = new ArrayList<>();
            BookTable bookTable = new BookTable();
            RoleTable roleTable = new RoleTable(RoleTable.UNREGISTERED_ID, botHomeId, null);
            List<EmoteLink> emoteLinks = new ArrayList<>();
            ChatDraftTable chatDraftTable = new ChatDraftTable();
            BotHome botHome = new BotHome(botHomeId, BotHome.DEFAULT_FLAGS, homeName, botName, timeZone, homedUserTable,
                    bookTable, commandTable, reactionTable, roleTable, storageTable, schedule, serviceHomes,
                    gameQueueTable, giveaways, emoteLinks, chatDraftTable);
            bot.addHome(botHome);
            botHome.start(bot.getHomeEditor(botHomeId), bot.getAlertQueue(), false);
            return botHome;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void stopHome(final int botHomeId) {
        bot.getHomeEditor(botHomeId).stop();
    }

    public void restartHome(final int botHomeId) {
        bot.removeHome(bot.getHome(botHomeId));

        BotHome botHome = this.serializers.getBotFactory().createBotHome(botHomeId);
        bot.addHome(botHome);

        bot.getHomeEditor(botHomeId).start(bot.getAlertQueue());
    }

    @Transactional(rollbackOn = Exception.class)
    public void addSuggestion(final String command) {
        String alias = command.toLowerCase();
        if (alias.length() > Validation.MAX_TRIGGER_LENGTH) {
            return; //ignore suggestions that are absurd
        }

        SuggestionRepository suggestionRepository = serializers.getSuggestionRepository();
        SuggestionRow suggestionRow = suggestionRepository.findByAlias(alias);
        if (suggestionRow == null) {
            suggestionRow = new SuggestionRow(alias, 1);
        } else {
            suggestionRow.setCount(suggestionRow.getCount() + 1);
        }
        suggestionRepository.save(suggestionRow);
    }

    public GameManager getGameManager(final int gameType) throws BotError {
        for(GameManager gameManager : bot.getGameManagers()) {
            if (gameManager.getType() == gameType) {
                return gameManager;
            }
        }
        throw new BotError("Bot does not have a game type: %d", gameType);
    }

    private void validateNewCommandName(final Set<String> commandNames, final String commandName)
            throws UserError {
        if (commandName != null) {
            CommandAlias.validateAlias(commandName);
            if (commandNames.contains(commandName)) {
                throw new UserError("%s is a duplicated command name.", commandName);
            }
            commandNames.add(commandName);
        }
    }
}
