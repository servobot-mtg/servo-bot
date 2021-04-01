package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.commands.RateLimiter;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.HomeCommand;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedCommand;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedHomedCommand;
import com.ryan_mtg.servobot.commands.hierarchy.MessagedHomeCommand;
import com.ryan_mtg.servobot.commands.hierarchy.UserCommand;
import com.ryan_mtg.servobot.commands.hierarchy.UserHomedCommand;
import com.ryan_mtg.servobot.error.BotErrorHandler;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.utility.CommandParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

import static com.ryan_mtg.servobot.user.User.UNREGISTERED_ID;

public class CommandPerformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandPerformer.class);
    private static final Pattern COMMAND_PATTERN = Pattern.compile("!\\w+");
    private static final CommandParser COMMAND_PARSER = new CommandParser(COMMAND_PATTERN);

    private final RateLimiter rateLimiter;

    public CommandPerformer(final RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    public CommandParser getCommandParser() {
        return COMMAND_PARSER;
    }

    public void perform(final UserHomeEvent userEvent, final UserHomedCommand command) {
        BotErrorHandler.handleError(() -> {
            if (shouldPerform(userEvent.getUser().getId(), command, userEvent)) {
                LOGGER.info("Performing {} at {}", command.getId(), userEvent.getServiceHome().getName());
                command.perform(userEvent);
            }
        });
    }

    public void perform(final HomeEvent homeEvent, final HomeCommand command) {
        BotErrorHandler.handleError(() -> {
            if (shouldPerform(UNREGISTERED_ID, command, homeEvent)) {
                LOGGER.info("Performing command {} for {}", command.getId(), homeEvent.getServiceHome().getName());
                command.perform(homeEvent);
            }
        });
    }

    public void perform(final CommandInvokedHomeEvent event, final Command command) {
        wrapForInvokedErrorHandling(event, command, () -> dynamicPerform(event, command));
    }

    public void perform(final CommandInvokedEvent event, final Command command) {
        wrapForInvokedErrorHandling(event, command, () -> dynamicPerform(event, command));
    }

    public void perform(final MessageHomeEvent messageHomeEvent, final Command command) {
        BotErrorHandler.handleError(() -> {
            if (shouldPerform(UNREGISTERED_ID, command, messageHomeEvent)) {
                LOGGER.info("Performing command {} for {}", command.getId(),
                        messageHomeEvent.getServiceHome().getName());
                dynamicPerform(messageHomeEvent, command);
            }
        });
    }

    private void dynamicPerform(final MessageHomeEvent event, final Command command) throws Exception {
        if (command instanceof MessagedHomeCommand) {
            ((MessagedHomeCommand) command).perform(event);
        }else if (command instanceof HomeCommand) {
            ((HomeCommand) command).perform(event);
        } else if (command instanceof UserCommand) {
            ((UserCommand) command).perform(event);
        } else {
            throw new SystemError("Command type %s cannot be applied to a MessageHomeEvent.", command.getClass());
        }
    }

    private void dynamicPerform(final CommandInvokedHomeEvent event, final Command command) throws Exception {
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
        } else if (command instanceof UserHomedCommand) {
            ((UserHomedCommand) command).perform(event);
        } else {
            throw new SystemError("Command type %s cannot be applied to a CommandInvokedEvent.", command.getClass());
        }
    }

    private void dynamicPerform(final CommandInvokedEvent event, final Command command) throws Exception {
        if (command instanceof InvokedCommand) {
            ((InvokedCommand) command).perform(event);
        } else if (command instanceof HomeCommand) {
            throw new SystemError("%s can only be performed for a home.", event.getCommand());
        }else if (command instanceof MessagedHomeCommand) {
            throw new SystemError("%s can only be performed for a home.", event.getCommand());
        } else if (command instanceof UserCommand) {
            ((UserCommand) command).perform(event);
        } else if (command instanceof InvokedHomedCommand) {
            throw new SystemError("%s can only be performed for a home.", event.getCommand());
        } else {
            throw new SystemError("Command type %s cannot be applied to a CommandInvokedEven.", command.getClass());
        }
    }

    private interface ThrowingFunction {
        void apply() throws Exception;
    }

    public void wrapForInvokedErrorHandling(final CommandInvokedEvent event, final Command command,
            final ThrowingFunction function) {
        BotErrorHandler.handleError(() -> {
            try {
                User sender = event.getSender();
                LOGGER.info("Performing {} for {} with arguments '{}'.", event.getCommand(), sender.getName(),
                        event.getArguments());

                if (shouldPerform(sender.getId(), command, event)) {
                    if (command.hasPermissions(sender)) {
                        function.apply();
                    } else {
                        event.say(String.format("%s is not allowed to %s.", sender.getName(), event.getCommand()));
                    }
                }
            } catch (UserError e) {
                event.say(e.getMessage());
            }
        });
    }

    private boolean shouldPerform(final int userId, final Command command, final Event event) {
        if (event instanceof HomeEvent &&
                (command.isOnlyWhileStreaming() && !((HomeEvent) event).getServiceHome().isStreaming())) {
            return false;
        }
        return (event.getServiceType() == Service.NO_SERVICE_TYPE || command.getService(event.getServiceType()))
                && rateLimiter.allow(userId, command.getId(), command.getRateLimit());
    }
}
