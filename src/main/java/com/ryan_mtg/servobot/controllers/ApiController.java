package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.data.repositories.BotHomeRepository;
import com.ryan_mtg.servobot.model.Bot;
import com.ryan_mtg.servobot.model.BotHome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {
    @Autowired
    private Bot bot;

    @PostMapping(value = "/api/set_home_time_zone", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void setBotHomeTimeZone(@RequestBody final SetBotHomeTimeZoneRequest request) {
        BotHome home = bot.getHome(request.getBotHomeId());
        home.setTimeZone(request.getTimeZone());
    }

    public static class SetBotHomeTimeZoneRequest {
        private int botHomeId;
        private String timeZone;

        public int getBotHomeId() {
            return botHomeId;
        }

        public String getTimeZone() {
            return timeZone;
        }
    }

    @PostMapping(value = "/api/secure_command", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean secureCommand(@RequestBody final SecureRequest request) {
        BotHome home = bot.getHome(request.getBotHomeId());
        return home.secureCommand(request.getObjectId(), request.getSecure());
    }

    @PostMapping(value = "/api/secure_reaction", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean secureReaction(@RequestBody final SecureRequest request) {
        BotHome home = bot.getHome(request.getBotHomeId());
        return home.secureReaction(request.getObjectId(), request.getSecure());
    }

    public static class SecureRequest {
        private int botHomeId;
        private int objectId;
        private boolean secure;

        public int getBotHomeId() {
            return botHomeId;
        }

        public int getObjectId() {
            return objectId;
        }

        public boolean getSecure() {
            return secure;
        }
    }
}
