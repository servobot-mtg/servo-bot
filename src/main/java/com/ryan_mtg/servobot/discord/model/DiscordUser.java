package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.user.HomedUser;
import net.dv8tion.jda.api.entities.Member;

public class DiscordUser implements User {
    private HomedUser user;
    private Member member;

    public DiscordUser(final HomedUser user, final Member member) {
        this.user = user;
        this.member = member;
    }

    @Override
    public String getName() {
        return member.getEffectiveName();
    }

    @Override
    public HomedUser getHomedUser() {
        return user;
    }

    @Override
    public boolean isBot() {
        return member.getUser().isBot();
    }

    @Override
    public boolean isAdmin() {
        return user.isAdmin();
    }

    @Override
    public boolean isModerator() {
        return user.isModerator();
    }

    @Override
    public boolean isSubscriber() {
        return user.isSubscriber();
    }

    @Override
    public void whisper(final String message) {
        member.getUser().openPrivateChannel().complete().sendMessage(message).queue();
    }

    public long getDiscordId() {
        return member.getIdLong();
    }

    public Member getMember() {
        return member;
    }
}
