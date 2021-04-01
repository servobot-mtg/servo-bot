package com.ryan_mtg.servobot;

import com.ryan_mtg.servobot.game.fortune.FortuneCookieResponder;
import com.ryan_mtg.servobot.tournament.channelfireball.mfo.MfoInformer;
import com.ryan_mtg.servobot.data.repositories.BotRepository;
import com.ryan_mtg.servobot.model.books.Book;
import com.ryan_mtg.servobot.model.Bot;
import com.ryan_mtg.servobot.data.factories.BotFactory;
import com.ryan_mtg.servobot.model.BotRegistrar;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.tournament.mtgmelee.MtgMeleeInformer;
import com.ryan_mtg.servobot.utility.Strings;
import com.ryan_mtg.servobot.utility.jokes.JokesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

@Configuration
public class BotConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotConfig.class);

    private final BotRepository botRepository;
    private final BotFactory botFactory;
    private final MfoInformer mfoInformer;
    private final MtgMeleeInformer meleeInformer;
    private final JokesClient jokesClient;
    private final FortuneCookieResponder fortuneCookieResponder;

    public BotConfig(final BotRepository botRepository, final BotFactory botFactory, final MfoInformer mfoInformer,
            final MtgMeleeInformer meleeInformer) {
        this.botRepository = botRepository;
        this.botFactory = botFactory;
        this.mfoInformer = mfoInformer;
        this.meleeInformer = meleeInformer;
        this.jokesClient = JokesClient.newClient();
        this.fortuneCookieResponder = new FortuneCookieResponder();
    }

    @Bean
    public Scope globalScope() {
        SimpleSymbolTable symbolTable = new SimpleSymbolTable();
        Function<Book, String> randomStatement = Book::randomStatement;
        symbolTable.addValue("randomStatement", randomStatement);
        Function<Integer, Integer> random = this::random;
        symbolTable.addValue("random", random);
        symbolTable.addFunctor("joke", this::joke);
        symbolTable.addFunctor("fortune", this::fortune);

        symbolTable.addFunctor("cfbTournaments", mfoInformer::describeCurrentTournaments);
        symbolTable.addFunctor("cfbDecklists", mfoInformer::getCurrentDecklists);
        symbolTable.addFunctor("cfbDecks", mfoInformer::getCurrentDecklists);
        symbolTable.addFunctor("cfbPairings", mfoInformer::getCurrentPairings);
        symbolTable.addFunctor("cfbStandings", mfoInformer::getCurrentStandings);
        symbolTable.addFunctor("cfbRound", mfoInformer::getCurrentRound);
        symbolTable.addFunctor("cfbRecords", mfoInformer::getCurrentRecords);
        symbolTable.addValue("cfbRecord", (Function<String, String>) this::getCfbRecord);

        symbolTable.addValue("scgStatus", (Function<String, String>) meleeInformer::getCurrentStatus);
        symbolTable.addFunctor("scgTournaments", meleeInformer::describeCurrentTournaments);
        symbolTable.addFunctor("scgDecklists", meleeInformer::getCurrentDecklists);
        symbolTable.addFunctor("scgDecks", meleeInformer::getCurrentDecklists);
        symbolTable.addFunctor("scgPairings", meleeInformer::getCurrentPairings);
        symbolTable.addFunctor("scgStandings", meleeInformer::getCurrentStandings);
        symbolTable.addFunctor("scgRound", meleeInformer::getCurrentRound);
        symbolTable.addFunctor("scgRecords", meleeInformer::getCurrentRecords);
        symbolTable.addValue("scgRecord", (Function<String, String>) this::getScgRecord);

        return new Scope(null, symbolTable);
    }

    @Bean
    public BotRegistrar botRegistrar(@Qualifier("globalScope") final Scope globalScope) {
        List<Bot> bots = botFactory.createBots(botRepository.findAll(), globalScope);
        return new BotRegistrar(bots);
    }

    private String getCfbRecord(final String input) {
        if (Strings.isBlank(input)) {
            return mfoInformer.getCurrentRecords();
        }
        return mfoInformer.getCurrentRecord(input);
    }

    private String getScgRecord(final String input) {
        if (Strings.isBlank(input)) {
            return meleeInformer.getCurrentRecords();
        }

        String result = meleeInformer.getCurrentRecord(input);
        if (result != null) {
            return result;
        }

        return meleeInformer.getCurrentRecords();
    }

    private int random(final int bound) {
        return new Random().nextInt(bound);
    }

    private String joke() {
        return jokesClient.getJoke();
    }

    private String fortune() {
        return fortuneCookieResponder.respond();
    }
}