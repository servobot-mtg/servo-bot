package com.ryan_mtg.servobot.model.editors;

import com.ryan_mtg.servobot.commands.CommandTableEdit;
import com.ryan_mtg.servobot.commands.chat.TextCommand;
import com.ryan_mtg.servobot.commands.chat_draft.EnterChatDraftCommand;
import com.ryan_mtg.servobot.commands.chat_draft.OpenChatDraftCommand;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedCommand;
import com.ryan_mtg.servobot.commands.hierarchy.RateLimit;
import com.ryan_mtg.servobot.data.factories.ChatDraftSerializer;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraft;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraftEdit;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraftTable;
import com.ryan_mtg.servobot.model.chat_draft.DraftEntrant;
import com.ryan_mtg.servobot.model.giveaway.GiveawayCommandSettings;
import com.ryan_mtg.servobot.user.HomedUser;
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
        validateCommand(openCommandName);

        GiveawayCommandSettings openSettings = chatDraft.getOpenCommandSettings();
        OpenChatDraftCommand openCommand = new OpenChatDraftCommand(Command.UNREGISTERED_ID,
                createCommandSettings(openSettings), ChatDraft.UNREGISTERED_ID, openSettings.getMessage());
        chatDraft.setOpenCommand(openCommand);

        ChatDraftEdit chatDraftEdit = chatDraftTable.addChatDraft(contextId, chatDraft, (savedChatDraft) -> {
            openCommand.setChatDraftId(savedChatDraft.getId());
            return openCommand;
        });
        chatDraftEdit.merge(commandTableEditor.addCommandEdit(openCommandName, openCommand));

        chatDraftSerializer.commit(chatDraftEdit);
        return chatDraft;
    }

    public ChatDraft openChatDraft(final int chatDraftId) throws UserError {
        ChatDraft chatDraft = chatDraftTable.getChatDraft(chatDraftId);

        GiveawayCommandSettings enterSettings = chatDraft.getEnterCommandSettings();
        String enterCommandName = enterSettings.getCommandName();
        validateCommand(enterCommandName);

        GiveawayCommandSettings statusSettings = chatDraft.getStatusCommandSettings();
        String statusCommandName = statusSettings.getCommandName();
        validateCommand(statusCommandName);

        GiveawayCommandSettings beginSettings = chatDraft.getBeginCommandSettings();
        String beginCommandName = beginSettings.getCommandName();
        validateCommand(beginCommandName);

        EnterChatDraftCommand enterCommand = new EnterChatDraftCommand(Command.UNREGISTERED_ID,
                createCommandSettings(enterSettings), chatDraft.getId(), enterSettings.getMessage());
        chatDraft.setEnterCommand(enterCommand);
        CommandTableEdit commandTableEdit = commandTableEditor.addCommandEdit(enterCommandName, enterCommand);

        InvokedCommand statusCommand = new TextCommand(Command.UNREGISTERED_ID, createCommandSettings(statusSettings),
                statusSettings.getMessage());
        chatDraft.setStatusCommand(statusCommand);
        commandTableEdit.merge(commandTableEditor.addCommandEdit(statusCommandName, statusCommand));

        InvokedCommand beginCommand = new TextCommand(Command.UNREGISTERED_ID, createCommandSettings(beginSettings),
                beginSettings.getMessage());
        chatDraft.setBeginCommand(beginCommand);
        commandTableEdit.merge(commandTableEditor.addCommandEdit(beginCommandName, beginCommand));

        ChatDraftEdit chatDraftEdit = new ChatDraftEdit();
        chatDraftEdit.saveChatDraft(contextId, chatDraft);
        chatDraftEdit.merge(commandTableEdit);

        chatDraftSerializer.commit(chatDraftEdit);
        return chatDraft;
    }

    public ChatDraft enterChatDraft(final int chatDraftId, final HomedUser entrant) throws UserError {
        ChatDraft chatDraft = chatDraftTable.getChatDraft(chatDraftId);

        DraftEntrant draftEntrant = new DraftEntrant(DraftEntrant.UNREGISTERED_ID, entrant);
        ChatDraftEdit chatDraftEdit = chatDraft.addDraftEntrant(draftEntrant);

        chatDraftSerializer.commit(chatDraftEdit);
        return chatDraft;
    }

    private void validateCommand(final String commandName) throws UserError {
        if (commandTableEditor.hasCommand(commandName)) {
            throw new UserError("There is already a '%s' command.", commandName);
        }
    }

    private CommandSettings createCommandSettings(final GiveawayCommandSettings commandSettings) {
        return new CommandSettings(commandSettings.getFlags(), commandSettings.getPermission(), new RateLimit());
    }
}
