package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.game.fortune.FortuneCookieResponder;
import com.ryan_mtg.servobot.game.sus.chat.SusResponder;
import com.ryan_mtg.servobot.timestamp.VideoTimestampManager;
import com.ryan_mtg.servobot.tournament.channelfireball.mfo.MfoInformer;
import com.ryan_mtg.servobot.controllers.error.BotError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.Bot;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.BotRegistrar;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.parser.ParseException;
import com.ryan_mtg.servobot.model.parser.Parser;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.tournament.mtgmelee.MtgMeleeInformer;
import com.ryan_mtg.servobot.utility.Strings;
import com.ryan_mtg.servobot.utility.Validation;
import com.ryan_mtg.servobot.utility.jokes.JokesClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/public")
public class PublicApiController {
    private static final Pattern TWICH_USERNAME_PATTERN = Pattern.compile("\\S{3,25}");

    private final BotRegistrar botRegistrar;
    private final SusResponder susResponder;
    private final FortuneCookieResponder fortuneCookieResponder;
    private final VideoTimestampManager videoTimestampManager;
    private final JokesClient jokesClient = JokesClient.newClient();

    public PublicApiController(final BotRegistrar botRegistrar, final SusResponder susResponder,
            final VideoTimestampManager videoTimestampManager, final FortuneCookieResponder fortuneCookieResponder) {
        this.botRegistrar = botRegistrar;
        this.susResponder = susResponder;
        this.fortuneCookieResponder = fortuneCookieResponder;
        this.videoTimestampManager = videoTimestampManager;
    }

    @GetMapping("/sus")
    public String sus(@RequestParam final String name, @RequestParam(required = false) final String sender) {
        return susResponder.respond(name, sender);
    }

    @GetMapping("/joke")
    public String joke() {
        return jokesClient.getJoke().replace('\n', ' ');
    }

    @GetMapping("/fortune")
    public String fortune() {
        return fortuneCookieResponder.respond();
    }

    @GetMapping("/evaluate")
    public String evaluateExpression(@RequestParam final String expression,
            @RequestParam(required = false) String home) throws UserError {
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
            Parser parser = new Parser(scope, homeEditor != null ? homeEditor.getStorageValueEditor() : null);
            return parser.parse(expression).evaluate();
        } catch (ParseException e) {
            throw new UserError(e, "Failed to parse %s: %s", expression, e.getMessage());
        }
    }

    @GetMapping("/timestamp")
    public String addTimestamp(@RequestParam(required = false) final String channel,
            @RequestParam(required = false) final String user, @RequestParam(required = false) final String note) {
        try {
            if (Strings.isBlank(channel)) {
                return "channel is a required parameter!";
            }

            Validation.validateStringValue(channel, Validation.MAX_USERNAME_LENGTH, "Channel",
                    TWICH_USERNAME_PATTERN);
            Validation.validateStringValue(user, Validation.MAX_USERNAME_LENGTH, "User", TWICH_USERNAME_PATTERN);
            Validation.validateStringLength(note, Validation.MAX_TEXT_LENGTH, "Note");

            videoTimestampManager.addTimeStamp(channel, user, note);

            String message = Strings.isBlank(note) ? "The timestamp has been recorded." : "The note has been recorded.";

            if (channel.equalsIgnoreCase("mythic_meebo")) {
                return String.format("%s Manda Thanks you!", message);
            }

            return message;
        } catch (UserError userError) {
            return userError.getMessage();
        }
    }

    @GetMapping("/cfb")
    public String evaluateCfbExpression(@RequestParam final String query,
            @RequestParam(required = false) final String arenaName, final MfoInformer informer) {
        switch (query) {
            case "tournaments":
                return informer.describeCurrentTournaments();
            case "decklists":
            case "decks":
            case "deck":
                if (Strings.isBlank(arenaName)) {
                    return informer.getCurrentDecklists();
                } else {
                    return informer.getCurrentDecklist(arenaName, false);
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

    @GetMapping("/melee")
    public String evaluateMeleeExpression(@RequestParam final String query,
            @RequestParam(required = false) final String name, @RequestParam(required = false) final String fallback,
            final MtgMeleeInformer informer) {
        switch (query) {
            case "tournaments":
                return informer.describeCurrentTournaments();
            case "decklists":
            case "decks":
            case "deck":
                if (Strings.isBlank(name)) {
                    return informer.getCurrentDecklists();
                } else {
                    String decklist = informer.getCurrentDecklist(name, fallback != null);
                    if (decklist == null) {
                        if (fallback != null && fallback.startsWith("cbl-")) {
                            return String.format("https://app.cardboard.live/s/%s", fallback.substring(4));
                        } else {
                            return "";
                        }
                    }
                    return decklist;
                }
            case "pairings":
                String description = informer.getCurrentPairings();
                if (description.length() >= 255) {
                    description = description.replace("#" + MtgMeleeInformer.PAIRINGS_ID, "");
                }
                return description;
            case "round":
                return informer.getCurrentRound();
            case "records":
            case "record":
                if (Strings.isBlank(name)) {
                    return informer.getCurrentRecords();
                } else {
                    return informer.getCurrentRecord(name);
                }
            case "standings":
                description = informer.getCurrentStandings();
                if (description.length() >= 255) {
                    description = description.replace("#" + MtgMeleeInformer.STANDINGS_ID, "");
                }
                return description;
            case "status":
                return informer.getCurrentStatus(name);
            default:
                return String.format("Unknown query %s", query);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BotError> botErrorHandler(final Exception exception) {
        exception.printStackTrace();
        return new ResponseEntity<>(new BotError(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
