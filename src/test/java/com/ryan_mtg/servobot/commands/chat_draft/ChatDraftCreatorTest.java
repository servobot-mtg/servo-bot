package com.ryan_mtg.servobot.commands.chat_draft;

import com.ryan_mtg.servobot.model.chat_draft.ChatDraft;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraftPack;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraftPick;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraftPicks;
import com.ryan_mtg.servobot.model.chat_draft.DraftEntrant;
import com.ryan_mtg.servobot.user.HomedUser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChatDraftCreatorTest {
    @Test
    public void testOneDrafter() {
        ChatDraftCreator chatDraftCreator = new ChatDraftCreator();
        ChatDraftPicks chatDraftPicks = chatDraftCreator.createDraftPicks(createDrafters(1));
        verifyDraftPicks(chatDraftPicks, 1);
    }

    @Test
    public void testFiveDrafters() {
        ChatDraftCreator chatDraftCreator = new ChatDraftCreator();
        ChatDraftPicks chatDraftPicks = chatDraftCreator.createDraftPicks(createDrafters(5));
        verifyDraftPicks(chatDraftPicks, 5);
    }

    @Test
    public void testMultipleDrafters() {
        ChatDraftCreator chatDraftCreator = new ChatDraftCreator();
        for (int drafters = 2; drafters <= 24; drafters++) {
            ChatDraftPicks chatDraftPicks = chatDraftCreator.createDraftPicks(createDrafters(drafters));
            verifyDraftPicks(chatDraftPicks, drafters);
            System.out.println(drafters);
        }
    }

    private List<DraftEntrant> createDrafters(final int count) {
        List<DraftEntrant> draftEntrants = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            DraftEntrant draftEntrant = mock(DraftEntrant.class);
            when(draftEntrant.getId()).thenReturn(i + 1);
            HomedUser user = mock(HomedUser.class);
            when(user.getId()).thenReturn(i + 1);
            when(draftEntrant.getUser()).thenReturn(user);
            draftEntrants.add(draftEntrant);
        }
        return draftEntrants;
    }

    private void verifyDraftPicks(final ChatDraftPicks chatDraftPicks, final int drafters) {
        assertThat(chatDraftPicks.getPacks().size(), is(ChatDraft.PACKS));
        int packNumber = 1;
        Map<Integer, Integer> count = new HashMap<>();
        for (ChatDraftPack pack : chatDraftPicks.getPacks()) {
            assertThat(pack.getPackNumber(), is(packNumber++));
            assertThat(pack.getPicks().size(), is(ChatDraft.PICKS_PER_PACK));
            for (ChatDraftPick pick : pack.getPicks()) {
                int pickerId = pick.getPicker().getId();
                assertThat(pickerId, allOf(greaterThanOrEqualTo(1), lessThanOrEqualTo(drafters)));
                count.put(pickerId, 1 + count.computeIfAbsent(pickerId, id -> 0));
            }
        }

        int minimum = Collections.min(count.values());
        int maximum = Collections.min(count.values());
        assertThat(maximum, is(lessThanOrEqualTo(1 + minimum)));
    }
}