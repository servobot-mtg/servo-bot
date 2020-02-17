package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.Message;

public class AddReactionCommand extends MessageCommand {
    public static final int TYPE = 24;

    private String emoteName;

    public AddReactionCommand(final int id, final int flags, final Permission permission, final String emoteName) {
        super(id, flags, permission);
        this.emoteName = emoteName;
    }

    public String getEmoteName() {
        return emoteName;
    }

    @Override
    public int getType() {
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
