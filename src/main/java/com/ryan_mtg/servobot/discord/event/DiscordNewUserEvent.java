package com.ryan_mtg.servobot.discord.event;

import com.ryan_mtg.servobot.discord.model.DiscordHome;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.events.AbstractHomedEvent;
import com.ryan_mtg.servobot.events.UserEvent;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.User;
import net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent;

public class DiscordNewUserEvent extends AbstractHomedEvent implements UserEvent {
    private Home home;
    private User user;

    public DiscordNewUserEvent(final GenericGuildMemberEvent event, final int botHomeId, final User user) {
        super(botHomeId);
        this.home = new DiscordHome(event.getGuild(), getHomeEditor());
        this.user = user;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public Home getHome() {
        return home;
    }

    @Override
    public int getServiceType() {
        return DiscordService.TYPE;
    }
}
