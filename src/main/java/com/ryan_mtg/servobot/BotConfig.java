package com.ryan_mtg.servobot;

import com.ryan_mtg.servobot.data.repositories.BotRepository;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.Bot;
import com.ryan_mtg.servobot.data.factories.BotFactory;
import com.ryan_mtg.servobot.model.BotRegistrar;
import com.ryan_mtg.servobot.model.scope.FunctorSymbolTable;
import com.ryan_mtg.servobot.model.scope.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {
    private static Logger LOGGER = LoggerFactory.getLogger(BotConfig.class);

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private BotFactory botFactory;

    @Bean
    public Scope globalScope() {
        FunctorSymbolTable symbolTable = new FunctorSymbolTable();
        Scope globalScope = new Scope(null, symbolTable);
        return globalScope;
    }

    @Bean
    public BotRegistrar botRegistrar(@Qualifier("globalScope") final Scope globalScope) throws BotErrorException {
        Bot bot = botFactory.createBot(botRepository.findFirst().get(), globalScope);
        BotRegistrar botRegistrar = new BotRegistrar(bot);
        return botRegistrar;
    }
}
