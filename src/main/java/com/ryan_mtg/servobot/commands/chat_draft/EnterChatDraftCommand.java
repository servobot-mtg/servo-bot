package com.ryan_mtg.servobot.commands.chat_draft;

import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedHomedCommand;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraft;
import com.ryan_mtg.servobot.model.editors.ChatDraftEditor;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class EnterChatDraftCommand extends InvokedHomedCommand {
    public static final CommandType TYPE = CommandType.ENTER_CHAT_DRAFT_COMMAND_TYPE;

    @Getter
    private final String response;

    @Getter
    private final int chatDraftId;

    public EnterChatDraftCommand(final int id, final CommandSettings commandSettings, final int chatDraftId,
                                final String response) throws UserError {
        super(id, commandSettings);
        this.response = response;
        this.chatDraftId = chatDraftId;

        Validation.validateStringLength(response, Validation.MAX_TEXT_LENGTH, "Command response");
    }

    @Override
    public void perform(final CommandInvokedHomeEvent event) throws BotHomeError, UserError {
        ChatDraftEditor chatDraftEditor = event.getChatDraftEditor();

        ChatDraft chatDraft = chatDraftEditor.enterChatDraft(chatDraftId, event.getSender().getHomedUser());

        SimpleSymbolTable symbolTable = new SimpleSymbolTable();
        symbolTable.addValue("chatDraft", chatDraft);
        event.say(symbolTable, response);
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitEnterChatDraftCommand(this);
    }
}
