package com.ryan_mtg.servobot.commands.jail;

import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class JailCommand extends MessageCommand {
    public static final CommandType TYPE = CommandType.JAIL_COMMAND_TYPE;

    @Getter
    private String prisonRole;

    @Getter
    private int threshold;

    @Getter
    private String variableName;

    public JailCommand(final int id, final int flags, final Permission permission, final String prisonRole,
                       final int threshold, final String variableName) throws BotErrorException {
        super(id, flags, permission);
        this.threshold = threshold;
        this.prisonRole = prisonRole;
        this.variableName = variableName;

        Validation.validateStringLength(prisonRole, Validation.MAX_ROLE_LENGTH, "Prison role");
        Validation.validateStringLength(variableName, Validation.MAX_STORAGE_NAME_LENGTH, "Threshold variable name");
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitJailCommand(this);
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        User sender = event.getSender();

        if (JailUtility.isInJail(event.getHome(), sender, prisonRole)) {
            return;
        }

        SimpleSymbolTable symbolTable = new SimpleSymbolTable();
        symbolTable.addValue("criminal", sender.getName());
        symbolTable.addValue("cop", event.getHome().getBotName());
        symbolTable.addValue("role", prisonRole);

        event.getHome().setRole(sender, prisonRole);
        MessageCommand.say(event, symbolTable, "%criminal%, I'm throwing the book at you!");
    }
}
