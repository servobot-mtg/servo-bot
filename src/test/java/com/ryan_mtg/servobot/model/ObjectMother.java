package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.events.HomeEvent;
import com.ryan_mtg.servobot.events.MessageSentEvent;
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
        return mockUser("mocked_user", mockHomedUser(userId));
    }

    public static User mockUser(final String name) {
        return mockUser(name, mock(HomedUser.class));
    }

    public static User mockUser(final String name, final HomedUser homedUser) {
        User user = mock(User.class);
        when(user.getName()).thenReturn(name);
        when(user.getHomedUser()).thenReturn(homedUser);
        return user;
    }

    public static Message mockMessage() {
        return mockMessage(0);
    }

    public static Message mockMessage(final int serviceType) {
        Message message = mock(Message.class);
        when(message.getServiceType()).thenReturn(serviceType);
        return message;
    }

    public static MessageSentEvent mockMessageSentEvent(final Channel channel) {
        return mockMessageSentEvent(mockHomeEditor(), channel);
    }

    public static MessageSentEvent mockMessageSentEvent(final BotEditor botEditor, final Channel channel) {
        return mockMessageSentEvent(botEditor, mockHomeEditor(), channel);
    }

    public static MessageSentEvent mockMessageSentEvent(final HomeEditor homeEditor, final Channel channel) {
        return mockMessageSentEvent(mockBotEditor(), homeEditor, channel);
    }

    public static MessageSentEvent mockMessageSentEvent(final Channel channel, final User sender) {
        return mockMessageSentEvent(mockHomeEditor(), channel, sender);
    }

    public static MessageSentEvent mockMessageSentEvent(final HomeEditor homeEditor, final Channel channel,
                                                        final User sender) {
        return mockMessageSentEvent(mockBotEditor(), homeEditor, channel, sender);
    }

    public static MessageSentEvent mockMessageSentEvent(final BotEditor botEditor, final HomeEditor homeEditor,
                                                        final Channel channel) {
        return mockMessageSentEvent(botEditor, homeEditor, channel, mockUser());
    }

    public static MessageSentEvent mockMessageSentEvent(final Home home, final Channel channel, final User sender,
                                                        final Message message) {
        return mockMessageSentEvent(mockBotEditor(), home, mockHomeEditor(), channel, sender, message);
    }

    public static MessageSentEvent mockMessageSentEvent(final BotEditor botEditor, final Channel channel,
                                                        final User sender) {
        return mockMessageSentEvent(botEditor, mockHomeEditor(), channel, sender);
    }

    public static MessageSentEvent mockMessageSentEvent(final BotEditor botEditor, final HomeEditor homeEditor,
                                                        final Channel channel, final User sender) {
        return mockMessageSentEvent(botEditor, mockHome(), homeEditor, channel, sender, mockMessage());
    }

    public static MessageSentEvent mockMessageSentEvent(final BotEditor botEditor, final Home home,
            final HomeEditor homeEditor, final Channel channel, final User sender, final Message message) {
        MessageSentEvent event = mock(MessageSentEvent.class);
        when(event.getBotEditor()).thenReturn(botEditor);
        when(event.getHome()).thenReturn(home);
        when(event.getHomeEditor()).thenReturn(homeEditor);
        when(event.getChannel()).thenReturn(channel);
        when(event.getSender()).thenReturn(sender);
        when(event.getMessage()).thenReturn(message);
        return event;
    }

    public static HomeEvent mockHomeEvent(final Home home) {
        return mockHomeEvent(home, 0);
    }

    public static HomeEvent mockHomeEvent(final Home home, final int serviceType) {
        HomeEvent event = mock(HomeEvent.class);
        when(event.getHome()).thenReturn(home);
        when(event.getServiceType()).thenReturn(serviceType);
        return event;
    }

    public static Home mockHome() {
        return mockHome(mockHomeEditor());
    }

    public static Home mockHome(final HomeEditor homeEditor) {
        Home home = mock(Home.class);
        when(home.getHomeEditor()).thenReturn(homeEditor);
        return home;
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