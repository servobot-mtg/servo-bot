package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.AlertGeneratorRow;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.alerts.AlertGenerator;
import com.ryan_mtg.servobot.model.alerts.ContinualGenerator;
import com.ryan_mtg.servobot.model.alerts.DailyGenerator;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;

@Component
public class AlertGeneratorSerializer {
    public AlertGenerator createAlertGenerator(final AlertGeneratorRow alertGeneratorRow) throws BotErrorException {
        switch (alertGeneratorRow.getType()) {
            case DailyGenerator.TYPE:
                LocalTime time = convertToLocalTime(alertGeneratorRow.getTime());
                return new DailyGenerator(alertGeneratorRow.getId(), alertGeneratorRow.getAlertToken(), time);
            case ContinualGenerator.TYPE:
                Duration duration = Duration.ofSeconds(alertGeneratorRow.getTime());
                return new ContinualGenerator(alertGeneratorRow.getId(), alertGeneratorRow.getAlertToken(), duration);
        }
        throw new IllegalArgumentException("Unsupported type: " + alertGeneratorRow.getType());
    }

    private static LocalTime convertToLocalTime(final int time) {
        return LocalTime.of(time / 3600, (time/60) % 60 , time % 60);
    }
}
