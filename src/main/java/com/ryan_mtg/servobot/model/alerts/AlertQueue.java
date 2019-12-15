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
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AlertQueue {
    static Logger LOGGER = LoggerFactory.getLogger(AlertQueue.class);

    private Bot bot;
    boolean active = false;
    private Map<AlertGenerator, Alertable> alertableMap = new HashMap<>();
    private Timer timer = new Timer();

    public AlertQueue(final Bot bot) {
        this.bot = bot;
    }

    public void update(final BotHome home) {
        Instant now = Instant.now();
        for (AlertGenerator alertGenerator : home.getAlertGenerators()) {
            Alertable alertable = alertableMap.computeIfAbsent(alertGenerator,
                    ag -> new Alertable(home, alertGenerator));
            if (active) {
                alertable.update(now);
            }
        }
    }

    public void remove(final BotHome home) {
        List<AlertGenerator> alertGeneratorsToRemove = new ArrayList<>();
        for (Map.Entry<AlertGenerator, Alertable> entry : alertableMap.entrySet()) {
            Alertable alertable = entry.getValue();
            if (alertable !=  null && alertable.getHome() == home) {
                alertable.cancel();
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
        timer.cancel();
    }

    private class Alertable {
        private BotHome home;
        private AlertGenerator generator;
        private AlertTask alertTask;

        public Alertable(final BotHome home, final AlertGenerator generator) {
            this.home = home;
            this.generator = generator;
        }

        public BotHome getHome() {
            return home;
        }

        public void alert() {
            LOGGER.info("Alerting {}", generator.getAlertToken());
            bot.getHomeEditor(home.getId()).alert(generator.getAlertToken());
            schedule(Instant.now());
        }

        public void update(final Instant now) {
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

        public void schedule(final Instant now) {
            Instant goal = generator.getNextAlertTime(now);
            Duration wait = Duration.between(now, goal);

            if (wait.compareTo(Duration.ofSeconds(5)) < 0) {
                Instant later = now.plus(5, ChronoUnit.SECONDS);
                goal = generator.getNextAlertTime(later);
                wait = Duration.between(later, goal);
            }

            alertTask = new AlertTask(this, goal);
            timer.schedule(alertTask, wait.toMillis());
        }

        public void cancel() {
            if (alertTask != null) {
                alertTask.kill();
            }
        }
    }

    private class AlertTask extends TimerTask {
        private Alertable alertable;
        private Instant alertTime;
        private boolean alive = true;

        public AlertTask(final Alertable alertable, final Instant alertTime) {
            this.alertable = alertable;
            this.alertTime = alertTime;
        }

        public void kill() {
            alive = false;
        }

        public Instant getAlertTime() {
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
