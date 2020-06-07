package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.commands.AddBookedStatementCommand;
import com.ryan_mtg.servobot.commands.CommandMapping;
import com.ryan_mtg.servobot.commands.ScoreCommand;
import com.ryan_mtg.servobot.commands.chat.AddCommand;
import com.ryan_mtg.servobot.commands.chat.AddReactionCommand;
import com.ryan_mtg.servobot.commands.AddStatementCommand;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.jail.ArrestCommand;
import com.ryan_mtg.servobot.commands.magic.CardSearchCommand;
import com.ryan_mtg.servobot.commands.magic.ScryfallSearchCommand;
import com.ryan_mtg.servobot.commands.trigger.CommandAlert;
import com.ryan_mtg.servobot.commands.trigger.CommandAlias;
import com.ryan_mtg.servobot.commands.trigger.CommandEvent;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.DelayedAlertCommand;
import com.ryan_mtg.servobot.commands.chat.DeleteCommand;
import com.ryan_mtg.servobot.commands.giveaway.EnterRaffleCommand;
import com.ryan_mtg.servobot.commands.EvaluateExpressionCommand;
import com.ryan_mtg.servobot.commands.chat.FactsCommand;
import com.ryan_mtg.servobot.commands.game_queue.GameQueueCommand;
import com.ryan_mtg.servobot.commands.jail.JailReleaseCommand;
import com.ryan_mtg.servobot.commands.giveaway.RaffleStatusCommand;
import com.ryan_mtg.servobot.commands.jail.JailBreakCommand;
import com.ryan_mtg.servobot.commands.jail.JailCommand;
import com.ryan_mtg.servobot.commands.game_queue.JoinGameQueueCommand;
import com.ryan_mtg.servobot.commands.chat.MessageChannelCommand;
import com.ryan_mtg.servobot.commands.game_queue.RemoveFromGameQueueCommand;
import com.ryan_mtg.servobot.commands.giveaway.RequestPrizeCommand;
import com.ryan_mtg.servobot.commands.giveaway.SelectWinnerCommand;
import com.ryan_mtg.servobot.commands.SetArenaUsernameCommand;
import com.ryan_mtg.servobot.commands.SetRoleCommand;
import com.ryan_mtg.servobot.commands.SetStatusCommand;
import com.ryan_mtg.servobot.commands.SetUsersRoleCommand;
import com.ryan_mtg.servobot.commands.SetValueCommand;
import com.ryan_mtg.servobot.commands.ShowArenaUsernamesCommand;
import com.ryan_mtg.servobot.commands.game_queue.ShowGameQueueCommand;
import com.ryan_mtg.servobot.commands.ShowValueCommand;
import com.ryan_mtg.servobot.commands.giveaway.StartRaffleCommand;
import com.ryan_mtg.servobot.commands.chat.TextCommand;
import com.ryan_mtg.servobot.commands.TierCommand;
import com.ryan_mtg.servobot.commands.trigger.Trigger;
import com.ryan_mtg.servobot.commands.trigger.TriggerVisitor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Getter
public class CommandDescriptor {
    private final Command command;
    private final String type;
    private final String description;
    private final String edit;
    private final List<CommandAlias> aliases = new ArrayList<>();
    private final List<CommandEvent> events = new ArrayList<>();
    private final List<CommandAlert> alerts = new ArrayList<>();

    public CommandDescriptor(final Command command) {
        this.command = command;
        DescriptorVisitor descriptorVisitor = new DescriptorVisitor();
        command.acceptVisitor(descriptorVisitor);
        this.type = command.getType().getName();
        this.description = descriptorVisitor.getDescription();
        this.edit = descriptorVisitor.getEdit();
    }

    public String getEdit() {
        return edit;
    }

    public void addTrigger(final Trigger trigger) {
        trigger.acceptVisitor(new TriggerAddingVisitor());
    }

