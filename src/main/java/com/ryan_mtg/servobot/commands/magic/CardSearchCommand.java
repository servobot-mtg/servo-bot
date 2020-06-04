package com.ryan_mtg.servobot.commands.magic;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedCommand;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.CommandInvokedEvent;
import com.ryan_mtg.servobot.scryfall.Card;
import com.ryan_mtg.servobot.scryfall.ScryfallQuerier;
import com.ryan_mtg.servobot.scryfall.ScryfallQueryException;
import com.ryan_mtg.servobot.utility.Strings;

public class CardSearchCommand extends InvokedCommand {
    public static final CommandType TYPE = CommandType.CARD_SEARCH_COMMAND_TYPE;

    private ScryfallQuerier scryfallQuerier;

    public CardSearchCommand(final int id, final CommandSettings commandSettings,
                             final ScryfallQuerier scryfallQuerier) {
        super(id, commandSettings);
        this.scryfallQuerier = scryfallQuerier;
    }

    @Override
    public void perform(final CommandInvokedEvent event) throws BotErrorException {
        String query = event.getArguments();
        if (Strings.isBlank(query)) {
            throw new BotErrorException("No search term provided.");
        }

        String lower = query.toLowerCase();

        switch (lower) {
            case "sfm":
                query = "Soulfire Grand Master";
                break;
            case "snek":
                query = "Ambush Viper";
                break;
            case "goyf":
                query = "Tarmogoyf";
                break;
            case "sfg":
            case "sgm":
                query = "Stoneforge Mystic";
                break;
            case "bob":
                query = "Dark Confidant";
                break;
        }

        try {
            Card card = scryfallQuerier.searchForCardByName(query);
            String response = CardUtil.respondToCardSearch(card, event.getServiceType() == DiscordService.TYPE);
            event.say(response);
        } catch (ScryfallQueryException e) {
            event.say(e.getDetails());
        }
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitCardSearchCommand(this);
    }
}
