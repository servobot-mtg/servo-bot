package com.ryan_mtg.servobot.model;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ObjectMother {
    public static User mockUser(final String name) {
        User user = mock(User.class);
        when(user.getName()).thenReturn(name);
        return user;
    }

    public static Message mockMessage(final Channel channel) {
        return mockMessage(channel, mockUser("mockedUser"));
    }

    public static Message mockMessage(final Channel channel, final User sender) {
        return mockMessage(mockHome(), channel, sender, 0);
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
        return mock(Home.class);
    }

    public static Channel mockChannel() {
        return mock(Channel.class);
    }
}