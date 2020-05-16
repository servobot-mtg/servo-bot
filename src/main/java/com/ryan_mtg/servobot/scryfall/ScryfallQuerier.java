package com.ryan_mtg.servobot.scryfall;

import com.google.common.collect.ImmutableMap;
import feign.FeignException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

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
            return Arrays.asList();
        }
    }

    public Card searchForCardByName(final String query) throws ScryfallQueryException {
        return scryfallClient.fuzzySearchForCardByName(ImmutableMap.of("fuzzy", query));
    }
}
