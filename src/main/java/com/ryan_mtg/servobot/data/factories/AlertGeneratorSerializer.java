package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.AlertGeneratorRow;
import com.ryan_mtg.servobot.data.repositories.AlertGeneratorRepository;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.model.alerts.AlertGenerator;
import com.ryan_mtg.servobot.model.alerts.ContinualGenerator;
import com.ryan_mtg.servobot.model.alerts.DailyGenerator;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;

@Component
public class AlertGeneratorSerializer {
    private final AlertGeneratorRepository alertGeneratorRepository;

    public AlertGeneratorSerializer(final AlertGeneratorRepository alertGeneratorRepository) {
        this.alertGeneratorRepository = alertGeneratorRepository;
    }

    public AlertGenerator createAlertGenerator(final AlertGeneratorRow alertGeneratorRow) {
        return SystemError.filter(() -> {
            switch (alertGeneratorRow.getType()) {
                case DailyGenerator.TYPE:
                    LocalTime time = LocalTime.ofSecondOfDay(alertGeneratorRow.getTime());
                    return new DailyGenerator(alertGeneratorRow.getId(), alertGeneratorRow.getAlertToken(), time);
                case ContinualGenerator.TYPE:
                    Duration duration = Duration.ofSeconds(alertGeneratorRow.getTime());
                    return new ContinualGenerator(alertGeneratorRow.getId(), alertGeneratorRow.getAlertToken(), duration);
            }
            throw new SystemError("Unsupported type: %s", alertGeneratorRow.getType());
        });
    }

    public void saveAlertGenerator(final int botHomeId, final AlertGenerator alertGenerator) {
        AlertGeneratorRow alertGeneratorRow = new AlertGeneratorRow();
        alertGeneratorRow.setId(alertGenerator.getId());
        alertGeneratorRow.setBotHomeId(botHomeId);
        alertGeneratorRow.setType(alertGenerator.getType());
        alertGeneratorRow.setAlertToken(alertGenerator.getAlertToken());
        switch (alertGenerator.getType()) {
            case DailyGenerator.TYPE:
                alertGeneratorRow.setTime(((DailyGenerator)alertGenerator).getTime().toSecondOfDay());
                break;
            case ContinualGenerator.TYPE:
                alertGeneratorRow.setTime((int)((ContinualGenerator)alertGenerator).getDuration().getSeconds());
                break;
        }
        alertGeneratorRepository.save(alertGeneratorRow);
        alertGenerator.setId(alertGeneratorRow.getId());
    }
}
