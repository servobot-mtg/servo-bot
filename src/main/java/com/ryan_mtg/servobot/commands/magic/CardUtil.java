package com.ryan_mtg.servobot.commands.magic;

import com.ryan_mtg.servobot.scryfall.json.Card;

import java.util.List;
import java.util.stream.Collectors;

public class CardUtil {
    public static String respondToCardSearch(final Card card, final boolean supportsNewLines) {
        if (card == null) {
            return "No matches found.";
        }

        return CardUtil.formatCard(card, supportsNewLines);
    }

    public static CardQuery resolveNickName(final String query, final boolean useEasterEggs) {
        String lower = query.toLowerCase();

        switch (lower) {
            case "baby jace":
                return new CardQuery("Jace Vryn's Prodigy");
            case "battle ball":
                return new CardQuery("Myr battlesphere");
            case "bob":
                return new CardQuery("Dark Confidant", "rav");
            case "bop":
            case "boppers":
                return new CardQuery("Birds of Paradise");
            case "carny t":
            case "carnyt":
                return new CardQuery("Carnage Tyrant");
            case "e wit":
                return new CardQuery("Eternal Witness");
            case "eugene":
                return new CardQuery("Ugin, the Spirit Dragon");
            case "fof":
            case "foffers":
                return new CardQuery("Fact or Fiction");
            case "gary":
                return new CardQuery("Gray Merchange of Asphodel");
            case "goyf":
                return new CardQuery("Tarmogoyf", "fut");
            case "gravy t":
            case "gravyt":
                return new CardQuery("Grave Titan");
            case "hippy":
            case "hippie":
                return new CardQuery("Hypnotic Specter", "alpha");
            case "hoof":
                return new CardQuery("Craterhoof Behemoth");
            case "house":
                return new CardQuery("Bolas's Citadel");
            case "k command":
                return new CardQuery("Kolaghan's Command");
            case "led":
                return new CardQuery("Lion's Eye Diamond");
            case "mom":
                return new CardQuery("Mother of Runes");
            case "muli duli":
                return new CardQuery("Oracle of Mul'Daya");
            case "needle":
                return new CardQuery("Pithing Needle");
            case "phimage":
                return new CardQuery("Phantasmal Image");
            case "poly k":
            case "polyk":
                return new CardQuery("Polukranos");
            case "prime time":
                return new CardQuery("Primeval Titan");
            case "sad robot":
                return new CardQuery("Solemn Simulacrum", "mrd");
            case "scooze":
                return new CardQuery("Scavenging Ooze");
            case "sfg":
                return new CardQuery("Stoneforge Mystic");
            case "sfm":
                return new CardQuery("Soulfire Grand Master");
            case "shelly":
                return new CardQuery("Shelldock Isle");
            case "sloth":
            case "slothers":
                return new CardQuery("Arboreal Grazer");
            case "snappy":
                return new CardQuery("Snapcaster Mage");
            case "snek":
                return new CardQuery("Ambush Viper");
            case "steve":
                return new CardQuery("Sakura-Tribe Elder");
            case "tim":
                return new CardQuery("Prodigal Sorcerer", "alpha");
            case "trop":
                return new CardQuery("Tropical Island");
            case "tsg":
                return new CardQuery("The Scarab God");
            case "v clique":
            case "vclique":
                return new CardQuery("Vendilion Clique");
            case "yogwill":
                return new CardQuery("Yawgmoth's Will");
        }

        if (!useEasterEggs) {
            return new CardQuery(query);
        }

        switch (lower) {
            case "ball":
                return new CardQuery("Myr battlesphere");
            case "brazy b":
            case "brazyb":
                return new CardQuery("Brazen Borrower");
            case "carnitas":
                return new CardQuery("Carnage Tyrant");
            case "chup":
            case "chalupacabra":
                return new CardQuery("Ravenous Chupacabra");
            case "geddon":
                return new CardQuery("Armageddon");
            case "glen":
                return new CardQuery("Glen Elendra Archmage");
            case "griselbee":
                return new CardQuery("Griselbrand");
            case "lady":
                return new CardQuery("Dragonlord Atarka");
            case "mulli d":
            case "mullid":
                return new CardQuery("Mulldrifter");
            case "young peezy":
                return new CardQuery("Young Pyromancer");
            case "mulli's invitational card":
                return new CardQuery("Baleful Strix");
            case "reginald":
                return new CardQuery("Rotting Regisaur");
            case "slime ball":
                return new CardQuery("Acidic Slime");
            case "soap":
                return new CardQuery("Simic Signet");
            case "sofi":
            case "sophie":
                return new CardQuery("Sword of Fire and Ice");
            case "splashiok":
                return new CardQuery("Ashiok, Nightmare Weaver");
        }

        return new CardQuery(query);
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


    public static String getCardImageUri(final Card card) {
        if (card.getImageUris() != null) {
            return card.getImageUris().getNormal();
        }
        return card.getCardFaces().get(0).getImageUris().getNormal();
    }

    public static String getCardFileName(final Card card) {
        return String.format("%s.png", card.getName().toLowerCase().replaceAll("\\W", "_"));
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
