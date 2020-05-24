package com.ryan_mtg.servobot.commands.magic;

import com.ryan_mtg.servobot.scryfall.Card;

import java.util.List;
import java.util.stream.Collectors;

public class CardUtil {
    public static String respondToCardSearch(final Card card, final boolean supportsNewLines) {
        if (card == null) {
            return "No matches found.";
        }

        return CardUtil.formatCard(card, supportsNewLines);
    }

    public static String respondToCardSearch(final List<Card> cards, final boolean supportsNewLines) {
        if (cards.isEmpty()) {
            return "No matches found.";
        }

        if (cards.size() == 1) {
            return CardUtil.formatCard(cards.get(0), supportsNewLines);
        }

        if (cards.size() <= 5) {
            List<String> names = cards.stream().map(Card::getName).collect(Collectors.toList());
            return String.format("%d matches found. Please choose between %s.", cards.size(), CardUtil.printList(names));
        }

        if (cards.size() > 100) {
            return "hundreds of matches found; please clarify.";
        }

        return String.format("%d matches found; please clarify.", cards.size());
    }

    public static String formatCard(final Card card, final boolean supportsNewLines) {
        String manaCost = getManaCost(card);
        if (supportsNewLines) {
            return String.format("%s %s\n%s\n%s", card.getName(), manaCost,
                    card.getTypeLine(), card.getOracleText());
        }
        return String.format("%s %s | %s | %s", card.getName(), manaCost,
                card.getTypeLine(), card.getOracleText());
    }

    private static String getManaCost(final Card card) {
        String manaCost = card.getManaCost().replaceAll("[{}]", "");
        if (manaCost.isEmpty()) {
            return manaCost;
        }
        return String.format("(%s)", manaCost);
    }

    private static String printList(final List<String> strings) {
        if (strings.size() == 2) {
            return String.format("%s and %s", strings.get(0), strings.get(1));
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strings.size(); i++) {
            stringBuilder.append(strings.get(i));
            if (i + 2 < strings.size()) {
                stringBuilder.append(", ");
            } else if (i + 2 == strings.size()) {
                stringBuilder.append(", and ");
            }
        }

        return stringBuilder.toString();
    }
}
