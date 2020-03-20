package com.ryan_mtg.servobot.commands.jail;

import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.utility.Strings;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

import java.util.List;

public class JailBreakCommand extends MessageCommand {
    public static final int TYPE = 26;

    @Getter
    private String prisonRole;

    @Getter
    private String variableName;

    public JailBreakCommand(final int id, final int flags, final Permission permission, final String prisonRole,
                            final String variableName) throws BotErrorException {
        super(id, flags, permission);

        this.prisonRole = prisonRole;
        this.variableName = variableName;

        Validation.validateStringLength(prisonRole, Validation.MAX_ROLE_LENGTH, "Prison role");
        Validation.validateStringLength(variableName, Validation.MAX_STORAGE_NAME_LENGTH, "Threshold variable name");
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitJailBreakCommand(this);
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        User sender = event.getSender();

        List<String> inmates = event.getHome().clearRole(prisonRole);

        HomeEditor homeEditor = event.getHomeEditor();
        homeEditor.remoteStorageVariables(variableName);

        if (inmates.isEmpty()) {
            return;
        }

        MessageCommand.say(event,
                String.format("%s broke %s out of %s!", sender.getName(), Strings.join(inmates),prisonRole));
    }
}
