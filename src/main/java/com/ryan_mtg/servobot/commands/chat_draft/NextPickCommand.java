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

public class NextPickCommand extends InvokedHomedCommand {
    public static final CommandType TYPE = CommandType.NEXT_PICK_COMMAND_TYPE;

    @Getter
    private String response;

    @Getter
    private final int chatDraftId;

    public NextPickCommand(final int id, final CommandSettings commandSettings, final int chatDraftId,
            final String response) throws UserError {
        super(id, commandSettings);
        setResponse(response);
        this.chatDraftId = chatDraftId;
    }

    public void setResponse(final String response) throws UserError {
        Validation.validateStringLength(response, Validation.MAX_TEXT_LENGTH, "Command response");
        this.response = response;
    }

    @Override
    public void perform(final CommandInvokedHomeEvent event) throws BotHomeError {
        ChatDraftEditor chatDraftEditor = event.getChatDraftEditor();

        ChatDraft chatDraft = chatDraftEditor.nextPick(chatDraftId);

        if (chatDraft.getPick() == 1) {
            event.say(chatDraft.getCurrentPack().getPackString());
        }

        if (chatDraft.getPick() != 0) {
            SimpleSymbolTable symbolTable = new SimpleSymbolTable();
            symbolTable.addValue("chatDraft", chatDraft);
            event.say(symbolTable, response);
        } else {
            event.say("The chat draft has completed.");
        }
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitNextPickCommand(this);
    }
}