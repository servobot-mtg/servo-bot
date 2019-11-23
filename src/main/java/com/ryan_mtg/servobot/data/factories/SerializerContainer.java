package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.repositories.BotHomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SerializerContainer {
    @Autowired
    private BotHomeRepository botHomeRepository;

    @Autowired
    private CommandSerializer commandSerializer;

    @Autowired
    private CommandTableSerializer commandTableSerializer;

    @Autowired
    private ReactionSerializer reactionSerializer;

    @Autowired
    private ReactionTableSerializer reactionTableSerializer;

    @Autowired
    private ServiceSerializer serviceSerializer;

    @Autowired
    private UserSerializer userSerializer;

    public BotHomeRepository getBotHomeRepository() {
        return botHomeRepository;
    }

    public CommandTableSerializer getCommandTableSerializer() {
        return commandTableSerializer;
    }

    public ReactionTableSerializer getReactionTableSerializer() {
        return reactionTableSerializer;
    }

    public ServiceSerializer getServiceSerializer() {
        return serviceSerializer;
    }
}
