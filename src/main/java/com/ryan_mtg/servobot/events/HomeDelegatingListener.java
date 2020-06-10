package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.model.BotEditor;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.utility.CommandParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class HomeDelegatingListener implements EventListener {
    private static Logger LOGGER = LoggerFactory.getLogger(HomeDelegatingListener.class);

    private BotEditor botEditor;
    private CommandPerformer commandPerformer;
    private CommandTable commandTable;
    private Map<Integer, HomeEditor> homeEditorMap;
    private Map<Integer, EventListener> botHomeMap = new HashMap<>();

    public HomeDelegatingListener(final BotEditor botEditor, final Map<Integer, HomeEditor> homeEditorMap,
            final CommandPerformer commandPerformer, final CommandTable commandTable) {
        this.botEditor = botEditor;
        this.homeEditorMap = homeEditorMap;
        this.commandPerformer = commandPerformer;
        this.commandTable = commandTable;
    }

    public void register(final BotHome botHome) {
        botHomeMap.put(botHome.getId(), botHome.getEventListener());
    }

    public void unregister(BotHome botHome) {
        botHomeMap.remove(botHome.getId());
    }

    @Override
    public void onPrivateMessage(final MessageEvent event) {
        event.setBotEditor(botEditor);
        Message message = event.getMessage();
        User sender = event.getSender();
        if (sender.isBot()) {
            return;
        }

        LOGGER.trace("seeing event for " + message.getContent());

        CommandParser.ParseResult parseResult = commandPerformer.getCommandParser().parse(message.getContent());
        switch (parseResult.getStatus()) {
            case NO_COMMAND:
            case COMMAND_MISMATCH:
                return;
        }

        String commandString = parseResult.getCommand().substring(1);
        String arguments = parseResult.getInput();

        Command command = commandTable.getCommand(commandString);

        if (command == null) {
            botEditor.addSuggestion(commandString);
            LOGGER.warn("Unknown command {} for {} with arguments '{}'.", commandString, sender.getName(), arguments);
        } else {
            CommandInvokedEvent commandInvokedEvent = new MessageInvokedEvent(event, commandString, arguments);
            commandPerformer.perform(commandInvokedEvent, command);
        }
    }

    @Override
    public void onMessage(final MessageHomeEvent messageHomeEvent) {
        EventListener listener = getListener(messageHomeEvent);
        if (listener != null) {
            listener.onMessage(messageHomeEvent);
        }
    }

    @Override
    public void onStreamStart(final StreamStartEvent streamStartEvent) {
        EventListener listener = getListener(streamStartEvent);
        if (listener != null) {
            listener.onStreamStart(streamStartEvent);
        }
    }

    @Override
    public void onNewUser(final UserHomeEvent newUserEvent) {
        EventListener listener = getListener(newUserEvent);
        if (listener != null) {
            listener.onNewUser(newUserEvent);
        }
    }

    @Override
    public void onRaid(final UserHomeEvent raidEvent) {
        EventListener listener = getListener(raidEvent);
        if (listener != null) {
            listener.onRaid(raidEvent);
        }
    }

    @Override
    public void onSubscribe(final UserHomeEvent subscribeEvent) {
        EventListener listener = getListener(subscribeEvent);
        if (listener != null) {
            listener.onRaid(subscribeEvent);
        }
    }

    @Override
    public void onAlert(final AlertEvent alertEvent) {
        EventListener listener = getListener(alertEvent);
        if (listener != null) {
            listener.onAlert(alertEvent);
        }
    }

    private EventListener getListener(final BotHomeEvent event) {
        int homeId = event.getHomeId();
        event.setBotEditor(botEditor);
        event.setHomeEditor(homeEditorMap.get(homeId));
        return botHomeMap.get(homeId);
    }
}
