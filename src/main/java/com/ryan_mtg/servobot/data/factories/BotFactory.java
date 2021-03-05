package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.BotHomeRow;
import com.ryan_mtg.servobot.data.models.BotRow;
import com.ryan_mtg.servobot.data.models.ServiceHomeRow;
import com.ryan_mtg.servobot.data.repositories.ServiceHomeRepository;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.game.GameManager;
import com.ryan_mtg.servobot.game.sus.SusGameManager;
import com.ryan_mtg.servobot.model.EmoteLink;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.books.Book;
import com.ryan_mtg.servobot.model.Bot;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.model.books.BookTable;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraftTable;
import com.ryan_mtg.servobot.model.game_queue.GameQueueTable;
import com.ryan_mtg.servobot.model.giveaway.Giveaway;
import com.ryan_mtg.servobot.model.roles.RoleTable;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.reaction.ReactionTable;
import com.ryan_mtg.servobot.model.storage.StorageTable;
import com.ryan_mtg.servobot.user.HomedUserTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BotFactory {
    private static Logger LOGGER = LoggerFactory.getLogger(BotFactory.class);

    private final ServiceHomeRepository serviceHomeRepository;
    private final SerializerContainer serializers;

    public BotFactory(final ServiceHomeRepository serviceHomeRepository, final SerializerContainer serializers) {
        this.serviceHomeRepository = serviceHomeRepository;
        this.serializers = serializers;
    }

    @PostConstruct
    public void setFactoryProperty() {
        serializers.setBotFactory(this);
    }


    public List<Bot> createBots(List<BotRow> botRows, Scope globalScope) {
        List<Bot> bots = new ArrayList<>();
        for (BotRow botRow : botRows) {
            bots.add(createBot(botRow, globalScope));
        }
        return bots;
    }

    private Bot createBot(final BotRow botRow, final Scope globalScope) {
        return SystemError.filter(() -> {
            LOGGER.info(">>>>>>>>>>>>>>>> Starting bot creation ");
            int botId = botRow.getId();

            ServiceSerializer serviceSerializer = serializers.getServiceSerializer();
            Map<Integer, Service> services = serviceSerializer.getServiceMap(botId);

            int contextId = -botId;

            BookTable bookTable = serializers.getBookSerializer().createBookTable(contextId);
            Map<Integer, Book> bookMap = bookTable.getBookMap();
            CommandTable commandTable = serializers.getCommandTableSerializer().createCommandTable(contextId, bookMap);

            StorageTable storageTable = serializers.getStorageTableSerializer().createStorageTable(contextId);

            List<GameManager> gameManagers = new ArrayList<GameManager>();
            gameManagers.add(new SusGameManager());

            Bot bot = new Bot(botId, botRow.getName(), globalScope, services, serializers, commandTable, bookTable,
                    storageTable, gameManagers);
            for (BotHomeRow botHomeRow : serializers.getBotHomeRepository().findAllByBotId(botId)) {
                bot.addHome(createBotHome(botHomeRow));
            }

            LOGGER.info("<<<<<<<<<<<<<<<< Ending bot creation ");
            return bot;
        });
    }

    public BotHome createBotHome(int botHomeId) {
        return createBotHome(serializers.getBotHomeRepository().findById(botHomeId));
    }

    @Transactional(rollbackOn = Exception.class)
    protected BotHome createBotHome(final BotHomeRow botHomeRow) {
        LOGGER.info(">>>>>> Starting bot home creation: {} ", botHomeRow.getHomeName());
        ServiceSerializer serviceSerializer = serializers.getServiceSerializer();

        Map<Integer, Service> services = serviceSerializer.getServiceMap(botHomeRow.getBotId());
        String homeName = botHomeRow.getHomeName();
        int flags = botHomeRow.getFlags();
        String botName = botHomeRow.getBotName();
        String timeZone = botHomeRow.getTimeZone();
        int botHomeId = botHomeRow.getId();

        LOGGER.info("------ Creating Books: {} ", botHomeRow.getHomeName());
        BookTable bookTable = serializers.getBookSerializer().createBookTable(botHomeId);
        Map<Integer, Book> bookMap = bookTable.getBookMap();

        LOGGER.info("------ Creating CommandTable: {} ", botHomeRow.getHomeName());
        CommandTable commandTable =
                serializers.getCommandTableSerializer().createCommandTable(botHomeId, bookMap);

        LOGGER.info("------ Creating ReactionTable: {} ", botHomeRow.getHomeName());
        ReactionTable reactionTable =
                serializers.getReactionTableSerializer().createReactionTable(botHomeId, commandTable);

        LOGGER.info("------ Creating StorageTable: {} ", botHomeRow.getHomeName());
        StorageTable storageTable = serializers.getStorageTableSerializer().createStorageTable(botHomeId);

        LOGGER.info("------ Creating service homes: {} ", botHomeRow.getHomeName());
        Map<Integer, ServiceHome> serviceHomes = new HashMap<>();
        for (ServiceHomeRow serviceHomeRow : serviceHomeRepository.findAllByBotHomeId(botHomeId)) {
            int serviceType = serviceHomeRow.getServiceType();
            Service service = services.get(serviceType);
            ServiceHome serviceHome = serviceSerializer.createServiceHome(serviceHomeRow, service);
            serviceHomes.put(serviceType, serviceHome);
        }

        LOGGER.info("------ Creating RoleTable: {} ", botHomeRow.getHomeName());
        RoleTable roleTable = serializers.getRoleTableSerializer()
                .createRoleTable(botHomeId, serviceHomes.get(DiscordService.TYPE));

        LOGGER.info("------ Creating Homed User Table: {} ", botHomeRow.getHomeName());
        HomedUserTable homedUserTable =
                new HomedUserTable(serializers.getUserSerializer(), serializers.getUserTable(), botHomeId);
        LOGGER.info("------ Creating Game Queues: {} ", botHomeRow.getHomeName());
        GameQueueTable gameQueueTable = serializers.getGameQueueSerializer()
                .createGameQueueTable(botHomeId, homedUserTable, serviceHomes.get(DiscordService.TYPE));

        LOGGER.info("------ Creating Giveaways: {} ", botHomeRow.getHomeName());
        List<Giveaway> giveaways =
                serializers.getGiveawaySerializer().createGiveaways(botHomeId, homedUserTable, commandTable);

        LOGGER.info("------ Creating Emote Links: {} ", botHomeRow.getHomeName());
        List<EmoteLink> emoteLinks = serializers.getEmoteLinkSerializer().createEmoteLinks(botHomeId);

        LOGGER.info("------ Creating Chat Draft Table: {} ", botHomeRow.getHomeName());
        ChatDraftTable chatDraftTable =
                serializers.getChatDraftSerializer().createChatDraftTable(botHomeId, homedUserTable, commandTable);

        LOGGER.info("------ Calling BotHome() constructor: {} ", botHomeRow.getHomeName());
        return SystemError.filter(() -> {
            BotHome botHome = new BotHome(botHomeId, flags, homeName, botName, timeZone, homedUserTable, bookTable,
                    commandTable, reactionTable, roleTable, storageTable, serviceHomes, gameQueueTable, giveaways,
                    emoteLinks, chatDraftTable);

            LOGGER.info("<<<<<< Ending bot home creation: {} ", botHomeRow.getHomeName());
            return botHome;
        });
    }
}