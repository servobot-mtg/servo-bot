package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.error.UserError;

import java.util.List;

public interface Channel {
    long getId();
    String getName();
    int getServiceType();

    void say(String message);
    Message sayAndWait(String text) throws UserError;
    void sendImage(String url, String fileName, String description) throws UserError;
    void sendImages(List<String> url, String fileName, List<String> description) throws UserError;
}