    public static List<CommandDescriptor> getCommandDescriptors(final CommandMapping commandMapping) {
        List<CommandDescriptor> commands = new ArrayList<>();
        Map<Command, CommandDescriptor> commandMap = new HashMap<>();

        Function<Command, CommandDescriptor> createCommandDescriptor = command -> {
            CommandDescriptor newDescriptor = new CommandDescriptor(command);
            commands.add(newDescriptor);
            return newDescriptor;
        };

        for (Map.Entry<Integer, Command> entry : commandMapping.getIdToCommandMap().entrySet()) {
            commandMap.computeIfAbsent(entry.getValue(), createCommandDescriptor);
        }

        for (Map.Entry<Trigger, Command> entry : commandMapping.getTriggerCommandMap().entrySet()) {
            CommandDescriptor descriptor = commandMap.computeIfAbsent(entry.getValue(), createCommandDescriptor);
            descriptor.addTrigger(entry.getKey());
        }

        commands.sort(new CommandDescriptorIdComparer());
        return commands;
    }

    private class TriggerAddingVisitor implements TriggerVisitor {
        @Override
        public void visitCommandAlias(final CommandAlias commandAlias) {
            aliases.add(commandAlias);
        }

        @Override
        public void visitCommandEvent(final CommandEvent commandEvent) {
            events.add(commandEvent);
        }

        @Override
        public void visitCommandAlert(final CommandAlert commandAlert) {
            alerts.add(commandAlert);
        }
    }

    private class DescriptorVisitor implements CommandVisitor {
        private String description;
        private String edit;

        public String getDescription() {
            return description;
        }

        public String getEdit() {
            return edit;
        }

        @Override
        public void visitAddBookedStatementCommand(final AddBookedStatementCommand addBookedStatementCommand) {
            description = String.format("Used to add new statements to the %s book and responsed with '%s'",
                    addBookedStatementCommand.getBook().getName(), addBookedStatementCommand.getResponse());
        }

        @Override
        public void visitAddCommand(final AddCommand addCommand) {
            description = "Used to make new message commands";
        }

        @Override
        public void visitAddReactionCommand(final AddReactionCommand addReactionCommand) {
            description = String.format("Reacts to a message with the '%s' emote", addReactionCommand.getEmoteName());
            edit = addReactionCommand.getEmoteName();
        }

        @Override
        public void visitAddStatementCommand(final AddStatementCommand addStatementCommand) {
            description = "Used to make new statements";
        }

        @Override
        public void visitArrestCommand(final ArrestCommand arrestCommand) {
            description = String.format("Arrests the user passed as input, by giving them the role '%s' and says '%s'",
                    arrestCommand.getPrisonRole(), arrestCommand.getMessage());
        }

        @Override
        public void visitCardSearchCommand(final CardSearchCommand cardSearchCommand) {
            description = "Searches for a card by name";
        }

        @Override
        public void visitDelayedAlertCommand(final DelayedAlertCommand delayedAlertCommand) {
            description = String.format("Alerts '%s' after %s", delayedAlertCommand.getAlertToken(),
                    delayedAlertCommand.getDelay().toString());
            edit = delayedAlertCommand.getAlertToken();
        }

        @Override
        public void visitDeleteCommand(final DeleteCommand deleteCommand) {
            description = "Used to remove commands";
        }

        @Override
        public void visitEnterGiveawayCommand(final EnterRaffleCommand enterRaffleCommand) {
            description = "Enters the user into the current giveaway";
        }

        @Override
        public void visitEvaluateExpressionCommand(final EvaluateExpressionCommand evaluateExpressionCommand) {
            description = "Used to evaluate an expression";
        }

        @Override
        public void visitFactsCommand(final FactsCommand factsCommand) {
            description = "Gives a random statement from " + factsCommand.getBook().getName();
            edit = factsCommand.getBook().getName();
        }

        @Override
        public void visitGameQueueCommand(final GameQueueCommand gameQueueCommand) {
            description = "Has subcommands to manipulate the game queue";
        }

        @Override
        public void visitGiveawayStatusCommand(final RaffleStatusCommand raffleStatusCommand) {
            description = "Displays the status of the current giveaway";
        }

        @Override
        public void visitJailCommand(final JailCommand jailCommand) {
            description = String.format("Puts the user into '%s' if triggered %d times",
                    jailCommand.getPrisonRole(), jailCommand.getThreshold());
        }

