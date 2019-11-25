package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.user.UserStatus;
import net.dv8tion.jda.api.entities.Member;

public class DiscordUser implements User {
    private com.ryan_mtg.servobot.user.User user;
    private Member member;
    private UserStatus userStatus;

    public DiscordUser(final com.ryan_mtg.servobot.user.User user, final Member member, final UserStatus userStatus) {
        this.user = user;
        this.member = member;
        this.userStatus = userStatus;
    }

    @Override
    public String getName() {
        return member.getEffectiveName();
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
        return userStatus.isModerator();
    }

    @Override
    public boolean isSubscriber() {
        return userStatus.isSubscriber();
    }

    public long getDiscordId() {
        return member.getIdLong();
    }
}
