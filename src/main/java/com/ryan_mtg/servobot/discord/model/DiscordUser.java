package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.user.HomedUser;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;

public class DiscordUser implements User {
    @Getter
    private HomedUser homedUser;

    @Getter
    private Member member;

    public DiscordUser(final HomedUser homedUser, final Member member) {
        this.homedUser = homedUser;
        this.member = member;
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

    @Override
    public void whisper(final String message) {
        member.getUser().openPrivateChannel().complete().sendMessage(message).queue();
    }

    public long getDiscordId() {
        return member.getIdLong();
    }
}
