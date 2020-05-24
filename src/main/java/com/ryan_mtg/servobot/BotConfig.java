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
import com.ryan_mtg.servobot.utility.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class BotConfig {
    private static Logger LOGGER = LoggerFactory.getLogger(BotConfig.class);

    private final BotRepository botRepository;
    private final BotFactory botFactory;
    private final MfoInformer informer;

    public BotConfig(final BotRepository botRepository, final BotFactory botFactory, final MfoInformer mfoInformer) {
        this.botRepository = botRepository;
        this.botFactory = botFactory;
        this.informer = mfoInformer;
    }

    @Bean
    public Scope globalScope() {
        SimpleSymbolTable symbolTable = new SimpleSymbolTable();
        Function<Book, String> randomStatement = Book::randomStatement;
        symbolTable.addValue("randomStatement", randomStatement);

        symbolTable.addFunctor("cfbTournaments", informer::describeCurrentTournaments);
        symbolTable.addFunctor("cfbDecklists", informer::getCurrentDecklists);
        symbolTable.addFunctor("cfbDecks", informer::getCurrentDecklists);
        symbolTable.addFunctor("cfbPairings", informer::getCurrentPairings);
        symbolTable.addFunctor("cfbStandings", informer::getCurrentStandings);
        symbolTable.addFunctor("cfbRound", informer::getCurrentRound);
        symbolTable.addFunctor("cfbRecords", informer::getCurrentRecords);

        Function<String, String> cfbRecord = this::getRecord;
        symbolTable.addFunctor("cfbRecord", () -> cfbRecord);

        return new Scope(null, symbolTable);
    }

    @Bean
    public BotRegistrar botRegistrar(@Qualifier("globalScope") final Scope globalScope) throws BotErrorException {
        Bot bot = botFactory.createBot(botRepository.findFirst().get(), globalScope);
        return new BotRegistrar(bot);
    }

    private String getRecord(final String input) {
        if (Strings.isBlank(input)) {
            return informer.getCurrentRecords();
        }
        return informer.getCurrentRecord(input);
    }
}
