package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.utility.Validation;

import java.util.List;

public class JailBreakCommand extends MessageCommand {
    public static final int TYPE = 26;
    private String prisonRole;
    private String variableName;

    public JailBreakCommand(final int id, final int flags, final Permission permission, final String prisonRole,
                            final String variableName) throws BotErrorException {
        super(id, flags, permission);

        this.prisonRole = prisonRole;
        this.variableName = variableName;

        Validation.validateStringLength(prisonRole, Validation.MAX_ROLE_LENGTH, "Prison role");
        Validation.validateStringLength(variableName, Validation.MAX_STORAGE_NAME_LENGTH, "Threshold variable name");
    }

    public String getPrisonRole() {
        return prisonRole;
    }

    public String getVariableName() {
        return variableName;
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
                String.format("%s broke %s out of %s!", sender.getName(), print(inmates),prisonRole));
    }

    private String print(final List<String> names) {
        StringBuilder builder = new StringBuilder();
        builder.append(names.get(0));
        if (names.size() == 2) {
            builder.append(" and ").append(names.get(1));
            return builder.toString();
        }

        for (int i = 1; i < names.size(); i++) {
            builder.append(", ");
            if (i + 1 == names.size()) {
                builder.append("and ");
            }
            builder.append(names.get(i));
        }

        return builder.toString();
    }
}
