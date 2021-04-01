package com.ryan_mtg.servobot.discord.event;

import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.events.AbstractHomedEvent;
import com.ryan_mtg.servobot.events.UserHomeEvent;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.User;
import lombok.Getter;
import net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent;

public class DiscordNewUserEvent extends AbstractHomedEvent implements UserHomeEvent {
    @Getter
    private final User user;

    public DiscordNewUserEvent(final GenericGuildMemberEvent event, final BotHome botHome, final User user) {
        super(botHome);
        this.user = user;
    }

    @Override
    public int getServiceType() {
        return DiscordService.TYPE;
    }
}
