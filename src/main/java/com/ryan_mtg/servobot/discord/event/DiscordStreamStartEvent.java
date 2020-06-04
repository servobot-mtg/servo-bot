package com.ryan_mtg.servobot.discord.event;

import com.ryan_mtg.servobot.discord.model.DiscordHome;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.events.StreamStartEvent;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Home;
import net.dv8tion.jda.api.events.user.update.GenericUserPresenceEvent;

public class DiscordStreamStartEvent extends DiscordBotHomeEvent implements StreamStartEvent  {
    private GenericUserPresenceEvent event;

    public DiscordStreamStartEvent(final GenericUserPresenceEvent event, final BotHome botHome) {
        super(botHome);
        this.event = event;
    }

    @Override
    public Home getHome() {
        return new DiscordHome(event.getGuild(), getHomeEditor());
    }

    @Override
    public int getServiceType() {
        return DiscordService.TYPE;
    }
}
