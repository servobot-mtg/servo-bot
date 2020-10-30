package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;
import net.dv8tion.jda.api.entities.Member;

public class DiscordMessage implements Message {
    private static int OLD_LENGTH = 8;

    private User sender;
    private net.dv8tion.jda.api.entities.Message message;

    public DiscordMessage(final User sender, final net.dv8tion.jda.api.entities.Message message) {
        this.sender = sender;
        this.message = message;
    }

    @Override
    public long getId() {
        return message.getIdLong();
    }

    @Override
    public long getChannelId() {
        return message.getChannel().getIdLong();
    }

    @Override
    public String getContent() {
        return message.getContentDisplay();
    }

    @Override
    public User getSender() {
        return sender;
    }

    @Override
    public boolean canEmote() {
        return true;
    }

    @Override
    public void addEmote(final Emote emote) {
        if (emote instanceof DiscordEmote) {
            message.addReaction(((DiscordEmote)emote).getDiscordEmote()).queue();
        } else {
            message.addReaction(emote.getName()).queue();
        }
    }

    @Override
    public void removeEmote(final Emote emote, final User user) {
        Member member = message.getGuild().getMemberById(user.getHomedUser().getDiscordId());
        if (emote instanceof DiscordEmote) {
            message.removeReaction(((DiscordEmote)emote).getDiscordEmote(), member.getUser()).queue();
        } else {
            message.removeReaction(emote.getName(), member.getUser()).queue();
        }
    }

    @Override
    public void updateText(final String text) {
        message.editMessage(text).queue();
    }

    @Override
    public boolean isOld() {
        return message.getChannel().getHistoryAfter(message.getId(), OLD_LENGTH).complete().size() >= OLD_LENGTH;
    }
}