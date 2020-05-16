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

public class CardSearchCommand extends MessageCommand {
    public static final CommandType TYPE = CommandType.CARD_SEARCH_COMMAND_TYPE;

    private ScryfallQuerier scryfallQuerier;

    public CardSearchCommand(final int id, final int flags, final Permission permission,
                                 final ScryfallQuerier scryfallQuerier) {
        super(id, flags, permission);
        this.scryfallQuerier = scryfallQuerier;
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        if (Strings.isBlank(arguments)) {
            throw new BotErrorException("No search term provided.");
        }

        String query = arguments;

        String lower = query.toLowerCase();

        switch (lower) {
            case "sfm":
                query = "Soulfire Grandmaster";
                break;
            case "snek":
                query = "Ambush Viper";
                break;
            case "goyf":
                query = "Tarmogoyf";
                break;
            case "sfg":
                query = "Stoneforge Mystic";
                break;
            case "bob":
                query = "Dark Confidant";
                break;
        }

        try {
            Card card = scryfallQuerier.searchForCardByName(query);
            String response = CardUtil.respondToCardSearch(card, event.getServiceType() == DiscordService.TYPE);
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
        commandVisitor.visitCardSearchCommand(this);
    }
}
