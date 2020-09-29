package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.error.UserError;

public interface Channel {
    void say(String message);
    Message sayAndWait(String text) throws UserError;
    void sendImage(String url, String fileName, String description) throws UserError;
}
