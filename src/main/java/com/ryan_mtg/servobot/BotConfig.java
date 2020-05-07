package com.ryan_mtg.servobot;

import com.ryan_mtg.servobot.channelfireball.mfo.MfoInformer;
import com.ryan_mtg.servobot.data.repositories.BotRepository;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.books.Book;
import com.ryan_mtg.servobot.model.Bot;
import com.ryan_mtg.servobot.data.factories.BotFactory;
import com.ryan_mtg.servobot.model.BotRegistrar;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.model.scope.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class BotConfig {
    private static Logger LOGGER = LoggerFactory.getLogger(BotConfig.class);

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private BotFactory botFactory;

    @Autowired
    private MfoInformer informer;

    @Bean
    public Scope globalScope() {
        SimpleSymbolTable symbolTable = new SimpleSymbolTable();
        Function<Book, String> randomStatement = Book::randomStatement;
        symbolTable.addValue("randomStatement", randomStatement);

        symbolTable.addFunctor("cfbTournaments", () -> informer.describeCurrentTournaments());
        symbolTable.addFunctor("cfbPairings", () -> informer.getCurrentPairings());
        symbolTable.addFunctor("cfbStandings", () -> informer.getCurrentStandings());

        return new Scope(null, symbolTable);
    }

    @Bean
    public BotRegistrar botRegistrar(@Qualifier("globalScope") final Scope globalScope) throws BotErrorException {
        Bot bot = botFactory.createBot(botRepository.findFirst().get(), globalScope);
        return new BotRegistrar(bot);
    }
}
