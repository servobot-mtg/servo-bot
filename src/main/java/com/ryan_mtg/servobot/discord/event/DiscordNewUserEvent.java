package com.ryan_mtg.servobot.discord.event;

import com.ryan_mtg.servobot.discord.model.DiscordHome;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.events.AbstractHomedEvent;
import com.ryan_mtg.servobot.events.UserEvent;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.User;
import lombok.Getter;
import net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent;

public class DiscordNewUserEvent extends AbstractHomedEvent implements UserEvent {
    @Getter
    private Home home;

    @Getter
    private User user;

    public DiscordNewUserEvent(final GenericGuildMemberEvent event, final BotHome botHome, final User user) {
        super(botHome);
        this.home = new DiscordHome(event.getGuild(), getHomeEditor());
        this.user = user;
    }

    @Override
    public int getServiceType() {
        return DiscordService.TYPE;
    }
}
