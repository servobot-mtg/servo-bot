package com.ryan_mtg.servobot.game.sus.chat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SusResponder {
    private final Random RANDOM = new Random();

    private final Map<String, SusResponse> responseMap = new HashMap<>();

    public void add(final List<String> aliases, final SusResponse response) {
        aliases.forEach(alias -> responseMap.put(alias.toLowerCase(), response));
    }

    private void add(final SusResponse response, final String... aliases) {
        for (String alias : aliases) {
            responseMap.put(alias.toLowerCase(), response);
        }
    }

    private void add(final String response, final String... aliases) {
        add(SusResponse.create(response), aliases);
    }

    public void initalize() {
        add("<name> has been hard cleared.", "lsv", "luis");
        add(SusResponse.create("Even a bot can tell that <name> is being sus.", "<name> is quite sus gabyDerp.",
                "<name> has been hard cleared."), "gaby", "gooby", "gabyspartz");
        add("<name> has been soft cleared. Like really soft. Taffy.", "bk", "abaeckstrom", "abaeckst");
        add("More like dishonest BK, am I right?", "honest bk");
        add("<name> is as sus as Simic Slaw.", "graham", "grahamlrr", "graham_lrr");
        add(SusResponse.create(2, "<name> is railroading! That's quite sus.", 1,
                "Is <name> the third imposter?!?! gabyHmm"),"wrapter", "wraptero");
        add(SusResponse.create(1, "What? <name> is sus? Is Pheylop dead?",
                2, "<name> is sus. Pheylop must be dead."), "mani", "zapgaze");
        add("More like SuslockFTW, am I right?", "stunlock", "stunlockftw");
        add("Fan favorite or not, <name> is sus!", "haiyue", "niphette");
        add("<name> is sus af. She's always the imposter!", "mikaela", "mythicmikaela");
        add("Of course <name> is sus, she's a stone cold killer!", "zyla", "babyberluce");
        add("<zeela> is sus, but Zyla is clear!", "zeela");
        add("Who be sus? <name> sus!", "corey", "coreyb", "corey_burkhart");
        add("With a name like <name>, she has to be sus!", "bloody");
        add("<name> is sus, but zulubars is clear!", "zlubars");
        add("<name> is sus, but zlubars is clear!", "zulubars", "zooloobars");
        add("<name> is above reproach, but nonetheless is quite sus!", "lady", "ladyatarka");
        add("How can <name> be sus when Mikaela killed him?", "tomlocke", "brandon");
        add("Even a sus <name> is right twice a day. However this isn't one of those times.",
                "squirrel", "squirrel_loot");
        add("<name> is still alive? Seems awfully sus to me.", "ondrej", "ondrejStrasky", "honey");
        add("<name> is a good crewmate... Oh no! I can't do it. <name> is sus. <name> is the imposter!",
                "riley", "rileyquarytower");
    }

    public String respond(final String name, final String sender) {
        String lowerName = name.toLowerCase().trim();
        if (lowerName.equals("mtgbot") || lowerName.isEmpty()) {
            if (!sender.isEmpty()) {
                return respond(sender, "");
            }
            return "Don't be suspicious! Don't be suspicious!";
        }

        if (responseMap.containsKey(lowerName)) {
            return responseMap.get(lowerName).respond(name, sender, RANDOM);
        }

        return String.format("%s is extremely sus.", name);
    }
}
