package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.commands.hierarchy.RateLimit;
import com.ryan_mtg.servobot.data.models.CommandRow;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RateLimitSerializer {
    public void saveRateLimit(final CommandRow commandRow, final RateLimit rateLimit) {
        if (rateLimit.getGlobalDuration() == null) {
            commandRow.setRateLimitDuration(null);
        } else {
            commandRow.setRateLimitDuration((int)rateLimit.getGlobalDuration().getSeconds());
        }
    }

    public RateLimit createRateLimit(final CommandRow commandRow) {
        RateLimit rateLimit = new RateLimit();

        Integer rateLimitDurationInSeconds = commandRow.getRateLimitDuration();
        if (rateLimitDurationInSeconds == null || rateLimitDurationInSeconds == 0) {
            return new RateLimit();
        } else {
            rateLimit.setGlobalDuration(Duration.ofSeconds(rateLimitDurationInSeconds));
        }

        return rateLimit;
    }
}
