package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.commands.AddCommand;
import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.CommandAlert;
import com.ryan_mtg.servobot.commands.CommandAlias;
import com.ryan_mtg.servobot.commands.CommandEvent;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.DeleteCommand;
import com.ryan_mtg.servobot.commands.FactsCommand;
import com.ryan_mtg.servobot.commands.GameQueueCommand;
import com.ryan_mtg.servobot.commands.JoinGameQueueCommand;
import com.ryan_mtg.servobot.commands.MessageChannelCommand;
import com.ryan_mtg.servobot.commands.RemoveFromGameQueueCommand;
import com.ryan_mtg.servobot.commands.SetArenaUsernameCommand;
import com.ryan_mtg.servobot.commands.ShowArenaUsernamesCommand;
import com.ryan_mtg.servobot.commands.ShowGameQueueCommand;
import com.ryan_mtg.servobot.commands.TextCommand;
import com.ryan_mtg.servobot.commands.TierCommand;

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

    public void addAlias(final CommandAlias alias) {
        aliases.add(alias);
    }

    public List<CommandEvent> getEvents() {
        return events;
    }

    public void addEvent(final CommandEvent event) {
        events.add(event);
    }

    public List<CommandAlert> getAlerts() {
        return alerts;
    }

    public void addAlert(final CommandAlert alert) {
        alerts.add(alert);
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
        public void visitDeleteCommand(final DeleteCommand deleteCommand) {
            type = "Delete Command";
            description = "Used to remove commands";
        }

        @Override
        public void visitFactsCommand(final FactsCommand factsCommand) {
            type = "Random Command";
            description = "Gives a random statement from " + factsCommand.getBook().getName();
            edit = factsCommand.getBook().getName();
        }

        @Override
        public void visitGameQueueCommand(final GameQueueCommand gameQueueCommand) {
            type = "Game Queue Command";
            description = "Has subcommands to manipulate the game queue";
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
        public void visitSetArenaUsernameCommand(final SetArenaUsernameCommand setArenaUsernameCommand) {
            type = "Set Arena Username Command";
            description = "Stores the user's arena username";
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
        public void visitTextCommand(final TextCommand textCommand) {
            type = "Respond Command";
            description = String.format("Responds with the message '%s'", textCommand.getText());
            edit = textCommand.getText();
        }

        @Override
        public void visitTierCommand(final TierCommand tierCommand) {
            type = "Respond Command";
            description = "Gives the user's friendship tier";
        }
    }
}
