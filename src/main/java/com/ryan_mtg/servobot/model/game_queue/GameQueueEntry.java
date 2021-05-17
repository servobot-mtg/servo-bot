package com.ryan_mtg.servobot.model.game_queue;

import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter @Setter
public class GameQueueEntry implements Comparable<GameQueueEntry> {
    private HomedUser user;
    private Instant enqueueTime;
    private PlayerState state;
    private String note;

    public GameQueueEntry(final HomedUser user, final Instant enqueueTime, final PlayerState state, final String note)
            throws UserError {
        this.user = user;
        this.enqueueTime = enqueueTime;
        this.state = state;
        setNote(note);
    }

    @Override
    public int compareTo(final GameQueueEntry o) {
        int result = state.compareTo(o.state);
        if (result != 0) {
            return result;
        }
        result = enqueueTime.compareTo(o.enqueueTime);
        if (result != 0) {
            return  result;
        }
        return user.getId() - o.user.getId();
    }

    public void setNote(final String note) throws UserError {
        Validation.validateStringLength(note, Validation.MAX_TEXT_LENGTH, "note");
        this.note = note;
    }

    public void clearNote() {
        this.note = null;
    }
}