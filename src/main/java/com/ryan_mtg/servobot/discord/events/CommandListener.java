package com.ryan_mtg.servobot.discord.events;

import com.ryan_mtg.servobot.discord.commands.CommandEvent;
import com.ryan_mtg.servobot.discord.commands.CommandTable;
import com.ryan_mtg.servobot.discord.commands.HomeCommand;
import com.ryan_mtg.servobot.discord.commands.MessageCommand;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.events.user.update.GenericUserPresenceEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Scanner;

public class CommandListener extends ListenerAdapter {
    private static Logger LOGGER = LoggerFactory.getLogger(CommandListener.class);
    private CommandTable commandTable;

    public CommandListener(final CommandTable commandTable) {
        this.commandTable = commandTable;
    }

    @Override
    public void onGuildMessageReceived(final @Nonnull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        LOGGER.trace("CommandListener seeing event for " + event.getMessage().getContentRaw());

        Message message = event.getMessage();
        Scanner scanner = new Scanner(message.getContentDisplay());
        if (!scanner.hasNext()) {
            return;
        }

        String firstToken = scanner.next();
        if (firstToken.charAt(0) != '!' || firstToken.length() <= 1) {
            return;
        }

        String command = firstToken.substring(1);

        scanner.useDelimiter("\\z");
        String arguments = scanner.hasNext() ? scanner.next() : null;

        MessageCommand messageCommand = commandTable.getCommand(command);

        if (messageCommand != null) {
            LOGGER.info("Peforming " + command + " for " + message.getAuthor() + " with arguments " + arguments);
            messageCommand.perform(message, arguments);
        } else {
            LOGGER.warn("Unknown command " + command + " for " + message.getAuthor() + " with arguments " + arguments);
        }
    }

    @Override
    public void onUserActivityStart(@Nonnull UserActivityStartEvent event) {
        LOGGER.trace(event.getMember() + " Changed activity to " + event.getNewActivity());
        if (event.getNewActivity().getType() == Activity.ActivityType.STREAMING && isStreamer(event)) {
            for (HomeCommand command : commandTable.getCommand(CommandEvent.Type.STREAM_START)) {
                command.perform(event.getGuild());
            }
        }
    }

    private boolean isStreamer(final GenericUserPresenceEvent event) {
        return event.getMember().getIdLong() == event.getGuild().getOwnerIdLong();
    }
}
