package com.ryan_mtg.servobot.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotErrorHandler {
    private static Logger LOGGER = LoggerFactory.getLogger(BotErrorHandler.class);

    public interface ThrowingFunction {
        void apply() throws Exception;
    };

    public static void handleError(final ThrowingFunction throwingFunction) {
        try {
            throwingFunction.apply();
        } catch (UserError e) {
            LOGGER.error("Unhandled UserError: {}", e.getMessage());
            e.printStackTrace();
        } catch (BotError e) {
            LOGGER.error("Unhandled BotError: {}", e.getMessage());
            e.printStackTrace();
        } catch (BotHomeError e) {
            LOGGER.error("Unhandled BotHomeError: {}", e.getMessage());
            e.printStackTrace();
        } catch (SystemError e) {
            LOGGER.error("Unhandled SystemError : {}", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.error("Unhandled Exception: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
