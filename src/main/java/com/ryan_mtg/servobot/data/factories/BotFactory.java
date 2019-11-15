package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.BotHomeRow;
import com.ryan_mtg.servobot.data.models.BotRow;
import com.ryan_mtg.servobot.data.models.ServiceHomeRow;
import com.ryan_mtg.servobot.data.repositories.BotHomeRepository;
import com.ryan_mtg.servobot.data.repositories.ServiceHomeRepository;
import com.ryan_mtg.servobot.model.Bot;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.reaction.ReactionTable;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BotFactory {
    private static Logger LOGGER = LoggerFactory.getLogger(BotFactory.class);

    @Autowired
    private BotHomeRepository botHomeRepository;

    @Autowired
    private ServiceHomeRepository serviceHomeRepository;

    @Autowired
    private CommandTableSerializer commandTableSerializer;

    @Autowired
    private ReactionTableSerializer reactionTableSerializer;

    @Autowired
    private ServiceHomeSerializer serviceHomeSerializer;

    public Bot createBot(final BotRow botRow) {
        Map<Integer, Service> services = new HashMap<>();

        if (botRow.getDiscordToken() != null) {
            services.put(DiscordService.TYPE, new DiscordService(botRow.getDiscordToken()));
        }

        if (botRow.getTwitchToken() != null) {
            services.put(TwitchService.TYPE,
                    new TwitchService(botRow.getTwitchClientId(), botRow.getTwitchSecret(), botRow.getTwitchToken()));
        }
        Bot bot = new Bot(botRow.getName(), services);

        Iterable<BotHomeRow> botHomeRows = botHomeRepository.findAll();

        for (BotHomeRow botHomeRow : botHomeRows) {
            String homeName = botHomeRow.getHomeName();
            String timeZone = botHomeRow.getTimeZone();
            int botHomeId = botHomeRow.getId();
            CommandTable commandTable = commandTableSerializer.createCommandTable(botHomeId);
            ReactionTable reactionTable = reactionTableSerializer.createReactionTable(botHomeId);

            Map<Integer, ServiceHome> serviceHomes = new HashMap<>();
            for (ServiceHomeRow serviceHomeRow : serviceHomeRepository.findAllByBotHomeId(botHomeId)) {
                int serviceType = serviceHomeRow.getServiceType();
                Service service = services.get(serviceType);
                ServiceHome serviceHome = serviceHomeSerializer.createServiceHome(serviceHomeRow, service);
                serviceHomes.put(serviceType, serviceHome);
            }

            BotHome botHome = new BotHome(botHomeId, homeName, timeZone, commandTable, reactionTable, serviceHomes);
            bot.addHome(botHome);
        }

        return bot;
    }
}
