package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.BotHomeRow;
import com.ryan_mtg.servobot.data.models.BotRow;
import com.ryan_mtg.servobot.data.models.GameQueueEntryRow;
import com.ryan_mtg.servobot.data.models.GameQueueRow;
import com.ryan_mtg.servobot.data.models.ServiceHomeRow;
import com.ryan_mtg.servobot.data.repositories.GameQueueEntryRepository;
import com.ryan_mtg.servobot.data.repositories.ServiceHomeRepository;
import com.ryan_mtg.servobot.model.Book;
import com.ryan_mtg.servobot.model.Bot;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.model.GameQueue;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.reaction.ReactionTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BotFactory {
    private static Logger LOGGER = LoggerFactory.getLogger(BotFactory.class);

    @Autowired
    private ServiceHomeRepository serviceHomeRepository;

    @Autowired
    private SerializerContainer serializers;

    public Bot createBot(final BotRow botRow) {
        ServiceSerializer serviceSerializer = serializers.getServiceSerializer();
        Map<Integer, Service> services = serviceSerializer.getServiceMap();

        Bot bot = new Bot(botRow.getName(), services, serializers);
        Iterable<BotHomeRow> botHomeRows = serializers.getBotHomeRepository().findAll();

        for (BotHomeRow botHomeRow : botHomeRows) {
            String homeName = botHomeRow.getHomeName();
            String timeZone = botHomeRow.getTimeZone();
            int botHomeId = botHomeRow.getId();

            List<Book> books = serializers.getBookSerializer().createBooks(botHomeId);
            Map<Integer, Book> bookMap = new HashMap<>();
            for (Book book : books) {
                bookMap.put(book.getId(), book);
            }

            CommandTable commandTable = serializers.getCommandTableSerializer().createCommandTable(botHomeId, bookMap);
            ReactionTable reactionTable = serializers.getReactionTableSerializer().createReactionTable(botHomeId);

            Map<Integer, ServiceHome> serviceHomes = new HashMap<>();
            for (ServiceHomeRow serviceHomeRow : serviceHomeRepository.findAllByBotHomeId(botHomeId)) {
                int serviceType = serviceHomeRow.getServiceType();
                Service service = services.get(serviceType);
                ServiceHome serviceHome = serviceSerializer.createServiceHome(serviceHomeRow, service);
                serviceHomes.put(serviceType, serviceHome);
            }

            List<GameQueue> gameQueues = new ArrayList<>();
            for (GameQueueRow gameQueueRow : serializers.getGameQueueRepository().findAllByBotHomeId(botHomeId)) {
                GameQueue gameQueue = new GameQueue(gameQueueRow.getId(), gameQueueRow.getName(),
                        gameQueueRow.getState(), gameQueueRow.getNext(), gameQueueRow.getCurrentPlayerId());

                GameQueueEntryRepository gameQueueEntryRepository = serializers.getGameQueueEntryRepository();
                for (GameQueueEntryRow gameQueueEntryRow :
                        gameQueueEntryRepository.findByGameQueueIdOrderBySpotAsc(gameQueue.getId())) {
                    gameQueue.enqueue(gameQueueEntryRow.getUserId(), gameQueueEntryRow.getSpot());
                }

                gameQueues.add(gameQueue);
            }

            BotHome botHome = new BotHome(botHomeId, homeName, timeZone, commandTable, reactionTable, serviceHomes,
                    books, gameQueues);
            bot.addHome(botHome);
        }

        return bot;
    }
}
