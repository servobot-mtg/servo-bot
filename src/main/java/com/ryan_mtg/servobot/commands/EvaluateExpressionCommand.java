package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.parser.ParseException;
import com.ryan_mtg.servobot.model.parser.Parser;
import com.ryan_mtg.servobot.model.scope.MessageSentSymbolTable;
import com.ryan_mtg.servobot.model.scope.Scope;

public class EvaluateExpressionCommand extends MessageCommand {
    public static final int TYPE = 19;

    public EvaluateExpressionCommand(final int id, final boolean secure, final Permission permission) {
        super(id, secure, permission);
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        if (arguments == null) {
            throw new BotErrorException("No argument provided.");
        }

        HomeEditor homeEditor = event.getHomeEditor();
        Scope scope = new Scope(homeEditor.getScope(), new MessageSentSymbolTable(event));
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
