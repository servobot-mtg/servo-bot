package com.ryan_mtg.servobot.commands.game_queue;

import com.ryan_mtg.servobot.commands.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.User;
import lombok.Getter;

public class RemoveFromGameQueueCommand extends MessageCommand {
    public static final int TYPE = 10;

    @Getter
    private int gameQueueId;

    public RemoveFromGameQueueCommand(final int id, final CommandSettings commandSettings, final int gameQueueId) {
        super(id, commandSettings);
        this.gameQueueId = gameQueueId;
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        User user = event.getSender();
        event.getHomeEditor().removeFromGameQueue(gameQueueId, user);
        MessageCommand.say(event, String.format("%s removed from the queue", user.getName()));
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitRemoveFromGameQueueCommand(this);
    }
}
