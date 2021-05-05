package com.ryan_mtg.servobot.commands.giveaway;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.HomeCommand;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;
import com.ryan_mtg.servobot.events.HomeEvent;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.giveaway.Raffle;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.utility.Flags;
import com.ryan_mtg.servobot.utility.Strings;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class SelectWinnerCommand extends HomeCommand {
    public static final CommandType TYPE = CommandType.SELECT_WINNER_COMMAND_TYPE;

    @Getter
    private final int giveawayId;

    @Getter
    private final String discordChannel;

    @Getter
    private final String response;

    public SelectWinnerCommand(final int id, final CommandSettings commandSettings, final int giveawayId,
                               final String response, final String discordChannel) {
        super(id, commandSettings);
        this.giveawayId = giveawayId;
        this.response = response;
        this.discordChannel = discordChannel;
    }

    @Override
    public void perform(final HomeEvent homeEvent) throws BotHomeError, UserError {
        HomeEditor homeEditor = homeEvent.getHomeEditor();
        Scope scope = homeEditor.getScope();

        Raffle raffle = homeEditor.getGiveaway(giveawayId).retrieveCurrentRaffle();
        List<HomedUser> winners = homeEditor.selectRaffleWinners(giveawayId, homeEvent.getServiceType());

        SimpleSymbolTable symbolTable = new SimpleSymbolTable();
        String message;
        if (winners.isEmpty()) {
            message = "The raffle has no winner, because there were no entrants.";
        } else {
            String winnerString = Strings.join(winners.stream().map(HomedUser::getName).collect(Collectors.toList()));
            scope = new Scope(scope, symbolTable);
            symbolTable.addValue("winner", winnerString);
            symbolTable.addValue("raffle", raffle);
            message = response;
        }

        if (!Strings.isBlank(discordChannel)) {
            homeEvent.say(homeEvent.getServiceHome(DiscordService.TYPE).getChannel(discordChannel), scope, message);
        }

        if (homeEvent instanceof CommandInvokedHomeEvent) {
            ((CommandInvokedHomeEvent) homeEvent).say(symbolTable, message);
        } else if (Flags.hasFlag(getFlags(), TWITCH_FLAG)) {
            String twitchChannel = homeEditor.getTwitchChannelName();
            Channel channel = homeEvent.getServiceHome(DiscordService.TYPE).getChannel(twitchChannel);
            if (channel != null) {
                homeEvent.say(channel, scope, message);
            }
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
