package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.repositories.BotHomeRepository;
import com.ryan_mtg.servobot.data.repositories.StatementRepository;
import com.ryan_mtg.servobot.data.repositories.SuggestionRepository;
import com.ryan_mtg.servobot.model.HomeEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SerializerContainer {
    @Autowired
    private BookSerializer bookSerializer;

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
    private StatementRepository statementRepository;

    @Autowired
    private SuggestionRepository suggestionRepository;

    @Autowired
    private UserSerializer userSerializer;

    public BookSerializer getBookSerializer() {
        return bookSerializer;
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
}
