package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.repositories.BotHomeRepository;
import com.ryan_mtg.servobot.data.repositories.GameQueueEntryRepository;
import com.ryan_mtg.servobot.data.repositories.GameQueueRepository;
import com.ryan_mtg.servobot.data.repositories.StatementRepository;
import com.ryan_mtg.servobot.data.repositories.SuggestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SerializerContainer {
    private BotFactory botFactory;

    @Autowired
    private BookSerializer bookSerializer;

    @Autowired
    private BotHomeRepository botHomeRepository;

    @Autowired
    private CommandSerializer commandSerializer;

    @Autowired
    private CommandTableSerializer commandTableSerializer;

    @Autowired
    private GameQueueRepository gameQueueRepository;

    @Autowired
    private GameQueueSerializer gameQueueSerializer;

    @Autowired
    private GameQueueEntryRepository gameQueueEntryRepository;

    @Autowired
    private ReactionSerializer reactionSerializer;

    @Autowired
    private ReactionTableSerializer reactionTableSerializer;

    @Autowired
    private ServiceSerializer serviceSerializer;

    @Autowired
    private StatementRepository statementRepository;

    @Autowired
    private SuggestionRepository suggestionRepository;

    @Autowired
    private UserSerializer userSerializer;

    public BookSerializer getBookSerializer() {
        return bookSerializer;
    }

    public BotFactory getBotFactory() {
        return botFactory;
    }

    public void setBotFactory(final BotFactory botFactory) {
        this.botFactory = botFactory;
    }

    public BotHomeRepository getBotHomeRepository() {
        return botHomeRepository;
    }

    public CommandSerializer getCommandSerializer() {
        return commandSerializer;
    }

    public CommandTableSerializer getCommandTableSerializer() {
        return commandTableSerializer;
    }

    public GameQueueRepository getGameQueueRepository() {
        return gameQueueRepository;
    }

    public GameQueueSerializer getGameQueueSerializer() {
        return gameQueueSerializer;
    }

    public GameQueueEntryRepository getGameQueueEntryRepository() {
        return gameQueueEntryRepository;
    }

    public ReactionTableSerializer getReactionTableSerializer() {
        return reactionTableSerializer;
    }

    public ReactionSerializer getReactionSerializer() {
        return reactionSerializer;
    }

    public ServiceSerializer getServiceSerializer() {
        return serviceSerializer;
    }

    public StatementRepository getStatementRepository() {
        return statementRepository;
    }

    public SuggestionRepository getSuggestionRepository() {
        return suggestionRepository;
    }

    public UserSerializer getUserSerializer() {
        return userSerializer;
    }
}
