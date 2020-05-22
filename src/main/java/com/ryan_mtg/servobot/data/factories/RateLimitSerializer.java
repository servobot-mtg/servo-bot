package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.CommandRow;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RateLimitSerializer {
    public void saveRateLimit(final CommandRow commandRow, final Duration rateLimitDuration) {
        if (rateLimitDuration == null) {
            commandRow.setRateLimitDuration(null);
        } else {
            commandRow.setRateLimitDuration((int)rateLimitDuration.getSeconds());
        }
    }

    public Duration createRateLimitDuration(final CommandRow commandRow) {
        Integer rateLimitDurationInSeconds = commandRow.getRateLimitDuration();
        if (rateLimitDurationInSeconds == null || rateLimitDurationInSeconds == 0) {
            return null;
        }
        return Duration.ofSeconds(rateLimitDurationInSeconds);
    }
}
