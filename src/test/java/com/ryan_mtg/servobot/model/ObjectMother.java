package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.events.CommandInvokedEvent;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;
import com.ryan_mtg.servobot.events.HomeEvent;
import com.ryan_mtg.servobot.model.editors.CommandTableEditor;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.user.HomedUser;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ObjectMother {
    public static HomedUser mockHomedUser(final int userId) {
        HomedUser user = mock(HomedUser.class);
        when(user.getId()).thenReturn(userId);
        return user;
    }

    public static User mockUser() {
        return mockUser("mocked_user");
    }

    public static User mockUser(final int userId) {
        return mockUser("mocked_user", userId, mockHomedUser(userId));
    }

    public static User mockUser(final String name) {
        return mockUser(name, 999999999, mock(HomedUser.class));
    }

    public static User mockUser(final String name, final int userId, final HomedUser homedUser) {
        User user = mock(User.class);
        when(user.getName()).thenReturn(name);
        when(user.getId()).thenReturn(userId);
        when(user.getHomedUser()).thenReturn(homedUser);
        return user;
    }

    public static Message mockMessage() {
        return mock(Message.class);
    }

    public static CommandInvokedEvent mockCommandInvokedEvent(final String arguments) {
        return mockCommandInvokedEvent(mock(Channel.class), arguments);
    }

    public static CommandInvokedEvent mockCommandInvokedEvent(final Channel channel, final String arguments) {
        return mockCommandInvokedEvent(mock(CommandTableEditor.class), channel, arguments);
    }

    public static CommandInvokedEvent mockCommandInvokedEvent(final CommandTableEditor commandTableEditor,
            final String arguments) {
        return mockCommandInvokedEvent(commandTableEditor, mock(Channel.class), mockUser(), arguments);
    }

    public static CommandInvokedEvent mockCommandInvokedEvent(final CommandTableEditor commandTableEditor,
            final Channel channel, final String arguments) {
        return mockCommandInvokedEvent(commandTableEditor, channel, mockUser(), arguments);
    }

    public static CommandInvokedEvent mockCommandInvokedEvent(final User sender, final String arguments) {
        return mockCommandInvokedEvent(mock(CommandTableEditor.class), mock(Channel.class), sender, arguments);
    }

    public static CommandInvokedEvent mockCommandInvokedEvent(final CommandTableEditor commandTableEditor,
            final User sender, final String arguments) {
        return mockCommandInvokedEvent(commandTableEditor, mock(Channel.class), sender, arguments);
    }

    public static CommandInvokedEvent mockCommandInvokedEvent(final CommandTableEditor commandTableEditor,
            final Channel channel, final User sender, final String arguments) {
        return mockCommandInvokedEvent(mock(Scope.class), commandTableEditor, channel, sender, arguments);
    }

    public static CommandInvokedEvent mockCommandInvokedEvent(final BotEditor botEditor, final String arguments) {
        return mockCommandInvokedEvent(botEditor, mockUser(), arguments);
    }

    public static CommandInvokedEvent mockCommandInvokedEvent(final BotEditor botEditor, final User sender,
            final String arguments) {
        return mockCommandInvokedEvent(botEditor, mock(Scope.class), mock(CommandTableEditor.class),
                mock(Channel.class), sender, arguments);
    }

    public static CommandInvokedEvent mockCommandInvokedEvent(final Scope scope, final String arguments) {
        return mockCommandInvokedEvent(scope, mock(CommandTableEditor.class), mock(Channel.class), mockUser(),
                arguments);
    }

    public static CommandInvokedEvent mockCommandInvokedEvent(final Scope scope,
            final CommandTableEditor commandTableEditor, final Channel channel, final User sender,
            final String arguments) {
        return mockCommandInvokedEvent(mock(BotEditor.class), scope, commandTableEditor, channel, sender, arguments);
    }

    public static CommandInvokedEvent mockCommandInvokedEvent(final BotEditor botEditor, final Scope scope,
            final CommandTableEditor commandTableEditor, final Channel channel, final User sender,
            final String arguments) {
        CommandInvokedEvent event = mock(CommandInvokedEvent.class);
        when(event.getCommandTableEditor()).thenReturn(commandTableEditor);
        when(event.getChannel()).thenReturn(channel);
        when(event.getSender()).thenReturn(sender);
        when(event.getScope()).thenReturn(scope);
        when(event.getArguments()).thenReturn(arguments);
        when(event.getBotEditor()).thenReturn(botEditor);
        return event;
    }

    public static CommandInvokedHomeEvent mockCommandInvokedHomeEvent(final ServiceHome serviceHome, final User sender,
            final String arguments) {
        CommandInvokedHomeEvent event = mock(CommandInvokedHomeEvent.class);
        when(event.getServiceHome()).thenReturn(serviceHome);
        when(event.getSender()).thenReturn(sender);
        when(event.getArguments()).thenReturn(arguments);
        return event;
    }

    public static HomeEvent mockHomeEvent(final ServiceHome serviceHome) {
        return mockHomeEvent(serviceHome, 0);
    }

    public static HomeEvent mockHomeEvent(final ServiceHome serviceHome, final int serviceType) {
        HomeEvent event = mock(HomeEvent.class);
        when(event.getServiceHome()).thenReturn(serviceHome);
        when(event.getServiceHome(serviceType)).thenReturn(serviceHome);
        when(event.getServiceType()).thenReturn(serviceType);
        return event;
    }

    public static ServiceHome mockServiceHome() {
        return mockServiceHome(mockHomeEditor());
    }

    public static ServiceHome mockServiceHome(final HomeEditor homeEditor) {
        ServiceHome serviceHome = mock(ServiceHome.class);
        when(serviceHome.getHomeEditor()).thenReturn(homeEditor);
        return serviceHome;
    }

    public static BotEditor mockBotEditor() {
        return mock(BotEditor.class);
    }

    public static HomeEditor mockHomeEditor() {
        return mock(HomeEditor.class);
    }

    public static Channel mockChannel() {
        return mock(Channel.class);
    }

    public static CommandMatcher isACommand(final boolean secure, final Permission permission) {
        return new CommandMatcher(secure, permission);
    }

    public static TextCommandMatcher isATextCommand(final String text) {
        return new TextCommandMatcher(text);
    }
}