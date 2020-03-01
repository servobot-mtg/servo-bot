package com.ryan_mtg.servobot.model.alerts;

import com.ryan_mtg.servobot.events.BotErrorException;
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

    private LocalTime time;
    private String timeZone;
    private ZonedDateTime goal;

    public DailyGenerator(final int id, final String alertToken, final LocalTime time) throws BotErrorException {
        super(id, alertToken);
        this.time = time;
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public String getDescription() {
        return String.format("Daily alert at %s(%s), with next alert at %s", time, timeZone, goal);
    }

    @Override
    public void setTimeZone(final String timeZone) {
        this.timeZone = timeZone;

        ZoneId zoneId = ZoneId.of(timeZone);
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        LocalDate date = LocalDate.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        goal = ZonedDateTime.of(date, time, zoneId);

        fix(now.toInstant());
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
