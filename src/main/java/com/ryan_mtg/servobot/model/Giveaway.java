package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.user.HomedUser;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Giveaway {
    public static int nextRewardId = 1;
    public static final String DISCORD_CHANNEL = "mooseland";
    public static final String TWITCH_CHANNEL = "themightylinguine";
    public static final Duration DURATION = Duration.of(10, ChronoUnit.MINUTES);

    private List<Reward> rewards = new ArrayList<>();
    private Reward currentReward;

    public List<Reward> getRewards() {
        return rewards;
    }

    public Reward getCurrentReward() {
        return currentReward;
    }

    public void addReward(final Reward reward) {
        rewards.add(reward);
    }

    public Reward startGiveaway() throws BotErrorException {
        if (currentReward != null) {
            throw new BotErrorException("A giveaway is already under way.");
        }

        currentReward = getReadyReward();
        if (currentReward == null) {
            throw new BotErrorException("Nothing to give away!");
        }

        currentReward.setStatus(Reward.Status.IN_PROGRESS);
        currentReward.setStopTime(Instant.now().plus(DURATION));
        return currentReward;
    }

    public void enterGiveaway(final HomedUser homedUser) throws BotErrorException {
        if (currentReward == null) {
            throw new BotErrorException("There is not a giveaway currently.");
        }

        if (currentReward.getStatus() != Reward.Status.IN_PROGRESS) {
            throw new BotErrorException("The giveaway has expired.");
        }

        currentReward.enter(homedUser);
    }

    public void finishGiveaway() {
        if (currentReward != null) {
            currentReward.setStatus(Reward.Status.BESTOWED);
            currentReward = null;
        }
    }

    private Reward getReadyReward() {
        return getRewardInStatus(Reward.Status.READY);
    }

    private Reward getRewardInStatus(final Reward.Status status) {
        return rewards.stream().filter(reward -> reward.getStatus() == status).findFirst().orElse(null);
    }

    public void deleteReward(final int rewardId) {
        if (currentReward.getId() == rewardId) {
            currentReward = null;
        }
        rewards.removeIf(reward -> reward.getId() == rewardId);
    }
}
