package com.ryan_mtg.servobot.commands.roles;

import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedHomedCommand;
import com.ryan_mtg.servobot.discord.model.DiscordEmoji;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.editors.RoleTableEditor;
import com.ryan_mtg.servobot.model.roles.Role;

import java.util.List;

public class MakeRoleMessageCommand extends InvokedHomedCommand {
    public static final CommandType TYPE = CommandType.MAKE_ROLE_MESSAGE_COMMAND_TYPE;

    public MakeRoleMessageCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    @Override
    public void perform(final CommandInvokedHomeEvent commandInvokedHomeEvent) throws BotHomeError, UserError {
        RoleTableEditor roleTableEditor = commandInvokedHomeEvent.getRoleTableEditor();
        List<Role> roles = roleTableEditor.getRoles();

        if (roles.isEmpty()) {
            commandInvokedHomeEvent.say("There are no roles");
            return;
        }

        StringBuilder text = new StringBuilder();
        text.append("React to this message to be assigned a role\n\n");
        for (Role role : roles) {
            text.append(role.getEmote()).append(": ").append(role.getRole()).append('\n');
        }

        Message message = commandInvokedHomeEvent.getChannel().sayAndWait(text.toString());
        roleTableEditor.setMessage(message);

        for (Role role : roles) {
            message.addEmote(new DiscordEmoji(role.getEmote()));
        }
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitMakeRoleMessageCommand(this);
    }
}