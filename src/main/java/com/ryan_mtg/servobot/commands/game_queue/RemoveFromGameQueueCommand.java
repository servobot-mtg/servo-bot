package com.ryan_mtg.servobot.commands.game_queue;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedHomedCommand;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;
import lombok.Getter;

public class RemoveFromGameQueueCommand extends InvokedHomedCommand {
    public static final CommandType TYPE = CommandType.REMOVE_FROM_GAME_QUEUE_COMMAND_TYPE;

    @Getter
    private final int gameQueueId;

    public RemoveFromGameQueueCommand(final int id, final CommandSettings commandSettings, final int gameQueueId) {
        super(id, commandSettings);
        this.gameQueueId = gameQueueId;
    }

    @Override
    public void perform(final CommandInvokedHomeEvent event) {
        /** TODO: fix this, maybe delete?
        User user = event.getSender();
        event.getHomeEditor().removeFromGameQueue(gameQueueId, user);
        event.say(String.format("%s removed from the queue", user.getName()));
         */
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitRemoveFromGameQueueCommand(this);
    }
}
