package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.parser.ParseException;
import com.ryan_mtg.servobot.model.parser.Parser;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class EvaluateExpressionCommand extends MessageCommand {
    public static final int TYPE = 19;
    public static final Pattern gabyEasterEggPattern = Pattern.compile("2\\s*\\+\\s*2");

    private static Logger LOGGER = LoggerFactory.getLogger(EvaluateExpressionCommand.class);
    private boolean gabyEasterEgg;

    public EvaluateExpressionCommand(final int id, final int flags, final Permission permission,
                                     final boolean gabyEasterEgg) {
        super(id, flags, permission);
        this.gabyEasterEgg = gabyEasterEgg;
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        if (arguments == null) {
            throw new BotErrorException("No argument provided.");
        }

        if (gabyEasterEgg && gabyEasterEggPattern.matcher(arguments).matches()) {
            int serviceType = event.getMessage().getServiceType();
            if (serviceType == TwitchService.TYPE) {
                MessageCommand.say(event, "gabyMath");
                return;
            } else if (serviceType == DiscordService.TYPE) {
                LOGGER.info("Wants to gabyMath");
                Emote emote = event.getHome().getEmote("gabyMath");
                if (emote != null) {
                    LOGGER.info("Got an emote!");
                    MessageCommand.say(event, emote.getMessageText());
                }
                return;
            }
        }

        HomeEditor homeEditor = event.getHomeEditor();
        Parser parser = new Parser(getMessageScope(event), homeEditor);

        try {
            MessageCommand.sayRaw(event, parser.parse(arguments).evaluate());
        } catch (ParseException e) {
            throw new BotErrorException(String.format("Failed to parse %s: %s", arguments, e.getMessage()));
        }
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitEvaluateExpressionCommand(this);
    }
}
