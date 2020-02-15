package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.data.models.ServiceRow;
import com.ryan_mtg.servobot.events.EventListener;
import com.ryan_mtg.servobot.user.User;

public interface Service {
    int MAX_CLIENT_ID_SIZE = ServiceRow.MAX_CLIENT_ID_SIZE;
    int MAX_CLIENT_SECRET_SIZE = ServiceRow.MAX_CLIENT_SECRET_SIZE;
    int MAX_TOKEN_SIZE = ServiceRow.MAX_TOKEN_SIZE;

    int getType();
    String getName();
    void register(BotHome botHome);
    void unregister(BotHome home);

    void start(EventListener eventListener) throws Exception;

    void whisper(User user, String message);
}
