package com.ryan_mtg.servobot.discord.event;

import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.events.StreamStartEvent;
import com.ryan_mtg.servobot.model.BotHome;
import net.dv8tion.jda.api.events.user.update.GenericUserPresenceEvent;

public class DiscordStreamStartEvent extends DiscordBotHomeEvent implements StreamStartEvent  {
    public DiscordStreamStartEvent(final GenericUserPresenceEvent event, final BotHome botHome) {
        super(botHome);
    }

    @Override
    public int getServiceType() {
        return DiscordService.TYPE;
    }
}
