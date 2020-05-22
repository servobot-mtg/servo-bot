package com.ryan_mtg.servobot.commands.game_queue;

import com.ryan_mtg.servobot.commands.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import lombok.Getter;

public class ShowGameQueueCommand extends MessageCommand {
    public static final CommandType TYPE = CommandType.SHOW_GAME_QUEUE_COMMAND_TYPE;

    @Getter
    private int gameQueueId;

    public ShowGameQueueCommand(final int id, final CommandSettings commandSettings, final int gameQueueId) {
        super(id, commandSettings);
        this.gameQueueId = gameQueueId;
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        String response = event.getHomeEditor().showGameQueue(gameQueueId);
        MessageCommand.say(event, response);
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitShowGameQueueCommand(this);
    }
}
