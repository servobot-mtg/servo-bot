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
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter @RequiredArgsConstructor
public class SerializerContainer {
    @Setter
    private BotFactory botFactory;

    private final AlertGeneratorSerializer alertGeneratorSerializer;
    private final BookSerializer bookSerializer;
    private final BotHomeRepository botHomeRepository;
    private final ChatDraftSerializer chatDraftSerializer;
    private final CommandSerializer commandSerializer;
    private final CommandTableSerializer commandTableSerializer;
    private final EmoteLinkSerializer emoteLinkSerializer;
    private final GameQueueRepository gameQueueRepository;
    private final GameQueueSerializer gameQueueSerializer;
    private final GameQueueEntryRepository gameQueueEntryRepository;
    private final GiveawayRepository giveawayRepository;
    private final GiveawaySerializer giveawaySerializer;
    private final PrizeRepository prizeRepository;
    private final ReactionSerializer reactionSerializer;
    private final ReactionTableSerializer reactionTableSerializer;
    private final RoleTableSerializer roleTableSerializer;
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
}
