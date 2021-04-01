package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.user.HomedUser;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;

public class DiscordHomedUser implements User {
    @Getter
    private final HomedUser homedUser;

    @Getter
    private final Member member;

    public DiscordHomedUser(final HomedUser homedUser, final Member member) {
        this.homedUser = homedUser;
        this.member = member;
    }

    @Override
    public String getName() {
        return member.getEffectiveName();
    }

    @Override
    public int getId() {
        return homedUser.getId();
    }

    @Override
    public com.ryan_mtg.servobot.user.User getUser() {
        return homedUser.getUser();
    }

    @Override
    public boolean isBot() {
        return member.getUser().isBot();
    }

    @Override
    public boolean isAdmin() {
        return homedUser.isAdmin();
    }

    @Override
    public boolean isModerator() {
        return homedUser.isModerator();
    }

    @Override
    public boolean isSubscriber() {
        return homedUser.isSubscriber();
    }

    public long getDiscordId() {
        return member.getIdLong();
    }
}
