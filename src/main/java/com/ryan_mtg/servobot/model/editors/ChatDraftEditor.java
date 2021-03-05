package com.ryan_mtg.servobot.model.editors;

import com.ryan_mtg.servobot.commands.chat.TextCommand;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedCommand;
import com.ryan_mtg.servobot.commands.hierarchy.RateLimit;
import com.ryan_mtg.servobot.data.factories.ChatDraftSerializer;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraft;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraftEdit;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraftTable;
import com.ryan_mtg.servobot.model.giveaway.GiveawayCommandSettings;
import com.ryan_mtg.servobot.user.HomedUserTable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatDraftEditor {
    private final int contextId;
    private final ChatDraftTable chatDraftTable;
    private final ChatDraftSerializer chatDraftSerializer;
    private final CommandTableEditor commandTableEditor;
    private final HomedUserTable userTable;

    public ChatDraft addChatDraft() throws UserError {
        ChatDraft chatDraft = new ChatDraft();

        String openCommandName = chatDraft.getOpenCommandSettings().getCommandName();
        if (commandTableEditor.hasCommand(openCommandName)) {
            throw new UserError("There is already a '%s' command.", openCommandName);
        }

        ChatDraftEdit chatDraftEdit = chatDraftTable.addChatDraft(contextId, chatDraft);

        GiveawayCommandSettings openSettings = chatDraft.getOpenCommandSettings();
        InvokedCommand openCommand = new TextCommand(Command.UNREGISTERED_ID, createCommandSettings(openSettings),
                openSettings.getMessage());
        chatDraftEdit.merge(commandTableEditor.addCommandEdit(openCommandName, openCommand));

        chatDraftSerializer.commit(chatDraftEdit);
        return chatDraft;
    }

    private CommandSettings createCommandSettings(final GiveawayCommandSettings commandSettings) {
        return new CommandSettings(commandSettings.getFlags(), commandSettings.getPermission(), new RateLimit());
    }
}
