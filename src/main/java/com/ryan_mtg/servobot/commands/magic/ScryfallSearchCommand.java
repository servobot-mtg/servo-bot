package com.ryan_mtg.servobot.commands.magic;

import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.scryfall.Card;
import com.ryan_mtg.servobot.scryfall.ScryfallQuerier;
import com.ryan_mtg.servobot.scryfall.ScryfallQueryException;
import com.ryan_mtg.servobot.utility.Strings;

import java.util.List;

public class ScryfallSearchCommand extends MessageCommand {
    public static final CommandType TYPE = CommandType.SCRYFALL_SEARCH_COMMAND_TYPE;

    private ScryfallQuerier scryfallQuerier;

    public ScryfallSearchCommand(final int id, final int flags, final Permission permission,
            final ScryfallQuerier scryfallQuerier) {
        super(id, flags, permission);
        this.scryfallQuerier = scryfallQuerier;
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        if (Strings.isBlank(arguments)) {
            throw new BotErrorException("No search term provided.");
        }

        try {
            List<Card> cards = scryfallQuerier.searchForCards(arguments);
            String response = CardUtil.respondToCardSearch(cards, event.getServiceType() == DiscordService.TYPE);
            MessageCommand.say(event, response);
        } catch (ScryfallQueryException e) {
            MessageCommand.say(event, e.getDetails());
        }
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitScryfallSearchCommand(this);
    }
}
