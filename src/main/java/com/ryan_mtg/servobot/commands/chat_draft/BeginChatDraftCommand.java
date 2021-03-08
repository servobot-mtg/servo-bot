package com.ryan_mtg.servobot.commands.chat_draft;

import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedHomedCommand;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraft;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraftPack;
import com.ryan_mtg.servobot.model.editors.ChatDraftEditor;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;
import lombok.Setter;

public class BeginChatDraftCommand extends InvokedHomedCommand {
    public static final CommandType TYPE = CommandType.BEGIN_CHAT_DRAFT_COMMAND_TYPE;

    @Getter
    private final String response;

    @Getter @Setter
    private int chatDraftId;

    public BeginChatDraftCommand(final int id, final CommandSettings commandSettings, final int chatDraftId,
            final String response) throws UserError {
        super(id, commandSettings);
        this.response = response;
        this.chatDraftId = chatDraftId;

        Validation.validateStringLength(response, Validation.MAX_TEXT_LENGTH, "Command response");
    }

    @Override
    public void perform(final CommandInvokedHomeEvent event) throws BotHomeError, UserError {
        ChatDraftEditor chatDraftEditor = event.getChatDraftEditor();

        ChatDraft chatDraft = chatDraftEditor.beginChatDraft(chatDraftId);

        SimpleSymbolTable symbolTable = new SimpleSymbolTable();
        symbolTable.addValue("chatDraft", chatDraft);
        event.say(symbolTable, response);

        for (ChatDraftPack chatDraftPack : chatDraft.getPicks().getPacks()) {
            event.say(chatDraftPack.getPackString());
        }
        event.say(symbolTable, chatDraft.getNextCommandSettings().getMessage());
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitBeginChatDraftCommand(this);
    }
}
