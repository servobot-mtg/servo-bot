package com.ryan_mtg.servobot.commands.game_queue;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedHomedCommand;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;
import lombok.Getter;

public class JoinGameQueueCommand extends InvokedHomedCommand {
    public static final CommandType TYPE = CommandType.JOIN_GAME_QUEUE_COMMAND_TYPE;

    @Getter
    private final int gameQueueId;

    public JoinGameQueueCommand(final int id, final CommandSettings commandSettings, final int gameQueueId) {
        super(id, commandSettings);
        this.gameQueueId = gameQueueId;
    }

    @Override
    public void perform(final CommandInvokedHomeEvent event) {
        /** TODO: fix this, maybe delete?
        User user = event.getSender();
        int position = event.getHomeEditor().joinGameQueue(gameQueueId, tuser);
        event.say(String.format("%s joined the queue in position %d", user.getName(), position));
         */
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitJoinGameQueueCommand(this);
    }
}
