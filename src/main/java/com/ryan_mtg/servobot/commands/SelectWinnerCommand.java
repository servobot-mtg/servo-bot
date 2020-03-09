package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.HomeEvent;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.scope.FunctorSymbolTable;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.utility.Flags;
import com.ryan_mtg.servobot.utility.Strings;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

public class SelectWinnerCommand extends HomeCommand {
    public static final int TYPE = 23;

    @Getter
    private int giveawayId;

    @Getter
    private String discordChannel;

    @Getter
    private String response;

    public SelectWinnerCommand(final int id, final int flags, final Permission permission, final int giveawayId,
                               final String response, final String discordChannel) {
        super(id, flags, permission);
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
                    winners.stream().map(winner -> winner.getName()).collect(Collectors.toList()));
            FunctorSymbolTable symbolTable = new FunctorSymbolTable();
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
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitSelectWinnerCommand(this);
    }
}
