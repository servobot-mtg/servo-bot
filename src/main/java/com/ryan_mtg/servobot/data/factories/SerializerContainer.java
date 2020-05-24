package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.repositories.BotHomeRepository;
import com.ryan_mtg.servobot.data.repositories.GameQueueEntryRepository;
import com.ryan_mtg.servobot.data.repositories.GameQueueRepository;
import com.ryan_mtg.servobot.data.repositories.ServiceHomeRepository;
import com.ryan_mtg.servobot.data.repositories.StatementRepository;
import com.ryan_mtg.servobot.data.repositories.SuggestionRepository;
import com.ryan_mtg.servobot.user.UserTable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class SerializerContainer {
    @Setter
    private BotFactory botFactory;

    private final AlertGeneratorSerializer alertGeneratorSerializer;
    private final BookSerializer bookSerializer;
    private final BotHomeRepository botHomeRepository;
    private final CommandSerializer commandSerializer;
    private final CommandTableSerializer commandTableSerializer;
    private final GameQueueRepository gameQueueRepository;
    private final GameQueueSerializer gameQueueSerializer;
    private final GameQueueEntryRepository gameQueueEntryRepository;
    private final GiveawaySerializer giveawaySerializer;
    private final ReactionSerializer reactionSerializer;
    private final ReactionTableSerializer reactionTableSerializer;
    private final ServiceHomeRepository serviceHomeRepository;
    private final ServiceSerializer serviceSerializer;
    private final StatementRepository statementRepository;
    private final StorageTableSerializer storageTableSerializer;
    private final StorageValueSerializer storageValueSerializer;
    private final SuggestionRepository suggestionRepository;
    private final UserSerializer userSerializer;
    private final UserTable userTable;

    public SerializerContainer(final AlertGeneratorSerializer alertGeneratorSerializer,
            final BookSerializer bookSerializer, final BotHomeRepository botHomeRepository,
            final CommandSerializer commandSerializer, final CommandTableSerializer commandTableSerializer,
            final GameQueueRepository gameQueueRepository, final GameQueueSerializer gameQueueSerializer,
            final GameQueueEntryRepository gameQueueEntryRepository, final GiveawaySerializer giveawaySerializer,
            final ReactionSerializer reactionSerializer, final ReactionTableSerializer reactionTableSerializer,
            final ServiceHomeRepository serviceHomeRepository, final ServiceSerializer serviceSerializer,
            final StatementRepository statementRepository, final StorageTableSerializer storageTableSerializer,
            final StorageValueSerializer storageValueSerializer, final SuggestionRepository suggestionRepository,
            final UserSerializer userSerializer, final UserTable userTable) {

        this.alertGeneratorSerializer = alertGeneratorSerializer;
        this.bookSerializer = bookSerializer;
        this.botHomeRepository = botHomeRepository;
        this.commandSerializer = commandSerializer;
        this.commandTableSerializer = commandTableSerializer;
        this.gameQueueRepository = gameQueueRepository;
        this.gameQueueSerializer = gameQueueSerializer;
        this.gameQueueEntryRepository = gameQueueEntryRepository;
        this.giveawaySerializer = giveawaySerializer;
        this.reactionSerializer = reactionSerializer;
        this.reactionTableSerializer = reactionTableSerializer;
        this.serviceHomeRepository = serviceHomeRepository;
        this.serviceSerializer = serviceSerializer;
        this.statementRepository = statementRepository;
        this.storageTableSerializer = storageTableSerializer;
        this.storageValueSerializer = storageValueSerializer;
        this.suggestionRepository = suggestionRepository;
        this.userSerializer = userSerializer;
        this.userTable = userTable;
    }
}
