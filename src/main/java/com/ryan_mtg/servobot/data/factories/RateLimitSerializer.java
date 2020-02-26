package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.CommandRow;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RateLimitSerializer {
    public void saveRateLimit(final CommandRow commandRow, final Duration rateLimitDuration) {
        if (rateLimitDuration == null) {
            commandRow.setRateLimitDuration(0);
        } else {
            commandRow.setRateLimitDuration((int)rateLimitDuration.getSeconds());
        }
    }

    public Duration createRateLimitDuration(final CommandRow commandRow) {
        int rateLimitDurationInSeconds = commandRow.getRateLimitDuration();
        return rateLimitDurationInSeconds == 0 ? null : Duration.ofSeconds(rateLimitDurationInSeconds);
    }
}
