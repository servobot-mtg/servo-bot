package com.ryan_mtg.servobot.scryfall;

import com.google.common.collect.ImmutableMap;
import com.ryan_mtg.servobot.commands.magic.CardQuery;
import com.ryan_mtg.servobot.scryfall.json.Card;
import feign.FeignException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ScryfallQuerier {
    private ScryfallClient scryfallClient;

    public ScryfallQuerier() {
        scryfallClient = ScryfallClient.newClient();
    }

    public List<Card> searchForCards(final String query) {
        try {
            CardList cardList = scryfallClient.cardSearch(ImmutableMap.of("q", query));
            return cardList.getData();
        } catch (FeignException.NotFound e) {
            return Collections.emptyList();
        }
    }

    public Card searchForCardByName(final CardQuery cardQuery) throws ScryfallQueryException {
        Map<String, String> arguments = new HashMap<>();
        arguments.put("fuzzy", cardQuery.getQuery());
        if (cardQuery.getSet() != null) {
            arguments.put("set", cardQuery.getSet());
        }
        return scryfallClient.fuzzySearchForCardByName(arguments);
    }
}
