package com.ryan_mtg.servobot.discord;

import com.ryan_mtg.servobot.data.repositories.BotRepository;
import com.ryan_mtg.servobot.discord.bot.Bot;
import com.ryan_mtg.servobot.data.factories.BotFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {
    private static Logger LOGGER = LoggerFactory.getLogger(BotConfig.class);

    @Autowired
    private BotRepository botRepository;

    @Autowired BotFactory botFactory;

    @Bean
    public Bot bot() {
        return botFactory.createBot(botRepository.findFirst().get());
    }
}
