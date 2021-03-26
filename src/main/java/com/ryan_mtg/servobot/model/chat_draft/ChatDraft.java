package com.ryan_mtg.servobot.model.chat_draft;

import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.commands.chat_draft.EnterChatDraftCommand;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.giveaway.GiveawayCommandSettings;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.utility.Flags;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ChatDraft {
    public static final int UNREGISTERED_ID = 0;

    private static final int DEFAULT_COMMAND_FLAGS = Command.TEMPORARY_FLAG | Command.TWITCH_FLAG | Command.DISCORD_FLAG;
    private static final int MINIMUM_ENTRANTS = 1;
    private static final int PICKS_PER_PACK = 8;
    private static final int PACKS = 3;

    public enum State {
        CONFIGURING,
        RECRUITING,
        ACTIVE,
        COMPLETE,
    }

    @Getter @Setter
    private int id;

    @Getter @Setter
    private State state;

    @Getter @Setter
    private int pick;

    @Getter @Setter
    private int pack;

    @Getter @Setter
    private GiveawayCommandSettings openCommandSettings;

    @Getter @Setter
    private GiveawayCommandSettings enterCommandSettings;

    @Getter @Setter
    private GiveawayCommandSettings statusCommandSettings;

    @Getter @Setter
    private GiveawayCommandSettings beginCommandSettings;

    @Getter @Setter
    private GiveawayCommandSettings nextCommandSettings;

    @Getter @Setter
    private GiveawayCommandSettings closeCommandSettings;

    @Getter @Setter
    private Command openCommand;

    @Getter @Setter
    private EnterChatDraftCommand enterCommand;

    @Getter @Setter
    private Command statusCommand;

    @Getter @Setter
    private Command beginCommand;

    @Getter @Setter
    private Command nextCommand;

    @Getter @Setter
    private Command closeCommand;

    @Getter
    private final List<DraftEntrant> entrants;

    @Getter
    private ChatDraftPicks picks;

    public ChatDraft(final int id, final State state, final int pack, final int pick,
            final List<DraftEntrant> entrants, final List<ChatDraftPick> chatDraftPicks) {
        this.id = id;
        this.state = state;
        this.entrants = entrants;
        this.picks = rebuildDraftPicks(chatDraftPicks);
    }

    public ChatDraft() {
        this(UNREGISTERED_ID, State.CONFIGURING, 0, 0, new ArrayList<>(), new ArrayList<>());

        String howToEnter = "To take part in it type !%chatDraft.enter%";
        openCommandSettings = new GiveawayCommandSettings("chatDraft", DEFAULT_COMMAND_FLAGS,
                Permission.STREAMER, String.format("A chat draft is about to start! %s", howToEnter));
        enterCommandSettings = new GiveawayCommandSettings("enter", DEFAULT_COMMAND_FLAGS,
                Permission.ANYONE, "%sender% has joined the chat draft.");
        statusCommandSettings =
                new GiveawayCommandSettings("status", DEFAULT_COMMAND_FLAGS, Permission.ANYONE,
                    String.format("%%chatDraft.entrantCount%% people have joined the chat draft. %s", howToEnter));
        beginCommandSettings = new GiveawayCommandSettings("start", DEFAULT_COMMAND_FLAGS, Permission.MOD,
                "The chat draft is starting!");
        nextCommandSettings = new GiveawayCommandSettings("next", DEFAULT_COMMAND_FLAGS, Permission.MOD,
            "The next pick is pack %chatDraft.pack% pick %chatDraft.pick% and belongs to %chatDraft.currentDrafter%.");
        closeCommandSettings = new GiveawayCommandSettings("end", DEFAULT_COMMAND_FLAGS,
                Permission.STREAMER, "The chat draft has ended!");
    }

    public String getEnter() {
        return enterCommandSettings.getCommandName();
    }

    public int getEntrantCount() {
        return entrants.size();
    }

    public String getCurrentDrafter() {
        if (state == State.ACTIVE) {
            return picks.getPicker(pack, pick).getName();
        }
        return null;
    }

    public ChatDraftPack getCurrentPack() {
        if (state == State.ACTIVE && 1 <= pack && pack <= picks.getPacks().size()) {
            return picks.getPacks().get(pack - 1);
        }
        return null;
    }

    public ChatDraftEdit addDraftEntrant(final DraftEntrant draftEntrant) throws UserError {
        if (entrants.stream().anyMatch(entrant -> entrant.getUser().getId() == draftEntrant.getUser().getId())) {
            throw new UserError("%s has already entered!", draftEntrant.getUser().getName());
        }

        entrants.add(draftEntrant);
        ChatDraftEdit chatDraftEdit = new ChatDraftEdit();
        chatDraftEdit.saveDraftEntrant(id, draftEntrant);
        return chatDraftEdit;
    }

    public ChatDraftEdit deletePicks() {
        ChatDraftEdit chatDraftEdit = new ChatDraftEdit();
        if (picks != null) {
            for (ChatDraftPack pack : picks.getPacks()) {
                chatDraftEdit.deleteChatDraftPicks(pack.getPicks());
            }
        }
        return chatDraftEdit;
    }

    public ChatDraftEdit beginDraft(final int botHomeId) throws UserError {
        if (state != State.RECRUITING) {
            throw new SystemError("Not in a proper state for beginning a chat draft: %s. ", state);
        }

        if (picks != null) {
            throw new SystemError("Picks should be null!");
        }

        if (entrants.size() < MINIMUM_ENTRANTS) {
            throw new UserError("There are not enough entrants to begin the chat draft (%d/%d).", entrants.size(),
                    MINIMUM_ENTRANTS);
        }

        ChatDraftEdit chatDraftEdit = new ChatDraftEdit();

        setState(State.ACTIVE);
        pick = 1;
        pack = 1;
        chatDraftEdit.saveChatDraft(botHomeId, this);

        picks = createDraftPicks(entrants);
        saveDraftPicks(picks, chatDraftEdit);

        return chatDraftEdit;
    }

    public ChatDraftEdit nextPick(final int botHomeId) {
        if (state != State.ACTIVE) {
            throw new SystemError("Chat drafts can only advance the pick while active.");
        }

        pick = pick + 1;
        if (pick > PICKS_PER_PACK) {
            pick = 1;
            pack = pack + 1;
            if (pack > PACKS) {
                pick = pack = 0;
                state = State.COMPLETE;
            }
        }

        ChatDraftEdit chatDraftEdit = new ChatDraftEdit();
        chatDraftEdit.saveChatDraft(botHomeId, this);
        return chatDraftEdit;
    }

    private ChatDraftPicks rebuildDraftPicks(final List<ChatDraftPick> chatDraftPicks) {
        if (chatDraftPicks.isEmpty()) {
            return null;
        }

        Collections.sort(chatDraftPicks, (pick, otherPick) -> {
            if (pick.getPack() != otherPick.getPack()) {
                return pick.getPack() - otherPick.getPack();
            }

            if (pick.getPick() != otherPick.getPick()) {
                return pick.getPick() - otherPick.getPick();
            }

            return pick.getId() - otherPick.getId();
        });

        ChatDraftPicks picks = new ChatDraftPicks();

        ChatDraftPack pack = null;
        for (ChatDraftPick pick : chatDraftPicks) {
            if (pack == null || pack.getPackNumber() != pick.getPack()) {
                if (pack != null) {
                    picks.addPack(pack);
                }
                pack = new ChatDraftPack();
            }
            pack.addPick(pick);
        }
        picks.addPack(pack);

        return picks;
    }

    private ChatDraftPicks createDraftPicks(final List<DraftEntrant> entrants) {
        int[] pickOrder = computeSnakeOrder(entrants.size());
        pickOrder = fixPickOrderForPackVariety(pickOrder);
        return createDraftPicks(entrants, pickOrder);
    }

    private ChatDraftPicks createDraftPicks(final List<DraftEntrant> entrants, final int[] pickOrder) {
        ChatDraftPicks chatDraftPicks = new ChatDraftPicks();
        ChatDraftPack[] chatDraftPack = new ChatDraftPack[PACKS];
        for (int p = 0; p < chatDraftPack.length; p++) {
            chatDraftPack[p] = new ChatDraftPack();
            chatDraftPicks.addPack(chatDraftPack[p]);
        }

        List<HomedUser> shuffledEntrants = entrants.stream()
                .map(entrant -> entrant.getUser()).collect(Collectors.toList());
        Collections.shuffle(shuffledEntrants);

        for (int i = 0; i < pickOrder.length; i++) {
            int pack = i % PACKS + 1;
            int pick = i / PACKS + 1;
            HomedUser picker = shuffledEntrants.get(pickOrder[i]);
            ChatDraftPick chatDraftPick = new ChatDraftPick(ChatDraftPick.UNREGISTERED_ID, pack, pick, picker);
            chatDraftPack[i % PACKS].addPick(chatDraftPick);
        }

        return chatDraftPicks;
    }

    private int[] computeSnakeOrder(final int entrantCount) {
        int totalPicks = PACKS * PICKS_PER_PACK;
        int picksPerPlayer = totalPicks / entrantCount;
        int remainderPicks = entrantCount - (totalPicks % entrantCount);

        int[] pickOrder = new int[totalPicks];

        int position = 0;
        int velocity = 1;
        for (int i = 0; i < picksPerPlayer * entrantCount; i++) {
            pickOrder[i] = position;
            position += velocity;

            if (position < 0) {
                position = 0;
                velocity = 1;
            } else if (position >= entrantCount) {
                position = entrantCount - 1;
                velocity = -1;
            }
        }

        for (int i = picksPerPlayer * entrantCount; i < totalPicks; i++) {
            int index = i - picksPerPlayer * entrantCount;
            pickOrder[i] = entrantCount - remainderPicks + index;
        }

        return pickOrder;
    }

    private int[] fixPickOrderForPackVariety(final int[] pickOrder) {
        int[] packSets = new int[pickOrder.length];
        int[] newPickOrder = new int[pickOrder.length];
        int[] position = new int[PACKS], best = new int[PACKS + 1];
        for (int i = 0; i < pickOrder.length; i+=PACKS) {
            for (int j = 0; j < PACKS; j++) {
                position[j] = pickOrder[i + j];
            }

            best[PACKS] = Integer.MAX_VALUE;
            minimizeCost(packSets, position[0], position[1], position[2], best);
            minimizeCost(packSets, position[0], position[2], position[1], best);
            minimizeCost(packSets, position[1], position[0], position[2], best);
            minimizeCost(packSets, position[1], position[2], position[0], best);
            minimizeCost(packSets, position[2], position[0], position[1], best);
            minimizeCost(packSets, position[2], position[1], position[0], best);

            for (int j = 0; j < PACKS; j++) {
                newPickOrder[i + j] = best[j];
            }
        }

        return newPickOrder;
    }

    private void minimizeCost(final int[] packSets, final int a, final int b, final int c, final int[] best) {
        int cost = 0;
        if (Flags.hasFlag(packSets[a], 1 << 0)) {
            cost++;
        }
        if (Flags.hasFlag(packSets[b], 1 << 1)) {
            cost++;
        }
        if (Flags.hasFlag(packSets[c], 1 << 2)) {
            cost++;
        }

        if (cost < best[PACKS]) {
            best[PACKS] = cost;
            best[0] = a;
            best[1] = b;
            best[2] = c;
        }
    }

    private void saveDraftPicks(final ChatDraftPicks picks, final ChatDraftEdit chatDraftEdit) {
        for (ChatDraftPack chatDraftPack : picks.getPacks()) {
            for (ChatDraftPick chatDraftPick : chatDraftPack.getPicks()) {
                chatDraftEdit.saveChatDraftPick(id, chatDraftPick);
            }
        }
    }
}
