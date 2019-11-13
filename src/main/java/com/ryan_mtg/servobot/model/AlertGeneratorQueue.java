package com.ryan_mtg.servobot.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

public class AlertGeneratorQueue {
    static Logger LOGGER = LoggerFactory.getLogger(AlertGeneratorQueue.class);

    private Bot bot;
    private Collection<Alertable> alertables = new HashSet<>();
    private Timer timer = new Timer();

    public AlertGeneratorQueue(final Bot bot) {
        this.bot = bot;
    }

    public void add(final BotHome home, final AlertGenerator alertGenerator) {
        alertables.add(new Alertable(home, alertGenerator));
    }

    public void start() {
        Instant now = Instant.now();
        alertables.stream().forEach(alertable -> schedule(alertable, now));
    }

    public void stop() {
        timer.cancel();
    }

    private void schedule(final Alertable alertable, final Instant now) {
        Duration wait = Duration.between(now, alertable.getNextTime(now));

        if (wait.compareTo(Duration.ofSeconds(5)) < 0) {
            Instant later = now.plus(5, ChronoUnit.SECONDS);
            wait = Duration.between(later, alertable.getNextTime(later));
        }

        timer.schedule(new AlertTask(alertable), wait.toMillis());
    }

    private class Alertable {
        private BotHome home;
        private AlertGenerator generator;

        public Alertable(final BotHome home, final AlertGenerator generator) {
            this.home = home;
            this.generator = generator;
        }

        public void alert() {
            LOGGER.info("Alerting %s ",  generator.getAlertToken());
            bot.alert(home, generator.getAlertToken());
        }

        public Instant getNextTime(final Instant now) {
            Instant result = generator.getNextAlertTime(now);
            return result;
        }
    }

    private class AlertTask extends TimerTask {
        private Alertable alertable;

        public AlertTask(final Alertable alertable) {
            this.alertable = alertable;
        }

        @Override
        public void run() {
            alertable.alert();
            schedule(alertable, Instant.now());
        }
    }
}
