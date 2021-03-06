package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.CommandTableEdit;
import com.ryan_mtg.servobot.commands.chat_draft.EnterChatDraftCommand;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.data.models.ChatDraftRow;
import com.ryan_mtg.servobot.data.models.DraftEntrantRow;
import com.ryan_mtg.servobot.data.repositories.ChatDraftRepository;
import com.ryan_mtg.servobot.data.repositories.DraftEntrantRepository;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraft;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraftEdit;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraftTable;
import com.ryan_mtg.servobot.model.chat_draft.DraftEntrant;
import com.ryan_mtg.servobot.model.giveaway.GiveawayCommandSettings;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.user.HomedUserTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ChatDraftSerializer {
    private final ChatDraftRepository chatDraftRepository;
    private final DraftEntrantRepository draftEntrantRepository;
    private final CommandTableSerializer commandTableSerializer;

    @Transactional(rollbackOn = Exception.class)
    public void commit(final ChatDraftEdit chatDraftEdit) {
        commandTableSerializer.commit(chatDraftEdit.getCommandTableEdit());

        chatDraftEdit.getSavedDraftEntrants()
                .forEach((draftEntrant, chatDraftId) -> saveDraftEntrant(chatDraftId, draftEntrant));

        CommandTableEdit commandTableEdit = new CommandTableEdit();
        chatDraftEdit.getSavedChatDrafts().forEach((chatDraft, botHomeId) -> {
            saveChatDraft(botHomeId, chatDraft);
            Function<ChatDraft, Command> saveCallback = chatDraftEdit.getChatDraftSaveCallbackMap().get(chatDraft);
            if (saveCallback != null) {
                Command command = saveCallback.apply(chatDraft);
                commandTableEdit.save(botHomeId, command);
            }
        });

        commandTableSerializer.commit(commandTableEdit);
    }

    public void saveChatDraft(final int botHomeId, final ChatDraft chatDraft) {
        ChatDraftRow chatDraftRow = new ChatDraftRow();
        chatDraftRow.setId(chatDraft.getId());
        chatDraftRow.setBotHomeId(botHomeId);
        chatDraftRow.setState(chatDraft.getState());

        chatDraftRow.setOpenCommandName(chatDraft.getOpenCommandSettings().getCommandName());
        chatDraftRow.setOpenPermission(chatDraft.getOpenCommandSettings().getPermission());
        chatDraftRow.setOpenFlags(chatDraft.getOpenCommandSettings().getFlags());
        chatDraftRow.setOpenMessage(chatDraft.getOpenCommandSettings().getMessage());
        chatDraftRow.setOpenCommandId(chatDraft.getOpenCommand() == null ? 0 : chatDraft.getOpenCommand().getId());

        chatDraftRow.setEnterCommandName(chatDraft.getEnterCommandSettings().getCommandName());
        chatDraftRow.setEnterPermission(chatDraft.getEnterCommandSettings().getPermission());
        chatDraftRow.setEnterFlags(chatDraft.getEnterCommandSettings().getFlags());
        chatDraftRow.setEnterMessage(chatDraft.getEnterCommandSettings().getMessage());
        chatDraftRow.setEnterCommandId(chatDraft.getEnterCommand() == null ? 0 : chatDraft.getEnterCommand().getId());

        chatDraftRow.setStatusCommandName(chatDraft.getStatusCommandSettings().getCommandName());
        chatDraftRow.setStatusPermission(chatDraft.getStatusCommandSettings().getPermission());
        chatDraftRow.setStatusFlags(chatDraft.getStatusCommandSettings().getFlags());
        chatDraftRow.setStatusMessage(chatDraft.getStatusCommandSettings().getMessage());
        chatDraftRow.setStatusCommandId(chatDraft.getStatusCommand() == null ? 0 :
                chatDraft.getStatusCommand().getId());

        chatDraftRow.setBeginCommandName(chatDraft.getBeginCommandSettings().getCommandName());
        chatDraftRow.setBeginPermission(chatDraft.getBeginCommandSettings().getPermission());
        chatDraftRow.setBeginFlags(chatDraft.getBeginCommandSettings().getFlags());
        chatDraftRow.setBeginMessage(chatDraft.getBeginCommandSettings().getMessage());
        chatDraftRow.setBeginCommandId(chatDraft.getBeginCommand() == null ? 0 : chatDraft.getBeginCommand().getId());

        chatDraftRow.setNextCommandName(chatDraft.getNextCommandSettings().getCommandName());
        chatDraftRow.setNextPermission(chatDraft.getNextCommandSettings().getPermission());
        chatDraftRow.setNextFlags(chatDraft.getNextCommandSettings().getFlags());
        chatDraftRow.setNextMessage(chatDraft.getNextCommandSettings().getMessage());
        chatDraftRow.setNextCommandId(chatDraft.getNextCommand() == null ? 0 : chatDraft.getNextCommand().getId());

        chatDraftRow.setCloseCommandName(chatDraft.getCloseCommandSettings().getCommandName());
        chatDraftRow.setClosePermission(chatDraft.getCloseCommandSettings().getPermission());
        chatDraftRow.setCloseFlags(chatDraft.getCloseCommandSettings().getFlags());
        chatDraftRow.setCloseMessage(chatDraft.getCloseCommandSettings().getMessage());
        chatDraftRow.setCloseCommandId(chatDraft.getCloseCommand() == null ? 0 : chatDraft.getCloseCommand().getId());

        chatDraftRepository.save(chatDraftRow);

        chatDraft.setId(chatDraftRow.getId());
    }

    private void saveDraftEntrant(final int chatDraftId, final DraftEntrant draftEntrant) {
        DraftEntrantRow draftEntrantRow = new DraftEntrantRow();
        draftEntrantRow.setId(draftEntrant.getId());
        draftEntrantRow.setChatDraftId(chatDraftId);
        draftEntrantRow.setUserId(draftEntrant.getUser().getId());

        draftEntrantRepository.save(draftEntrantRow);

        draftEntrant.setId(draftEntrantRow.getId());
    }

    public ChatDraftTable createChatDraftTable(final int botHomeId, final HomedUserTable homedUserTable,
            final CommandTable commandTable) {

        Iterable<ChatDraftRow> chatDraftRows = chatDraftRepository.findAllByBotHomeId(botHomeId);

        Iterable<Integer> chatDraftIds = SerializationSupport.getIds(chatDraftRows, ChatDraftRow::getId);

        Map<Integer, List<DraftEntrantRow>> draftEntrantRowMap =
                SerializationSupport.getIdMapping(draftEntrantRepository.findAllByChatDraftIdIn(chatDraftIds),
                        chatDraftIds, DraftEntrantRow::getChatDraftId);

        Set<Integer> userIds = new HashSet<>();
        draftEntrantRowMap.forEach((giveawayId, draftEntrantRows) -> draftEntrantRows.forEach(draftEntrantRow -> {
            userIds.add(draftEntrantRow.getUserId());
        }));

        Map<Integer, HomedUser> homedUserMap = new HashMap<>();
        homedUserTable.getHomedUsers(userIds).forEach(homedUser -> homedUserMap.put(homedUser.getId(), homedUser));

        Map<Integer, List<DraftEntrant>> draftEntrantsMap = createDraftEntrants(draftEntrantRowMap, homedUserMap);

        ChatDraftTable chatDraftTable = new ChatDraftTable();
        for (ChatDraftRow chatDraftRow : chatDraftRows) {
            chatDraftTable.add(createChatDraft(chatDraftRow, commandTable, draftEntrantsMap));
        }

        return chatDraftTable;
    }

    private Map<Integer, List<DraftEntrant>> createDraftEntrants(
            final Map<Integer, List<DraftEntrantRow>> draftEntrantRowMap, final Map<Integer, HomedUser> homedUserMap) {
        Map<Integer, List<DraftEntrant>> draftEntrantsMap = new HashMap<>();
        for(Map.Entry<Integer, List<DraftEntrantRow>> entry : draftEntrantRowMap.entrySet()) {
            List<DraftEntrant> draftEntrants = new ArrayList<>();
            for (DraftEntrantRow draftEntrantRow : entry.getValue()) {
                draftEntrants.add(createDraftEntrant(draftEntrantRow, homedUserMap));
            }
            draftEntrantsMap.put(entry.getKey(), draftEntrants);
        }

        return draftEntrantsMap;
    }

    private DraftEntrant createDraftEntrant(final DraftEntrantRow draftEntrantRow,
            final Map<Integer, HomedUser> homedUserMap) {
        return new DraftEntrant(draftEntrantRow.getId(), homedUserMap.get(draftEntrantRow.getUserId()));
    }

    private ChatDraft createChatDraft(final ChatDraftRow chatDraftRow, final CommandTable commandTable,
            final Map<Integer, List<DraftEntrant>> draftEntrantsMap) {
        return SystemError.filter(() -> {
            List<DraftEntrant> draftEntrants = draftEntrantsMap.get(chatDraftRow.getId());
            if (draftEntrants == null) {
                draftEntrants = new ArrayList<>();
            }

            ChatDraft chatDraft = new ChatDraft(chatDraftRow.getId(), chatDraftRow.getState(), draftEntrants);

            chatDraft.setOpenCommandSettings(new GiveawayCommandSettings(chatDraftRow.getOpenCommandName(),
                    chatDraftRow.getOpenFlags(), chatDraftRow.getOpenPermission(), chatDraftRow.getOpenMessage()));

            if (chatDraftRow.getOpenCommandId() != Command.UNREGISTERED_ID) {
                Command openCommand = commandTable.getCommand(chatDraftRow.getOpenCommandId());
                chatDraft.setOpenCommand(openCommand);
            }

            chatDraft.setEnterCommandSettings(new GiveawayCommandSettings(chatDraftRow.getEnterCommandName(),
                    chatDraftRow.getEnterFlags(), chatDraftRow.getEnterPermission(), chatDraftRow.getEnterMessage()));

            if (chatDraftRow.getEnterCommandId() != Command.UNREGISTERED_ID) {
                EnterChatDraftCommand enterCommand =
                        (EnterChatDraftCommand) commandTable.getCommand(chatDraftRow.getEnterCommandId());
                chatDraft.setEnterCommand(enterCommand);
            }

            chatDraft.setStatusCommandSettings(new GiveawayCommandSettings(chatDraftRow.getStatusCommandName(),
                    chatDraftRow.getStatusFlags(), chatDraftRow.getStatusPermission(), chatDraftRow.getStatusMessage()));

            if (chatDraftRow.getStatusCommandId() != Command.UNREGISTERED_ID) {
                Command statusCommand = commandTable.getCommand(chatDraftRow.getStatusCommandId());
                chatDraft.setStatusCommand(statusCommand);
            }

            chatDraft.setBeginCommandSettings(new GiveawayCommandSettings(chatDraftRow.getBeginCommandName(),
                    chatDraftRow.getBeginFlags(), chatDraftRow.getBeginPermission(), chatDraftRow.getBeginMessage()));

            if (chatDraftRow.getBeginCommandId() != Command.UNREGISTERED_ID) {
                Command beginCommand = commandTable.getCommand(chatDraftRow.getBeginCommandId());
                chatDraft.setBeginCommand(beginCommand);
            }

            chatDraft.setNextCommandSettings(new GiveawayCommandSettings(chatDraftRow.getNextCommandName(),
                    chatDraftRow.getNextFlags(), chatDraftRow.getNextPermission(), chatDraftRow.getNextMessage()));

            if (chatDraftRow.getNextCommandId() != Command.UNREGISTERED_ID) {
                Command nextCommand = commandTable.getCommand(chatDraftRow.getNextCommandId());
                chatDraft.setNextCommand(nextCommand);
            }

            chatDraft.setCloseCommandSettings(new GiveawayCommandSettings(chatDraftRow.getCloseCommandName(),
                    chatDraftRow.getCloseFlags(), chatDraftRow.getClosePermission(), chatDraftRow.getCloseMessage()));

            if (chatDraftRow.getCloseCommandId() != Command.UNREGISTERED_ID) {
                Command closeCommand = commandTable.getCommand(chatDraftRow.getCloseCommandId());
                chatDraft.setCloseCommand(closeCommand);
            }

            return chatDraft;
        });
    }
}