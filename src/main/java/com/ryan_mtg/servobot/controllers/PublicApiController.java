package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.channelfireball.mfo.MfoInformer;
import com.ryan_mtg.servobot.controllers.error.BotError;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.Bot;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.BotRegistrar;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.parser.ParseException;
import com.ryan_mtg.servobot.model.parser.Parser;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.utility.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicApiController {
    private static Logger LOGGER = LoggerFactory.getLogger(PublicApiController.class);

    @Autowired
    private BotRegistrar botRegistrar;

    @GetMapping("/evaluate")
    public String evaluateExpression(@RequestParam final String expression, @RequestParam(required = false) String home)
            throws BotErrorException {

        Bot bot = botRegistrar.getDefaultBot();
        Scope scope = bot.getBotScope();
        HomeEditor homeEditor = null;

        if (home != null) {
            BotHome botHome = botRegistrar.getBotHome(home);
            bot = botHome.getBot();
            homeEditor = new HomeEditor(bot, botHome);
            scope = botHome.getBotHomeScope();
        }

        try {
            Parser parser = new Parser(scope, homeEditor);
            return parser.parse(expression).evaluate();
        } catch (ParseException e) {
            throw new BotErrorException(String.format("Failed to parse %s: %s", expression, e.getMessage()));
        }
    }

    @GetMapping("/cfb")
    public String evaluateCfbExpression(@RequestParam final String query,
            @RequestParam(required = false) final String arenaName, final MfoInformer informer)
            throws BotErrorException {
        //Bot bot = botRegistrar.getDefaultBot();
        //Scope scope = bot.getBotScope();

        switch (query) {
            case "tournaments":
                return informer.describeCurrentTournaments();
            case "decklists":
            case "decks":
            case "deck":
                if (Strings.isBlank(arenaName)) {
                    return informer.getCurrentDecklists();
                } else {
                    return informer.getCurrentDecklist(arenaName);
                }
            case "pairings":
                return informer.getCurrentPairings();
            case "standings":
                return informer.getCurrentStandings();
            case "round":
                return informer.getCurrentRound();
            case "records":
            case "record":
                if (Strings.isBlank(arenaName)) {
                    return informer.getCurrentRecords();
                } else {
                    return informer.getCurrentRecord(arenaName);
                }
            default:
                return String.format("Unknown query %s", query);
        }
    }

    @ExceptionHandler(BotErrorException.class)
    public ResponseEntity<BotError> botErrorExceptionHandler(final BotErrorException exception) {
        exception.printStackTrace();
        return new ResponseEntity<>(new BotError(exception.getErrorMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BotError> botErrorHandler(final Exception exception) {
        exception.printStackTrace();
        return new ResponseEntity<>(new BotError(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
