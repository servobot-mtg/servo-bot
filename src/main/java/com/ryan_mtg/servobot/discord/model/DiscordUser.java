package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.model.User;
import net.dv8tion.jda.api.entities.Member;

public class DiscordUser implements User {
    private Member member;

    public DiscordUser(final Member member) {
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


    public long getDiscordId() {
        return member.getIdLong();
    }
}
