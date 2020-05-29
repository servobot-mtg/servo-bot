package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.repositories.BotHomeRepository;
import com.ryan_mtg.servobot.data.repositories.GameQueueEntryRepository;
import com.ryan_mtg.servobot.data.repositories.GameQueueRepository;
import com.ryan_mtg.servobot.data.repositories.GiveawayRepository;
import com.ryan_mtg.servobot.data.repositories.PrizeRepository;
import com.ryan_mtg.servobot.data.repositories.ServiceHomeRepository;
import com.ryan_mtg.servobot.data.repositories.StatementRepository;
import com.ryan_mtg.servobot.data.repositories.StorageValueRepository;
import com.ryan_mtg.servobot.data.repositories.SuggestionRepository;
import com.ryan_mtg.servobot.data.repositories.UserHomeRepository;
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
    private final GiveawayRepository giveawayRepository;
    private final GiveawaySerializer giveawaySerializer;
    private final PrizeRepository prizeRepository;
    private final ReactionSerializer reactionSerializer;
    private final ReactionTableSerializer reactionTableSerializer;
    private final ServiceHomeRepository serviceHomeRepository;
    private final ServiceSerializer serviceSerializer;
    private final StatementRepository statementRepository;
    private final StorageTableSerializer storageTableSerializer;
    private final StorageValueRepository storageValueRepository;
    private final StorageValueSerializer storageValueSerializer;
    private final SuggestionRepository suggestionRepository;
    private final UserHomeRepository userHomeRepository;
    private final UserSerializer userSerializer;
    private final UserTable userTable;

    public SerializerContainer(final AlertGeneratorSerializer alertGeneratorSerializer,
            final BookSerializer bookSerializer, final BotHomeRepository botHomeRepository,
            final CommandSerializer commandSerializer, final CommandTableSerializer commandTableSerializer,
            final GameQueueRepository gameQueueRepository, final GameQueueSerializer gameQueueSerializer,
            final GameQueueEntryRepository gameQueueEntryRepository, final GiveawayRepository giveawayRepository,
            final GiveawaySerializer giveawaySerializer, final PrizeRepository prizeRepository,
            final ReactionSerializer reactionSerializer, final ReactionTableSerializer reactionTableSerializer,
            final ServiceHomeRepository serviceHomeRepository, final ServiceSerializer serviceSerializer,
            final StatementRepository statementRepository, final StorageTableSerializer storageTableSerializer,
            final StorageValueRepository storageValueRepository, final StorageValueSerializer storageValueSerializer,
            final SuggestionRepository suggestionRepository, final UserHomeRepository userHomeRepository,
            final UserSerializer userSerializer, final UserTable userTable) {

        this.alertGeneratorSerializer = alertGeneratorSerializer;
        this.bookSerializer = bookSerializer;
        this.botHomeRepository = botHomeRepository;
        this.commandSerializer = commandSerializer;
        this.commandTableSerializer = commandTableSerializer;
        this.gameQueueRepository = gameQueueRepository;
        this.gameQueueSerializer = gameQueueSerializer;
        this.gameQueueEntryRepository = gameQueueEntryRepository;
        this.giveawayRepository = giveawayRepository;
        this.giveawaySerializer = giveawaySerializer;
        this.prizeRepository = prizeRepository;
        this.reactionSerializer = reactionSerializer;
        this.reactionTableSerializer = reactionTableSerializer;
        this.serviceHomeRepository = serviceHomeRepository;
        this.serviceSerializer = serviceSerializer;
        this.statementRepository = statementRepository;
        this.storageTableSerializer = storageTableSerializer;
        this.storageValueRepository = storageValueRepository;
        this.storageValueSerializer = storageValueSerializer;
        this.suggestionRepository = suggestionRepository;
        this.userHomeRepository = userHomeRepository;
        this.userSerializer = userSerializer;
        this.userTable = userTable;
    }
}
