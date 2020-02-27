package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.scope.FunctorSymbolTable;
import com.ryan_mtg.servobot.model.scope.MessageSentSymbolTable;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.utility.Validation;

public class SetUsersRoleCommand extends MessageCommand {
    public static final int TYPE = 27;
    private String role;
    private String message;

    public SetUsersRoleCommand(final  int id, final int flags, final Permission permission, final String role,
                               final String message)
            throws BotErrorException {
        super(id, flags, permission);
        this.role = role;
        this.message = message;

        Validation.validateStringLength(role, Validation.MAX_ROLE_LENGTH, "Role");
    }

    public String getRole() {
        return role;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        String userName = arguments;
        if (userName == null) {
            throw new BotErrorException("Noone specified.");
        }

        if (userName.startsWith("@")) {
            userName = userName.substring(1);
        }

        event.getHome().setRole(userName, role);

        HomeEditor homeEditor = event.getHomeEditor();
        Scope messageEventScope = new Scope(homeEditor.getScope(), new MessageSentSymbolTable(event, userName));
        FunctorSymbolTable setUsersRoleSymbolTable = new FunctorSymbolTable();
        setUsersRoleSymbolTable.addFunctor("user", () -> arguments);
        setUsersRoleSymbolTable.addFunctor("role", () -> role);

        Scope setUsersRoleScope = new Scope(messageEventScope, setUsersRoleSymbolTable);
        MessageCommand.say(event, setUsersRoleScope, message);
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitSetUsersRoleCommand(this);
    }
}