        @Override
        public void visitJailBreakCommand(final JailBreakCommand jailBreakCommand) {
            description = String.format("Breaks all of the users out of '%s'",
                    jailBreakCommand.getPrisonRole());
        }

        @Override
        public void visitJailReleaseCommand(final JailReleaseCommand jailReleaseCommand) {
            description =String.format("Releases the users passed in as input out of '%s'",
                    jailReleaseCommand.getPrisonRole());
        }

        @Override
        public void visitJoinGameQueueCommand(final JoinGameQueueCommand joinGameQueueCommand) {
            description = "Adds the user to the end of the game queue";
        }

        @Override
        public void visitMessageChannelCommand(final MessageChannelCommand messageChannelCommand) {
            description = String.format("Sends the message '%s' to #%s on service %d",
                    messageChannelCommand.getMessage(), messageChannelCommand.getChannelName(),
                    messageChannelCommand.getServiceType());
            edit = messageChannelCommand.getMessage();
        }

        @Override
        public void visitRemoveFromGameQueueCommand(final RemoveFromGameQueueCommand removeFromGameQueueCommand) {
            description = "Removes the user from the game queue";
        }

        @Override
        public void visitRequestPrizeCommand(final RequestPrizeCommand requestPrizeCommand) {
            description = "Requests a giveaway prize";
        }

        @Override
        public void visitScryfallSearchCommand(final ScryfallSearchCommand scryfallSearchCommand) {
            description = "Searches Scryfall for a card";
        }

        @Override
        public void visitScoreCommand(final ScoreCommand scoreCommand) {
            description = String.format("Gives the scores for %s", scoreCommand.getGameName());
        }

        @Override
        public void visitSelectWinnerCommand(final SelectWinnerCommand selectWinnerCommand) {
            description = "Selects a winner for the current giveaway";
        }

        @Override
        public void visitSetArenaUsernameCommand(final SetArenaUsernameCommand setArenaUsernameCommand) {
            description = "Stores the user's arena username";
        }

        @Override
        public void visitSetRoleCommand(final SetRoleCommand setRoleCommand) {
            description = String.format("Sets the user's role to '%s'", setRoleCommand.getRole());
        }

        @Override
        public void visitSetStatusCommand(final SetStatusCommand setStatusCommand) {
            description = "Sets the status to a random statement from " + setStatusCommand.getBook().getName();
            edit = setStatusCommand.getBook().getName();
        }

        @Override
        public void visitSetUsersRoleCommand(final SetUsersRoleCommand setUsersRoleCommand) {
            description = String.format("Sets the user passed as input to the role '%s' and says '%s'",
                    setUsersRoleCommand.getRole(), setUsersRoleCommand.getMessage());
            edit = setUsersRoleCommand.getRole();

        }

        @Override
        public void visitSetValueCommand(final SetValueCommand setValueCommand) {
            description = "Resets a storage value";
        }

        @Override
        public void visitShowArenaUsernamesCommand(final ShowArenaUsernamesCommand showArenaUsernamesCommand) {
            description = "Shows the stored arena usernames";
        }

        @Override
        public void visitShowGameQueueCommand(final ShowGameQueueCommand showGameQueueCommand) {
            description = "Shows who is in the game queue";
        }

        @Override
        public void visitShowValueCommand(final ShowValueCommand showValueCommand) {
            description = "Shows the stored value";
        }

        @Override
        public void visitStartGiveawayCommand(final StartRaffleCommand startRaffleCommand) {
            description = "Starts a new giveaway";
        }

        @Override
        public void visitTextCommand(final TextCommand textCommand) {
            description = String.format("Responds with the message '%s'", textCommand.getText());
            edit = textCommand.getText();
        }

        @Override
        public void visitTierCommand(final TierCommand tierCommand) {
            description = "Gives the user's friendship tier";
        }
    }

    private static class CommandDescriptorIdComparer implements Comparator<CommandDescriptor> {
        @Override
        public int compare(final CommandDescriptor first, final CommandDescriptor second) {
            return first.getCommand().getId() - second.getCommand().getId();
        }
    }
}
