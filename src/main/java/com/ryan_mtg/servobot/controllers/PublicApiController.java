package com.ryan_mtg.servobot.controllers;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/api/public")
public class PublicApiController {
    private static final Random RANDOM = new Random();
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicApiController.class);

    private final BotRegistrar botRegistrar;

    public PublicApiController(final BotRegistrar botRegistrar) {
        this.botRegistrar = botRegistrar;
    }

    @GetMapping("/sus")
    public String sus(@RequestParam final String name, @RequestParam(required = false) final String sender) {
        String lowerName = name.toLowerCase();
        switch (lowerName) {
            case "lsv":
            case "luis":
                return String.format("%s has been hard cleared.", name);
            case "gaby":
            case "gooby":
            case "gabyspartz":
                switch (RANDOM.nextInt(3)) {
                    case 0:
                        return String.format("Even a bot can tell that %s is being sus.", name);
                    case 1:
                        return String.format("%s is quite sus gabyDerp.", name);
                    case 2:
                        return String.format("%s has been hard cleared.", name);
                }
            case "bk":
                return String.format("Detective %s is on the case.", name);
            case "niteowlbk":
            case "nightowl bk":
            case "night owl bk":
                return String.format("While he isn't sus in the morning, %s is super sus at this hour.", name);
            case "secretbk":
            case "secret bk":
                return String.format("Shh, don't tell anyone, but %s is sus.", name);
            case "lord bk":
            case "lordbk":
                return String.format("%s and his huge tracts of land are extremely sus.", name);
            case "honest bk":
                return String.format("More like dishonest BK, am I right?", name);
            case "graham":
            case "grahamlrr":
            case "graham_lrr":
                return String.format("%s is as sus as Simic Slaw.", name);
            case "wrapter":
            case "wraptero":
                switch (RANDOM.nextInt(3)) {
                    case 0:
                    case 1:
                        return String.format("%s is railroading! That's quite sus.", name);
                    case 2:
                        return String.format("Is %s the third imposter?!?! gabyHmm", name);
                }
            case "mani":
            case "zapgaze":
                switch (RANDOM.nextInt(3)) {
                    case 0:
                        return String.format("What? %s is sus? Is Pheylop dead?", name);
                    case 1:
                    case 2:
                        return String.format("%s is sus. Pheylop must be dead.", name);
                }
            case "pheylop":
                switch (RANDOM.nextInt(3)) {
                    case 0:
                        return String.format("What? %s is sus? Is Mani dead?", name);
                    case 1:
                    case 2:
                        return String.format("%s is sus. Mani must be dead.", name);
                }
            case "stunlock":
            case "stunlockftw":
                return String.format("More like SuslockFTW, am I right?", name);
            case "suslock":
            case "suslockftw":
                return String.format("%s is definitely sus, it's right there in the name!", name);
            case "haiyue":
            case "niphette":
                return String.format("Fan favorite or not, %s is sus!", name);
            case "mikaela":
            case "mythicmikaela":
                return String.format("%s sus af. She's always the imposter!", name);
            case "zyla":
            case "babyberluce":
                return String.format("Of course %s is sus, she's a stone cold killer!", name);
            case "corey":
            case "coreyb":
            case "corey_burkhart":
                return String.format("Who be sus? %s sus!", name);
            case "bloody":
                return String.format("With a name like %s, she has to be sus!", name);
            case "zlubars":
                return String.format("%s is sus, but zulubars is clear!", name);
            case "zulubars":
            case "zooloobars":
                return String.format("%s is sus, but zlubars is clear!", name);
            case "lady":
            case "ladyatarka":
                return String.format("%s is above reproach, but nonetheless is quite sus!", name);
            case "tom":
            case "tomlocke":
            case "brandom":
                return String.format("How can %s be sus when Mikaela killed him?", name);
            case "squirrel":
            case "squirrel_loot":
                return String.format("Even a sus %s is right twice a day. However this isn't one of those times.", name);
            case "":
            case "mtgbot":
                if (!sender.isEmpty()) {
                    return sus(sender, sender);
                }
                return "Don't be suspicious! Don't be suspicious!";
        }

        switch (RANDOM.nextInt(8)) {
            case 0:
            case 1:
            case 2:
                return String.format("%s is extremely sus.", name);
            case 3:
                return String.format("Even a bot can tell that %s is being sus.", name);
            case 4:
                return String.format("I've got a very sus feeling about %s.", name);
            case 5:
                return String.format("Error, error! %s is sus. Does not compute!", name);
            case 6:
                return String.format("%s is sus. I'm not saying they're an imposter, but they might just be the 3rd imposter.", name);
            case 7:
                return String.format("Never trust, always sus of %s.", name);
        }
        return String.format("%s is extremely sus.", name);
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
            Parser parser = new Parser(scope, homeEditor.getStorageValueEditor());
            return parser.parse(expression).evaluate();
        } catch (ParseException e) {
            throw new UserError(e, "Failed to parse %s: %s", expression, e.getMessage());
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

    @GetMapping("/melee")
    public String evaluateMeleeExpression(@RequestParam final String query,
            @RequestParam(required = false) final String name, final MtgMeleeInformer informer) {
        switch (query) {
            case "tournaments":
                return informer.describeCurrentTournaments();
            case "decklists":
            case "decks":
            case "deck":
                if (Strings.isBlank(name)) {
                    return informer.getCurrentDecklists();
                } else {
                    return informer.getCurrentDecklist(name);
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
