package com.ryan_mtg.servobot.commands.chat;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.MessagedHomeCommand;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.MessageHomeEvent;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class AddReactionCommand extends MessagedHomeCommand {
    @Getter
    private String emoteName;

    public AddReactionCommand(final int id, final CommandSettings commandSettings, final String emoteName)
            throws UserError {
        super(id, commandSettings);
        this.emoteName = emoteName;

        Validation.validateStringLength(emoteName, Validation.MAX_EMOTE_LENGTH, "Emote name");
    }

    @Override
    public CommandType getType() {
        return CommandType.ADD_REACTION_COMMAND_TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitAddReactionCommand(this);
    }

    @Override
    public void perform(final MessageHomeEvent event) throws BotHomeError {
        Message message = event.getMessage();
        if (!message.canEmote()) {
            return;
        }

        Emote emote = event.getHome().getEmote(emoteName);
        if (emote == null) {
            throw new BotHomeError(String.format("No emote named %s" , emoteName));
        }
        message.addEmote(emote);
    }
}
