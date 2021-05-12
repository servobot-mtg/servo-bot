package com.ryan_mtg.servobot.twitch.event;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.RaidEvent;
import com.ryan_mtg.servobot.events.UserHomeEvent;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.twitch.model.TwitchUser;

public class TwitchRaidEvent extends TwitchHomeEvent implements UserHomeEvent {
    private final TwitchUser raider;

    public TwitchRaidEvent(final TwitchClient client, final RaidEvent event, final BotHome botHome,
            final TwitchUser raider) {
        super(client, botHome, event.getChannel().getName(), Long.parseLong(event.getChannel().getId()));
        this.raider = raider;
    }

    @Override
    public Scope getScope() {
        HomeEditor homeEditor = getHomeEditor();
        SimpleSymbolTable messageSymbolTable = new SimpleSymbolTable();
        messageSymbolTable.addValue("raider", raider.getName());
        messageSymbolTable.addValue("user", raider.getName());
        messageSymbolTable.addValue("home", getServiceHome().getName());

        return new Scope(homeEditor.getScope(), messageSymbolTable);
    }

    @Override
    public User getUser() {
        return raider;
    }
}
