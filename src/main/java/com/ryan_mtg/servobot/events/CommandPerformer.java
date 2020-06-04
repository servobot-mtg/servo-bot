package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.commands.RateLimiter;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.HomeCommand;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedCommand;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedHomedCommand;
import com.ryan_mtg.servobot.commands.hierarchy.MessagedHomeCommand;
import com.ryan_mtg.servobot.commands.hierarchy.UserCommand;
import com.ryan_mtg.servobot.commands.hierarchy.UserHomedCommand;
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

    public void perform(final UserHomeEvent userEvent, final UserHomedCommand command) {
        wrapForErrorHandling(() -> {
            if (shouldPerform(userEvent.getUser().getHomedUser().getId(), command, userEvent)) {
                LOGGER.info("Performing {} at {}", command.getId(), userEvent.getHome().getName());
                command.perform(userEvent);
            }
        });
    }

    public void perform(final HomeEvent homeEvent, final HomeCommand command) {
        wrapForErrorHandling(() -> {
            if (shouldPerform(UNREGISTERED_ID, command, homeEvent)) {
                LOGGER.info("Performing command {} for {}", command.getId(), homeEvent.getHome().getName());
                command.perform(homeEvent);
            }
        });
    }

    public void perform(final CommandInvokedHomeEvent event, final Command command) {
        wrapForInvokedErrorHandling(event, command, () -> {
            dynamicPerform(event, command);
        });
    }

    public void perform(final CommandInvokedEvent event, final Command command) {
        wrapForInvokedErrorHandling(event, command, () -> {
            dynamicPerform(event, command);
        });
    }

    public void perform(final MessageHomeEvent messageHomeEvent, final Command command) {
        wrapForErrorHandling(() -> {
            if (shouldPerform(UNREGISTERED_ID, command, messageHomeEvent)) {
                LOGGER.info("Performing command {} for {}", command.getId(), messageHomeEvent.getHome().getName());
                dynamicPerform(messageHomeEvent, command);
            }
        });
    }

    private void dynamicPerform(final MessageHomeEvent event, final Command command) throws BotErrorException {
        if (command instanceof MessagedHomeCommand) {
            ((MessagedHomeCommand) command).perform(event);
        }else if (command instanceof HomeCommand) {
            ((HomeCommand) command).perform(event);
        } else if (command instanceof UserCommand) {
            ((UserCommand) command).perform(event);
        } else {
            throw new BotErrorException(
                    String.format("Command type %s cannot be applied to a MessageHomeEvent.", command.getClass()));
        }
    }

    private void dynamicPerform(final CommandInvokedHomeEvent event, final Command command) throws BotErrorException {
        if (command instanceof InvokedCommand) {
            ((InvokedCommand) command).perform(event);
        } else if (command instanceof HomeCommand) {
            ((HomeCommand) command).perform(event);
        }else if (command instanceof MessagedHomeCommand) {
            ((MessagedHomeCommand) command).perform(event);
        } else if (command instanceof UserCommand) {
            ((UserCommand) command).perform(event);
        } else if (command instanceof InvokedHomedCommand) {
            ((InvokedHomedCommand) command).perform(event);
        } else {
            throw new BotErrorException(
                    String.format("Command type %s cannot be applied to a CommandInvokedEven.", command.getClass()));
        }
    }

    private void dynamicPerform(final CommandInvokedEvent event, final Command command) throws BotErrorException {
        if (command instanceof InvokedCommand) {
            ((InvokedCommand) command).perform(event);
        } else if (command instanceof HomeCommand) {
            throw new BotErrorException(event.getCommand() + " can only be performed for a home.");
        }else if (command instanceof MessagedHomeCommand) {
            throw new BotErrorException(event.getCommand() + " can only be performed for a home.");
        } else if (command instanceof UserCommand) {
            ((UserCommand) command).perform(event);
        } else if (command instanceof InvokedHomedCommand) {
            throw new BotErrorException(event.getCommand() + " can only be performed for a home.");
        } else {
            throw new BotErrorException(
                    String.format("Command type %s cannot be applied to a CommandInvokedEven.", command.getClass()));
        }
    }

    private interface ThrowingFunction {
        void apply() throws Exception;
    }

    private void wrapForErrorHandling(final ThrowingFunction function) {
        try {
            function.apply();
        } catch (BotErrorException e) {
            LOGGER.error(e.getErrorMessage(), e);
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public void wrapForInvokedErrorHandling(final CommandInvokedEvent event, final Command command,
            final ThrowingFunction function) {
        wrapForErrorHandling(() -> {
            try {
                User sender = event.getSender();
                LOGGER.info("Performing {} for {} with arguments '{}'.", event.getCommand(), sender.getName(),
                        event.getArguments());

                if (shouldPerform(sender.getHomedUser().getId(), command, event)) {
                    if (command.hasPermissions(sender)) {
                        function.apply();
                    } else {
                        event.say(String.format("%s is not allowed to %s.", sender.getName(), event.getCommand()));
                    }
                }
            } catch (BotErrorException e) {
                event.say(e.getErrorMessage());
            }
        });
    }

    private boolean shouldPerform(final int userId, final Command command, final Event event) {
        if (event instanceof HomeEvent &&
                (command.isOnlyWhileStreaming() && !((HomeEvent) event).getHome().isStreaming())) {
            return false;
        }
        return (event.getServiceType() == Service.NO_SERVICE_TYPE || command.getService(event.getServiceType()))
                && rateLimiter.allow(userId, command.getId(), command.getRateLimit());
    }
}
