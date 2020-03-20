package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.commands.AddCommand;
import com.ryan_mtg.servobot.commands.AddReactionCommand;
import com.ryan_mtg.servobot.commands.AddStatementCommand;
import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.CommandAlert;
import com.ryan_mtg.servobot.commands.CommandAlias;
import com.ryan_mtg.servobot.commands.CommandEvent;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.DelayedAlertCommand;
import com.ryan_mtg.servobot.commands.DeleteCommand;
import com.ryan_mtg.servobot.commands.EnterRaffleCommand;
import com.ryan_mtg.servobot.commands.EvaluateExpressionCommand;
import com.ryan_mtg.servobot.commands.FactsCommand;
import com.ryan_mtg.servobot.commands.GameQueueCommand;
import com.ryan_mtg.servobot.commands.JailReleaseCommand;
import com.ryan_mtg.servobot.commands.RaffleStatusCommand;
import com.ryan_mtg.servobot.commands.JailBreakCommand;
import com.ryan_mtg.servobot.commands.JailCommand;
import com.ryan_mtg.servobot.commands.JoinGameQueueCommand;
import com.ryan_mtg.servobot.commands.MessageChannelCommand;
import com.ryan_mtg.servobot.commands.RemoveFromGameQueueCommand;
import com.ryan_mtg.servobot.commands.RequestPrizeCommand;
import com.ryan_mtg.servobot.commands.SelectWinnerCommand;
import com.ryan_mtg.servobot.commands.SetArenaUsernameCommand;
import com.ryan_mtg.servobot.commands.SetRoleCommand;
import com.ryan_mtg.servobot.commands.SetStatusCommand;
import com.ryan_mtg.servobot.commands.SetUsersRoleCommand;
import com.ryan_mtg.servobot.commands.SetValueCommand;
import com.ryan_mtg.servobot.commands.ShowArenaUsernamesCommand;
import com.ryan_mtg.servobot.commands.ShowGameQueueCommand;
import com.ryan_mtg.servobot.commands.ShowValueCommand;
import com.ryan_mtg.servobot.commands.StartRaffleCommand;
import com.ryan_mtg.servobot.commands.TextCommand;
import com.ryan_mtg.servobot.commands.TierCommand;
import com.ryan_mtg.servobot.commands.Trigger;
import com.ryan_mtg.servobot.commands.TriggerVisitor;

import java.util.ArrayList;
import java.util.List;

public class CommandDescriptor {
    private Command command;
    private String type;
    private String description;
    private String edit;
    private List<CommandAlias> aliases = new ArrayList<>();
    private List<CommandEvent> events = new ArrayList<>();
    private List<CommandAlert> alerts = new ArrayList<>();

    public CommandDescriptor(final Command command) {
        this.command = command;
        DescriptorVisitor descriptorVisitor = new DescriptorVisitor();
        command.acceptVisitor(descriptorVisitor);
        this.type = descriptorVisitor.getType();
        this.description = descriptorVisitor.getDescription();
        this.edit = descriptorVisitor.getEdit();
    }

