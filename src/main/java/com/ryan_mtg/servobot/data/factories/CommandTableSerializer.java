package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.Application;
import com.ryan_mtg.servobot.commands.CommandAlert;
import com.ryan_mtg.servobot.data.models.AlertGeneratorRow;
import com.ryan_mtg.servobot.data.models.CommandAlertRow;
import com.ryan_mtg.servobot.data.models.CommandAliasRow;
import com.ryan_mtg.servobot.data.models.CommandEventRow;
import com.ryan_mtg.servobot.data.models.CommandRow;
import com.ryan_mtg.servobot.data.repositories.AlertGeneratorRepository;
import com.ryan_mtg.servobot.data.repositories.CommandAlertRepository;
import com.ryan_mtg.servobot.data.repositories.CommandAliasRepository;
import com.ryan_mtg.servobot.data.repositories.CommandEventRepository;
import com.ryan_mtg.servobot.data.repositories.CommandRepository;
import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.CommandAlias;
import com.ryan_mtg.servobot.commands.CommandEvent;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.FactsCommand;
import com.ryan_mtg.servobot.commands.HomeCommand;
import com.ryan_mtg.servobot.commands.MessageChannelCommand;
import com.ryan_mtg.servobot.commands.MessageCommand;
import com.ryan_mtg.servobot.commands.TextCommand;
import com.ryan_mtg.servobot.commands.TierCommand;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.model.alerts.AlertGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class CommandTableSerializer {
    private static Logger LOGGER = LoggerFactory.getLogger(CommandTableSerializer.class);

    @Autowired
    private CommandRepository commandRepository;

    @Autowired
    private CommandSerializer commandSerializer;

    @Autowired
    private AlertGeneratorSerializer alertGeneratorSerializer;

    @Autowired
    private CommandAliasRepository commandAliasRepository;

    @Autowired
    private CommandEventRepository commandEventRepository;

    @Autowired
    private CommandAlertRepository commandAlertRepository;

    @Autowired
    private AlertGeneratorRepository alertGeneratorRepository;

    @Value("${startup.database}")
    private boolean useDatabase;

    public CommandTable createCommandTable(final int botHomeId) {
        if (useDatabase) {
            return createPersistedCommandTable(botHomeId);
        }
        return getMooseCommandTable();
    }

    public void saveCommandTable(final CommandTable commandTable, final int botHomeId) {
        List<CommandAlias> aliases = commandTable.getAliases();
        Set<MessageCommand> aliasedCommands = new HashSet<>();
        Map<Command, Integer> commandIdMap = new HashMap<>();

        for(CommandAlias alias : aliases) {
            MessageCommand command = commandTable.getCommands(alias.getAlias());
            aliasedCommands.add(command);
        }

        for(MessageCommand command : aliasedCommands) {
            CommandRow commandRow = commandSerializer.saveCommand(botHomeId, command);
            commandRepository.save(commandRow);
            commandIdMap.put(command, commandRow.getId());
        }

        for(CommandAlias alias : aliases) {
            MessageCommand command = commandTable.getCommands(alias.getAlias());
            CommandAliasRow aliasRow = new CommandAliasRow(alias.getId(), commandIdMap.get(command), alias.getAlias());
            commandAliasRepository.save(aliasRow);
        }

        Set<HomeCommand> triggeredCommands = new HashSet<>();

        List<CommandEvent> events = commandTable.getEvents();
        for (CommandEvent event : events) {
            triggeredCommands.addAll(commandTable.getCommands(event.getEventType()));
        }

        for(HomeCommand command : triggeredCommands) {
            CommandRow commandRow = commandSerializer.saveCommand(botHomeId, command);
            commandRepository.save(commandRow);
            commandIdMap.put(command, commandRow.getId());
        }

        for (CommandEvent event : events) {
            HomeCommand command = commandTable.getCommand(event);
            CommandEventRow eventRow = new CommandEventRow(event.getId(), commandIdMap.get(command),
                    event.getEventType());
            commandEventRepository.save(eventRow);
        }
    }

    private CommandTable createPersistedCommandTable(final int botHomeId) {
        Iterable<CommandRow> commandRows = commandRepository.findAllByBotHomeId(botHomeId);
        CommandTable commandTable = new CommandTable(false);
        Set<String> alertTokens = new HashSet<>();

        for (CommandRow commandRow : commandRows) {
            Command command = commandSerializer.createCommand(commandRow);

            Iterable<CommandAliasRow> aliases = commandAliasRepository.findAllByCommandId(commandRow.getId());
            if (aliases.iterator().hasNext()) {
                MessageCommand messageCommand = (MessageCommand) command;
                for (CommandAliasRow alias : aliases) {
                    commandTable.registerCommand(messageCommand, new CommandAlias(alias.getId(), alias.getAlias()));
                }
            }

            Iterable<CommandEventRow> events = commandEventRepository.findAllByCommandId(commandRow.getId());
            for (CommandEventRow event : events) {
                HomeCommand homeCommand = (HomeCommand) command;
                commandTable.registerCommand(homeCommand, new CommandEvent(event.getId(), event.getEventType()));
            }

            Iterable<CommandAlertRow> alerts = commandAlertRepository.findAllByCommandId(commandRow.getId());
            for (CommandAlertRow alert : alerts) {
                HomeCommand homeCommand = (HomeCommand) command;
                String alertToken = alert.getAlertToken();
                commandTable.registerCommand(homeCommand, new CommandAlert(alert.getId(), alert.getAlertToken()));
                alertTokens.add(alertToken);
            }
        }

        Iterable<AlertGeneratorRow> alertGeneratorRows = alertGeneratorRepository.findByBotHomeId(botHomeId);

        List<AlertGenerator> alertGenerators = StreamSupport.stream(alertGeneratorRows.spliterator(), true)
                .map(alertGeneratorRow -> alertGeneratorSerializer.createAlertGenerator(alertGeneratorRow))
                .collect(Collectors.toList());

        commandTable.setAlertGenerators(alertGenerators);

        return commandTable;
    }

    private CommandTable getMooseCommandTable() {
        CommandTable commandTable = new CommandTable(false);
        commandTable.registerCommand(new TierCommand(Command.UNREGISTERED_ID), "tier");
        commandTable.registerCommand(new FactsCommand(Command.UNREGISTERED_ID, "MooseFacts"), "mooseFacts");
        commandTable.registerCommand(new FactsCommand(Command.UNREGISTERED_ID, "MooseLies"), "mooseLies", "meeseFacts");
        commandTable.registerCommand(new FactsCommand(Command.UNREGISTERED_ID, "ServoFacts"), "servofacts");
        commandTable.registerCommand(new FactsCommand(Command.UNREGISTERED_ID, "CanadaFacts"), "canadaFacts");
        commandTable.registerCommand(new FactsCommand(Command.UNREGISTERED_ID, "FrankFacts"), "frankFacts");
        commandTable.registerCommand(new FactsCommand(Command.UNREGISTERED_ID, "CommandFacts"), "commandFacts");
        commandTable.registerCommand(new TextCommand(Command.UNREGISTERED_ID, "Hello %user%, I am MoosersBot!"), "hello", "moose","hi");

        commandTable.registerCommand(new TextCommand(Command.UNREGISTERED_ID, "You found it, duh!"), "discord");
        commandTable.registerCommand(new TextCommand(Command.UNREGISTERED_ID, "themightylinguine is a little less the on twitter: https://twitter.com/MightyLinguine"), "twitter");

        commandTable.registerCommand(new TextCommand(Command.UNREGISTERED_ID, "Carolyn is on the FAM: Friends and Magic Podcast. Use !googlecast !applecast or !spotifycast for links."), "podcast");
        commandTable.registerCommand(new TextCommand(Command.UNREGISTERED_ID, "https://open.spotify.com/show/0smeuYeWNKjpdw0AfdI9Eq"), "spotifycast");
        commandTable.registerCommand(new TextCommand(Command.UNREGISTERED_ID, "https://podcasts.google.com/?feed=aHR0cHM6Ly9hbmNob3IuZm0vcy9mMjg5ODZjL3BvZGNhc3QvcnNz"), "googlecast");
        commandTable.registerCommand(new TextCommand(Command.UNREGISTERED_ID, "https://podcasts.apple.com/us/podcast/fam-friends-and-magic/id1482838493"), "applecast");
        commandTable.registerCommand(new TextCommand(Command.UNREGISTERED_ID, "MoosersBot has facts on moose, Canada, servos, meese, and Frank. It will one day have facts on sloths, but those are coming slowly."), "factfacts");

        String channelName = Application.isTesting() ? "general" : "a-moose-ments";
        HomeCommand streamStartCommand = new MessageChannelCommand(Command.UNREGISTERED_ID, DiscordService.TYPE, channelName,
                    "@everyone should know that Linguine is going live! http://twitch.tv/themightylinguine");

        commandTable.registerCommand(streamStartCommand, new CommandEvent(CommandEvent.UNREGISTERED_ID, CommandEvent.Type.STREAM_START));
        return commandTable;
    }
}
