package com.ryan_mtg.servobot.commands.magic;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedCommand;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedEvent;
import com.ryan_mtg.servobot.scryfall.json.Card;
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
    public void perform(final CommandInvokedEvent event) throws BotHomeError, UserError {
        String query = event.getArguments();
        if (Strings.isBlank(query)) {
            throw new UserError("No search term provided.");
        }

        CardQuery cardQuery = CardUtil.resolveNickName(query);

        try {
            Card card = scryfallQuerier.searchForCardByName(cardQuery);
            if (event.getServiceType() == DiscordService.TYPE) {
                String fileName = CardUtil.getCardFileName(card);
                String imageUri = CardUtil.getCardImageUri(card);
                event.sendImage(imageUri, fileName, card.getName());
            } else {
                String response = CardUtil.respondToCardSearch(card, false);
                event.say(response);
            }
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
