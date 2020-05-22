package com.ryan_mtg.servobot.commands.giveaway;

import com.ryan_mtg.servobot.commands.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.HomeCommand;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.HomeEvent;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.utility.Flags;
import com.ryan_mtg.servobot.utility.Strings;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

public class SelectWinnerCommand extends HomeCommand {
    public static final CommandType TYPE = CommandType.SELECT_WINNER_COMMAND_TYPE;

    @Getter
    private int giveawayId;

    @Getter
    private String discordChannel;

    @Getter
    private String response;

    public SelectWinnerCommand(final int id, final CommandSettings commandSettings, final int giveawayId,
                               final String response, final String discordChannel) {
        super(id, commandSettings);
        this.giveawayId = giveawayId;
        this.response = response;
        this.discordChannel = discordChannel;
    }

    @Override
    public void perform(final HomeEvent homeEvent) throws BotErrorException {
        HomeEditor homeEditor = homeEvent.getHomeEditor();
        Home home = homeEvent.getHome();
        Scope scope = homeEditor.getScope();

        List<HomedUser> winners = homeEditor.selectRaffleWinners(giveawayId);

        String message;
        if (winners.isEmpty()) {
            message = "The raffle has no winner, because there were no entrants.";
        } else {
            String winnerString = Strings.join(
                    winners.stream().map(homedUser -> homedUser.getName()).collect(Collectors.toList()));
            SimpleSymbolTable symbolTable = new SimpleSymbolTable();
            scope = new Scope(scope, symbolTable);
            symbolTable.addValue("winner", winnerString);
            message = response;
        }

        if (Flags.hasFlag(getFlags(), DISCORD_FLAG) && discordChannel != null && !discordChannel.isEmpty()) {
            Command.say(home.getChannel(discordChannel, DiscordService.TYPE), homeEvent, scope, message);
        }

        if (Flags.hasFlag(getFlags(), TWITCH_FLAG)) {
            String twitchChannel = homeEditor.getTwitchChannelName();
            Command.say(home.getChannel(twitchChannel, TwitchService.TYPE), homeEvent, scope, message);
        }
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitSelectWinnerCommand(this);
    }
}
