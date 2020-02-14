package com.ryan_mtg.servobot.model.giveaway;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.alerts.Alert;
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

    private int id;
    private List<Reward> rewards = new ArrayList<>();
    private Reward currentReward;
    private String enterCommandString;
    private Duration duration = Duration.of(10, ChronoUnit.MINUTES);

    public int getId() {
        return id;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public Reward getCurrentReward() {
        return currentReward;
    }

    public void addReward(final Reward reward) {
        rewards.add(reward);
    }

    public StartGiveawayResult startGiveaway() throws BotErrorException {
        if (currentReward != null) {
            throw new BotErrorException("A giveaway is already under way.");
        }

        currentReward = getReadyReward();
        if (currentReward == null) {
            throw new BotErrorException("Nothing to give away!");
        }

        currentReward.setStatus(Reward.Status.IN_PROGRESS);
        currentReward.setStopTime(Instant.now().plus(duration));

        List<Alert> alerts = new ArrayList<>();

        if (duration.toMinutes() > 5) {
            alerts.add(new Alert(duration.minus(5, ChronoUnit.MINUTES), "5min"));
        }

        if (duration.toMinutes() > 1) {
            alerts.add(new Alert(duration.minus(1, ChronoUnit.MINUTES), "1min"));
        }

        alerts.add(new Alert(duration, "winner"));
        return new StartGiveawayResult(alerts, currentReward);
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