    public Command getCommand() {
        return command;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getEdit() {
        return edit;
    }

    public List<CommandAlias> getAliases() {
        return aliases;
    }

    public List<CommandEvent> getEvents() {
        return events;
    }

    public List<CommandAlert> getAlerts() {
        return alerts;
    }

    public void addTrigger(final Trigger trigger) {
        trigger.acceptVisitor(new TriggerAddingVisitor());
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
        private String type;
        private String description;
        private String edit;

        public String getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        public String getEdit() {
            return edit;
        }

        @Override
        public void visitAddCommand(final AddCommand addCommand) {
            type = "Add Command";
            description = "Used to make new message commands";
        }

        @Override
        public void visitAddReactionCommand(final AddReactionCommand addReactionCommand) {
            type = "Add Reaction Command";
            description = String.format("Reacts to a message with the '%s' emote", addReactionCommand.getEmoteName());
            edit = addReactionCommand.getEmoteName();
        }

        @Override
        public void visitAddStatementCommand(final AddStatementCommand addStatementCommand) {
            type = "Add Statement Command";
            description = "Used to make new statements";
        }

        @Override
        public void visitDelayedAlertCommand(final DelayedAlertCommand delayedAlertCommand) {
            type = "Delayed Alert Command";
            description = String.format("Alerts '%s' after %s", delayedAlertCommand.getAlertToken(),
                    delayedAlertCommand.getDelay().toString());
            edit = delayedAlertCommand.getAlertToken();
        }

        @Override
        public void visitDeleteCommand(final DeleteCommand deleteCommand) {
            type = "Delete Command";
            description = "Used to remove commands";
        }

        @Override
        public void visitEnterGiveawayCommand(final EnterRaffleCommand enterRaffleCommand) {
            type = "Enter Giveaway Command";
            description = "Enters the user into the current giveaway";
        }

        @Override
        public void visitEvaluateExpressionCommand(final EvaluateExpressionCommand evaluateExpressionCommand) {
            type = "Math Command";
            description = "Used to evaluate an expression";
        }

        @Override
        public void visitFactsCommand(final FactsCommand factsCommand) {
            type = "Random Statement Command";
            description = "Gives a random statement from " + factsCommand.getBook().getName();
            edit = factsCommand.getBook().getName();
        }

        @Override
        public void visitGameQueueCommand(final GameQueueCommand gameQueueCommand) {
            type = "Game Queue Command";
            description = "Has subcommands to manipulate the game queue";
        }

        @Override
        public void visitGiveawayStatusCommand(final RaffleStatusCommand raffleStatusCommand) {
            type = "Giveaway Status Command";
            description = "Displays the status of the current giveaway";
        }

        @Override
        public void visitJailCommand(final JailCommand jailCommand) {
            type = "Jail Command";
            description = String.format("Puts the user into '%s' if triggered %d times",
                    jailCommand.getPrisonRole(), jailCommand.getThreshold());
        }

        @Override
        public void visitJailBreakCommand(final JailBreakCommand jailBreakCommand) {
            type = "Jail Break Command";
            description = String.format("Breaks all of the users out of '%s'",
                    jailBreakCommand.getPrisonRole());
        }

        @Override
        public void visitJailReleaseCommand(final JailReleaseCommand jailReleaseCommand) {
            type = "Jail Release Command";
            description =String.format("Releases the users passed in as input out of '%s'",
                    jailReleaseCommand.getPrisonRole());
        }

        @Override
        public void visitJoinGameQueueCommand(final JoinGameQueueCommand joinGameQueueCommand) {
            type = "Join Game Queue Command";
            description = "Adds the user to the end of the game queue";
        }

        @Override
        public void visitMessageChannelCommand(final MessageChannelCommand messageChannelCommand) {
            type = "Message Channel Command";
            description = String.format("Sends the message '%s' to #%s", messageChannelCommand.getMessage(),
                    messageChannelCommand.getChannelName());
            edit = messageChannelCommand.getMessage();
        }

        @Override
        public void visitRemoveFromGameQueueCommand(final RemoveFromGameQueueCommand removeFromGameQueueCommand) {
            type = "Remove From Game Queue Command";
            description = "Removes the user from the game queue";
        }

        @Override
        public void visitRequestPrizeCommand(final RequestPrizeCommand requestPrizeCommand) {
            type = "Request Prize Command";
            description = "Requests a giveaway prize";
        }

        @Override
        public void visitSelectWinnerCommand(final SelectWinnerCommand selectWinnerCommand) {
            type = "Select Giveaway Winner Command";
            description = "Selects a winner for the current giveaway";
        }

        @Override
        public void visitSetArenaUsernameCommand(final SetArenaUsernameCommand setArenaUsernameCommand) {
            type = "Set Arena Username Command";
            description = "Stores the user's arena username";
        }

        @Override
        public void visitSetRoleCommand(final SetRoleCommand setRoleCommand) {
            type = "Set Role Command";
            description = String.format("Sets the user's role to '%s'", setRoleCommand.getRole());
        }

        @Override
        public void visitSetStatusCommand(final SetStatusCommand setStatusCommand) {
            type = "Set Status Command";
            description = "Sets the status to a random statement from " + setStatusCommand.getBook().getName();
            edit = setStatusCommand.getBook().getName();
        }

        @Override
        public void visitSetUsersRoleCommand(final SetUsersRoleCommand setUsersRoleCommand) {
            type = "Set Users Role Command";
            description = String.format("Sets the user passed as input to the role '%s' and says '%s'",
                    setUsersRoleCommand.getRole(), setUsersRoleCommand.getMessage());
            edit = setUsersRoleCommand.getRole();

        }

        @Override
        public void visitSetValueCommand(final SetValueCommand setValueCommand) {
            type = "Set Value Command";
            description = "Resets a storage value";
        }

        @Override
        public void visitShowArenaUsernamesCommand(final ShowArenaUsernamesCommand showArenaUsernamesCommand) {
            type = "Show Arena Usernames Command";
            description = "Shows the stored arena usernames";
        }

        @Override
        public void visitShowGameQueueCommand(final ShowGameQueueCommand showGameQueueCommand) {
            type = "Show Game Queue Command";
            description = "Shows who is in the game queue";
        }

        @Override
        public void visitShowValueCommand(final ShowValueCommand showValueCommand) {
            type = "Show Value Command";
            description = "Shows the stored value";
        }

        @Override
        public void visitStartGiveawayCommand(final StartRaffleCommand startRaffleCommand) {
            type = "Start Giveaway Command";
            description = "Starts a new giveaway";
        }

        @Override
        public void visitTextCommand(final TextCommand textCommand) {
            type = "Respond Command";
            description = String.format("Responds with the message '%s'", textCommand.getText());
            edit = textCommand.getText();
        }

        @Override
        public void visitTierCommand(final TierCommand tierCommand) {
            type = "Friendship Tier Command";
            description = "Gives the user's friendship tier";
        }
    }
}
