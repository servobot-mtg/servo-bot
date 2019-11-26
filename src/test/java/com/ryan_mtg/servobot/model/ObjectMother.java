package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.commands.Permission;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ObjectMother {
    public static User mockUser(final String name) {
        User user = mock(User.class);
        when(user.getName()).thenReturn(name);
        return user;
    }

    public static Message mockMessage(final Channel channel) {
        return mockMessage(channel, mockUser("mocked_user"));
    }

    public static Message mockMessage(final Channel channel, final User sender) {
        return mockMessage(mockHome(), channel, sender, 0);
    }

    public static Message mockMessage(final Home home, final Channel channel) {
        return mockMessage(home, channel, mockUser("mocked_user"), 0);
    }

    public static Message mockMessage(final Home home, final Channel channel, final User sender, final int serviceType) {
        Message message = mock(Message.class);
        when(message.getChannel()).thenReturn(channel);
        when(message.getSender()).thenReturn(sender);
        when(message.getHome()).thenReturn(home);
        when(message.getServiceType()).thenReturn(serviceType);
        return message;
    }

    public static Home mockHome() {
        return mockHome(mockHomeEditor());
    }

    public static Home mockHome(final HomeEditor homeEditor) {
        Home home = mock(Home.class);
        when(home.getHomeEditor()).thenReturn(homeEditor);
        return home;
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