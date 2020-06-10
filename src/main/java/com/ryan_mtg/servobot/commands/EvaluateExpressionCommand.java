package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedCommand;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedEvent;
import com.ryan_mtg.servobot.events.HomeEvent;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.parser.ParseException;
import com.ryan_mtg.servobot.model.parser.Parser;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class EvaluateExpressionCommand extends InvokedCommand {
    public static final CommandType TYPE = CommandType.EVALUATE_EXPRESSION_COMMAND_TYPE;
    public static final Pattern gabyEasterEggPattern = Pattern.compile("2\\s*\\+\\s*2");

    private static Logger LOGGER = LoggerFactory.getLogger(EvaluateExpressionCommand.class);
    private boolean gabyEasterEgg;

    public EvaluateExpressionCommand(final int id, CommandSettings commandSettings, final boolean gabyEasterEgg) {
        super(id, commandSettings);
        this.gabyEasterEgg = gabyEasterEgg;
    }

    @Override
    public void perform(final CommandInvokedEvent event) throws BotHomeError, UserError {
        String expression = event.getArguments();
        if (expression == null) {
            throw new UserError("No expression provided.");
        }

        if (gabyEasterEgg && gabyEasterEggPattern.matcher(expression).matches()) {
            int serviceType = event.getServiceType();
            if (serviceType == TwitchService.TYPE) {
                event.say("gabyMath");
                return;
            } else if (serviceType == DiscordService.TYPE && event instanceof HomeEvent) {
                Emote emote = ((HomeEvent) event).getHome().getEmote("gabyMath");
                if (emote != null) {
                    LOGGER.info("Got an emote!");
                    event.say(emote.getMessageText());
                }
                return;
            }
        }

        Parser parser = new Parser(event.getScope(), event.getStorageValueEditor());

        try {
            event.sayRaw(parser.parse(expression).evaluate());
        } catch (ParseException e) {
            throw new UserError("Failed to parse %s: %s", expression, e.getMessage());
        }
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitEvaluateExpressionCommand(this);
    }
}
