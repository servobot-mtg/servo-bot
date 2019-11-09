package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;

public interface MessageSentEvent extends Event {
    Message getMessage();
    User getSender();
}
