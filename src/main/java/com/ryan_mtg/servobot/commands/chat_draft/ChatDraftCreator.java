package com.ryan_mtg.servobot.commands.chat_draft;

import com.ryan_mtg.servobot.model.chat_draft.ChatDraft;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraftPack;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraftPick;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraftPicks;
import com.ryan_mtg.servobot.model.chat_draft.DraftEntrant;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.utility.Flags;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ChatDraftCreator {
    private static final int PACKS = ChatDraft.PACKS;
    private static final int PICKS_PER_PACK = ChatDraft.PICKS_PER_PACK;

    public ChatDraftPicks createDraftPicks(final List<DraftEntrant> entrants) {
        int[] pickOrder = computeSnakeOrder(entrants.size());
        printPickOrder("after snake", pickOrder);
        pickOrder = fixPickOrderForPackVariety(pickOrder);
        printPickOrder("after fix", pickOrder);
        return createDraftPicks(entrants, pickOrder);
    }

    private void printPickOrder(final String message, final int[] pickOrder) {
        System.out.println(String.format("Pick Order(%s): %s", message, Arrays.toString(pickOrder)));
    }

    private int[] computeSnakeOrder(final int entrantCount) {
        int totalPicks = PACKS * PICKS_PER_PACK;
        int picksPerPlayer = totalPicks / entrantCount;

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
            int remainderIndex = i - picksPerPlayer * entrantCount;
            pickOrder[i] = entrantCount - 1 - remainderIndex;
        }

        return pickOrder;
    }

    private int[] fixPickOrderForPackVariety(final int[] pickOrder) {
        int[] packSets = new int[pickOrder.length];
        int[] newPickOrder = new int[pickOrder.length];
        int[] position = new int[PACKS];
        int[] best = new int[PACKS + 1];
        for (int i = 0; i < pickOrder.length; i+=PACKS) {
            System.arraycopy(pickOrder, i, position, 0, PACKS);

            best[PACKS] = Integer.MAX_VALUE;
            minimizeCost(packSets, position[0], position[1], position[2], best);
            minimizeCost(packSets, position[0], position[2], position[1], best);
            minimizeCost(packSets, position[1], position[0], position[2], best);
            minimizeCost(packSets, position[1], position[2], position[0], best);
            minimizeCost(packSets, position[2], position[0], position[1], best);
            minimizeCost(packSets, position[2], position[1], position[0], best);

            System.arraycopy(best, 0, newPickOrder, i, PACKS);
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

    private ChatDraftPicks createDraftPicks(final List<DraftEntrant> entrants, final int[] pickOrder) {
        ChatDraftPicks chatDraftPicks = new ChatDraftPicks();
        ChatDraftPack[] chatDraftPack = new ChatDraftPack[PACKS];
        for (int p = 0; p < chatDraftPack.length; p++) {
            chatDraftPack[p] = new ChatDraftPack();
            chatDraftPicks.addPack(chatDraftPack[p]);
        }

        List<HomedUser> shuffledEntrants = entrants.stream().map(DraftEntrant::getUser).collect(Collectors.toList());
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
}
