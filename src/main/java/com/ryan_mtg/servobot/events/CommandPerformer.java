package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.commands.RateLimiter;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.HomeCommand;
import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.commands.hierarchy.UserCommand;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ryan_mtg.servobot.user.User.UNREGISTERED_ID;

public class CommandPerformer {
    private static Logger LOGGER = LoggerFactory.getLogger(CommandPerformer.class);
    private RateLimiter rateLimiter;

    public CommandPerformer(final RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    public void perform(final UserEvent userEvent, final UserCommand command) {
        try {
            if (shouldPerform(userEvent.getUser().getHomedUser().getId(), command, userEvent)) {
                LOGGER.info("Performing {} for {}", command.getId(), userEvent.getHome().getName());
                command.perform(userEvent.getHome(), userEvent.getUser());
            }
        } catch (BotErrorException e) {
            LOGGER.error(e.getErrorMessage(), e);
        }
    }

    public void perform(final HomeEvent homeEvent, final HomeCommand command) {
        try {
            if (shouldPerform(UNREGISTERED_ID, command, homeEvent)) {
                LOGGER.info("Performing command {} for {}", command.getId(), homeEvent.getHome().getName());
                command.perform(homeEvent);
            }
        } catch (BotErrorException e) {
            LOGGER.error(e.getErrorMessage(), e);
            e.printStackTrace();
        }
    }

    public void perform(final String commandString, final String arguments, final MessageSentEvent messageSentEvent,
            final MessageCommand command) {
        try {
            User sender = messageSentEvent.getSender();
            LOGGER.info("Performing " + commandString + " for " + sender.getName() + " with arguments " + arguments);

            if (shouldPerform(sender.getHomedUser().getId(), command, messageSentEvent)) {
                if (command.hasPermissions(sender)) {
                    command.perform(messageSentEvent, arguments);
                } else {
                    messageSentEvent.getMessage().getChannel().say(
                            String.format("%s is not allowed to %s.", sender.getName(), commandString));
                }
            }
        } catch (BotErrorException e) {
            messageSentEvent.getMessage().getChannel().say(e.getErrorMessage());
        }
    }

    private boolean shouldPerform(final int userId, final Command command, final HomeEvent homeEvent) {
        return (!command.isOnlyWhileStreaming() || homeEvent.getHome().isStreaming())
                && (homeEvent.getServiceType() == Service.NO_SERVICE_TYPE || command.getService(homeEvent.getServiceType()))
                && rateLimiter.allow(userId, command.getId(), command.getRateLimit());
    }
}
