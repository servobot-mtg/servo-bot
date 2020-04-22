package com.ryan_mtg.servobot.commands.game_queue;

import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.User;
import lombok.Getter;

public class JoinGameQueueCommand extends MessageCommand {
    public static final CommandType TYPE = CommandType.JOIN_GAME_QUEUE_COMMAND_TYPE;

    @Getter
    private int gameQueueId;

    public JoinGameQueueCommand(final int id, final int flags, final Permission permission,
                                final int gameQueueId) {
        super(id, flags, permission);
        this.gameQueueId = gameQueueId;
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        User user = event.getSender();
        int position = event.getHomeEditor().joinGameQueue(gameQueueId, user);
        MessageCommand.say(event,String.format("%s joined the queue in position %d", user.getName(), position));
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
