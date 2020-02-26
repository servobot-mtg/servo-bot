package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.parser.ParseException;
import com.ryan_mtg.servobot.model.parser.Parser;
import com.ryan_mtg.servobot.model.scope.MessageSentSymbolTable;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.twitch.model.TwitchService;

import java.util.regex.Pattern;

public class EvaluateExpressionCommand extends MessageCommand {
    public static final int TYPE = 19;
    public static final Pattern gabyEasterEggPattern = Pattern.compile("2\\s*\\+\\s*2");

    private boolean gabyEasterEgg;

    public EvaluateExpressionCommand(final int id, CommandSettings commandSettings, final boolean gabyEasterEgg) {
        super(id, commandSettings);
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
                Emote emote = event.getHome().getEmote("gabyMath");
                if (emote != null) {
                    MessageCommand.say(event, emote.getMessageText());
                }
                return;
            }
        }

        HomeEditor homeEditor = event.getHomeEditor();
        Scope scope = new Scope(homeEditor.getScope(), new MessageSentSymbolTable(event, arguments));
        Parser parser = new Parser(scope, homeEditor);

        try {
            MessageCommand.say(event, parser.parse(arguments).toString());
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
