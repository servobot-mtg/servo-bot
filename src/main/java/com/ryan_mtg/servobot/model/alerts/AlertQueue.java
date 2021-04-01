package com.ryan_mtg.servobot.model.alerts;

import com.ryan_mtg.servobot.model.Bot;
import com.ryan_mtg.servobot.model.BotHome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class AlertQueue {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlertQueue.class);

    private final Bot bot;
    private final Map<AlertGenerator, RepeatingAlertable> alertableMap = new HashMap<>();
    private final Set<Alertable> alertables = new HashSet<>();
    private final Timer timer = new Timer();
    private boolean active = false;

    public AlertQueue(final Bot bot) {
        this.bot = bot;
    }

    public void scheduleAlert(final BotHome botHome, final Alert alert) {
        if (active) {
            Alertable alertable = new OneShotAlertable(botHome, alert.getDelay(), alert.getToken());
            alertables.add(alertable);
            alertable.schedule(Instant.now());
        }
    }

    public void update(final BotHome home) {
        Instant now = Instant.now();
        for (AlertGenerator alertGenerator : home.getAlertGenerators()) {
            RepeatingAlertable alertable = alertableMap.computeIfAbsent(alertGenerator,
                    ag -> new RepeatingAlertable(home, alertGenerator));
            alertables.add(alertable);
            if (active) {
                alertable.update(now);
            }
        }
    }

    public void add(final BotHome home, final AlertGenerator alertGenerator) {
        RepeatingAlertable alertable = alertableMap.computeIfAbsent(alertGenerator,
                ag -> new RepeatingAlertable(home, alertGenerator));
        alertables.add(alertable);
        if (active) {
            alertable.update(Instant.now());
        }
    }

    public void remove(final BotHome home) {
        List<AlertGenerator> alertGeneratorsToRemove = new ArrayList<>();
        for (Map.Entry<AlertGenerator, RepeatingAlertable> entry : alertableMap.entrySet()) {
            RepeatingAlertable alertable = entry.getValue();
            if (alertable != null && alertable.getHome() == home) {
                alertable.cancel();
                alertables.remove(alertable);
                alertGeneratorsToRemove.add(entry.getKey());
            }
        }
        for (AlertGenerator alertGenerator : alertGeneratorsToRemove) {
            alertableMap.remove(alertGenerator);
        }
    }

    public void start() {
        active = true;
        Instant now = Instant.now();
        alertableMap.values().forEach(alertable -> alertable.schedule(now));
    }

    public void stop() {
        active = false;
        for (Alertable alertable : alertables) {
            alertable.cancel();
        }
        timer.cancel();
    }

    private interface Alertable {
        void alert();
        void schedule(final Instant now);
        void cancel();
    }

    private class OneShotAlertable implements Alertable {
        private final BotHome botHome;
        private final Duration waitTime;
        private final String alertToken;
        private AlertTask alertTask;

        OneShotAlertable(final BotHome botHome, final Duration waitTime, final String alertToken) {
            this.botHome = botHome;
            this.waitTime = waitTime;
            this.alertToken = alertToken;
        }

        @Override
        public void alert() {
            LOGGER.info("Alerting {}", alertToken);
            bot.getHomeEditor(botHome.getId()).alert(alertToken);
            alertables.remove(this);
            alertTask = null;
        }

        @Override
        public void schedule(final Instant now) {
            Instant goal = now.plus(waitTime);

            alertTask = new AlertTask(this, goal);
            timer.schedule(alertTask, waitTime.toMillis());
        }

        @Override
        public void cancel() {
            if (alertTask != null) {
                alertTask.kill();
            }
        }
    }

    private class RepeatingAlertable implements Alertable {
        private final BotHome home;
        private final AlertGenerator generator;
        private AlertTask alertTask;

        RepeatingAlertable(final BotHome home, final AlertGenerator generator) {
            this.home = home;
            this.generator = generator;
        }

        public BotHome getHome() {
            return home;
        }

        @Override
        public void alert() {
            LOGGER.info("Alerting {}", generator.getAlertToken());
            bot.getHomeEditor(home.getId()).alert(generator.getAlertToken());
            schedule(Instant.now());
        }

        void update(final Instant now) {
            if (alertTask == null) {
                schedule(now);
                return;
            }

            Instant goal = generator.getNextAlertTime(now);
            if (!goal.equals(alertTask.getAlertTime())) {
                alertTask.kill();
                schedule(now);
            }
        }

        @Override
        public void schedule(final Instant now) {
            LOGGER.trace("Scheduling {} for {}", generator.getAlertToken(), now);
            Instant goal = generator.getNextAlertTime(now);
            Duration wait = Duration.between(now, goal);
            LOGGER.trace("  - goal is {} with wait {}", goal, wait);

            if (wait.compareTo(Duration.ofSeconds(5)) < 0) {
                Instant later = now.plus(5, ChronoUnit.SECONDS);
                goal = generator.getNextAlertTime(later);
                wait = Duration.between(now, goal);
                LOGGER.trace("  wait was too small, changed: goal is {} with wait {}", goal, wait);
            }

            alertTask = new AlertTask(this, goal);
            timer.schedule(alertTask, wait.toMillis());
        }

        @Override
        public void cancel() {
            if (alertTask != null) {
                alertTask.kill();
            }
        }
    }

    private class AlertTask extends TimerTask {
        private final Alertable alertable;
        private final Instant alertTime;
        private boolean alive = true;

        AlertTask(final Alertable alertable, final Instant alertTime) {
            this.alertable = alertable;
            this.alertTime = alertTime;
        }

        void kill() {
            alive = false;
        }

        Instant getAlertTime() {
            return alertTime;
        }

        @Override
        public void run() {
            if (active && alive) {
                alertable.alert();
            }
        }
    }
}
