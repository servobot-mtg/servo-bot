package com.ryan_mtg.servobot.commands.jail;

import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class ArrestCommand extends MessageCommand {
    public static final int TYPE = 30;

    @Getter
    private String prisonRole;

    @Getter
    private String message;

    public ArrestCommand(final int id, final int flags, final Permission permission, final String prisonRole,
            final String message) throws BotErrorException {
        super(id, flags, permission);

        this.prisonRole = prisonRole;
        this.message = message;

        Validation.validateStringLength(prisonRole, Validation.MAX_ROLE_LENGTH, "Prison role");
        Validation.validateStringLength(message, Validation.MAX_STORAGE_NAME_LENGTH, "Threshold message");
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitArrestCommand(this);
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        Home home = event.getHome();
        User cop  = event.getSender();

        if (JailUtility.isInAnyJail(home, cop, prisonRole)) {
            MessageCommand.say(event, String.format("You can't arrest someone while in jail!", prisonRole));
            return;
        }

        if (!home.hasUser(arguments)) {
            if (home.hasRole(arguments)) {
                home.setRole(cop, prisonRole);
                MessageCommand.say(event, "No one is above the law! %sender% is going to the clink.");
                return;
            }
            MessageCommand.say(event, String.format("No user with name %s.", arguments));
        }

        User criminal = home.getUser(arguments);
        if (home.isHigherRanked(criminal, cop)) {
            home.setRole(event.getSender(), prisonRole);
            MessageCommand.say(event, "I see through your tricks! I'm checking %sender% into the greybar hotel.");
            return;
        }

        if (JailUtility.isInAnyJail(home, criminal, prisonRole)) {
            MessageCommand.say(event, String.format("%s is already in jail!", criminal.getName(), prisonRole));
            return;
        }

        home.setRole(criminal, prisonRole);

        SimpleSymbolTable symbolTable = new SimpleSymbolTable();
        symbolTable.addValue("input", arguments);
        symbolTable.addValue("criminal", criminal.getName());
        symbolTable.addValue("cop", cop.getName());
        symbolTable.addValue("role", prisonRole);

        MessageCommand.say(event, symbolTable, message);
    }
}
