package com.ryan_mtg.servobot.model.giveaway;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.Entrant;
import com.ryan_mtg.servobot.user.HomedUser;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Reward {
    public static final int UNREGISTERED_ID = 0;
    private static final Random RANDOM = new Random();

    public enum Status {
        READY,
        IN_PROGRESS,
        CONCLUDED,
        AWARDED,
        BESTOWED,
    }

    private int id;
    private Status status;
    private String prize;
    private HomedUser winner;
    private Instant stopTime;
    private List<Entrant> entrants = new ArrayList<>();

    public Reward(final int id, final String prize) {
        this.id = id;
        this.prize = prize;
        this.status = Status.READY;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    public String getPrize() {
        return prize;
    }

    public HomedUser getWinner() {
        return winner;
    }

    public Duration getTimeLeft() {
        if (stopTime == null) {
            return null;
        }

        Duration result = Duration.between(Instant.now(), stopTime);
        if (result.compareTo(Duration.ofSeconds(0)) < 0) {
            return Duration.ofSeconds(0);
        }
        return result;
    }

    public void setStopTime(final Instant stopTime) {
        this.stopTime = stopTime;
    }

    public List<Entrant> getEntrants() {
        return entrants;
    }

    public void enter(final HomedUser user) throws BotErrorException {
        Iterator<Entrant> entrantIterator = entrants.iterator();
        while (entrantIterator.hasNext()) {
            Entrant entrant = entrantIterator.next();
            if (entrant.getUser().getId() == user.getId()) {
                throw new BotErrorException(user.getName() + " has already entered.");
            }
        }

        entrants.add(new Entrant(user));
    }

    public void award() throws BotErrorException {
        if (status != Status.CONCLUDED && status != Status.AWARDED) {
            throw new BotErrorException("Cannot award when in status " + status);
        }

        if (entrants.size() == 0) {
            throw new BotErrorException("Cannot award giveaway with no entrants!");
        }

        winner = entrants.get(RANDOM.nextInt(entrants.size())).getUser();

        status = Status.AWARDED;
    }
}
