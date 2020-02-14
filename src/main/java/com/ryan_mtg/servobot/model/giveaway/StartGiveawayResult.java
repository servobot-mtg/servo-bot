package com.ryan_mtg.servobot.model.giveaway;

import com.ryan_mtg.servobot.model.alerts.Alert;

import java.util.List;

public class StartGiveawayResult {
    private List<Alert> alerts;
    private Reward reward;

    public StartGiveawayResult(final List<Alert> alerts, final Reward reward) {
        this.alerts = alerts;
        this.reward = reward;
    }

    public List<Alert> getAlerts() {
        return alerts;
    }

    public Reward getReward() {
        return reward;
    }
}
