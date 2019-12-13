package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;

public interface MessageSentEvent extends Event {
    Home getHome();
    Channel getChannel();
    Message getMessage();
    User getSender();
}
