package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.ServiceHomeRow;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.discord.model.DiscordServiceHome;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import com.ryan_mtg.servobot.twitch.model.TwitchServiceHome;
import org.springframework.stereotype.Component;

@Component
public class ServiceHomeSerializer {
    public ServiceHome createServiceHome(final ServiceHomeRow serviceHomeRow, final Service service) {
        switch (serviceHomeRow.getServiceType()) {
            case DiscordService.TYPE:
                return new DiscordServiceHome((DiscordService) service, serviceHomeRow.getLong());
            case TwitchService.TYPE:
                return new TwitchServiceHome((TwitchService) service, serviceHomeRow.getLong());
        }
        throw new IllegalArgumentException("Unknown ServiceHome type: " + serviceHomeRow.getServiceType());
    }
}
