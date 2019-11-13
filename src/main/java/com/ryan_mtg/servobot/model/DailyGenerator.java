package com.ryan_mtg.servobot.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DailyGenerator extends AlertGenerator {
    static Logger LOGGER = LoggerFactory.getLogger(DailyGenerator.class);
    public static final int TYPE = 2;

    private ZoneId zoneId;
    private LocalTime time;
    private ZonedDateTime goal;

    public DailyGenerator(final int id, final String alertToken, final LocalTime time, final String timeZone) {
        super(id, alertToken);

        this.zoneId = ZoneId.of(timeZone);
        this.time = time;

        LOGGER.info("Daily alert at time {} for {}",  time, alertToken);
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        LocalDate date = LocalDate.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        goal = ZonedDateTime.of(date, time, zoneId);

        fix(now.toInstant());
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public Instant getNextAlertTime(final Instant now) {
        fix(now);
        return goal.toInstant();
    }

    private void fix(final Instant now) {
        while (goal.toInstant().isBefore(now)) {
            goal = goal.plusDays(1);
        }
    }
}
