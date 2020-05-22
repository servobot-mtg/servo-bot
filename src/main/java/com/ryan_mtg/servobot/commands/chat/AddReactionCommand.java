package com.ryan_mtg.servobot.commands.chat;

import com.ryan_mtg.servobot.commands.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class AddReactionCommand extends MessageCommand {
    public static final CommandType TYPE = CommandType.ADD_REACTION_COMMAND_TYPE;

    @Getter
    private String emoteName;

    public AddReactionCommand(final int id, final CommandSettings commandSettings, final String emoteName)
            throws BotErrorException {
        super(id, commandSettings);
        this.emoteName = emoteName;

        Validation.validateStringLength(emoteName, Validation.MAX_EMOTE_LENGTH, "Emote name");
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitAddReactionCommand(this);
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        Message message = event.getMessage();
        if (!message.canEmote()) {
            return;
        }

        Emote emote = event.getHome().getEmote(emoteName);
        if (emote == null) {
            throw new BotErrorException(String.format("No emote named %s" , emoteName));
        }
        message.addEmote(emote);
    }
}
