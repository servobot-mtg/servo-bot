package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.Bot;
import com.ryan_mtg.servobot.model.HomeEditor;
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
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        homeEditor.setTimeZone(request.getTimeZone());
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
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.secureCommand(request.getObjectId(), request.getSecure());
    }

    @PostMapping(value = "/api/secure_reaction", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean secureReaction(@RequestBody final SecureRequest request) {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.secureReaction(request.getObjectId(), request.getSecure());
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

    @PostMapping(value = "/api/set_command_permission", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Permission secureReaction(@RequestBody final SetPermissionRequest request) {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.setCommandPermission(request.getCommandId(), request.getPermission());
    }

    public static class SetPermissionRequest {
        private int botHomeId;
        private int commandId;
        private Permission permission;

        public int getBotHomeId() {
            return botHomeId;
        }

        public int getCommandId() {
            return commandId;
        }

        public Permission getPermission() {
            return permission;
        }
    }

    @PostMapping(value = "/api/delete_command", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteCommand(@RequestBody final DeleteCommandRequest request) {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        try {
            homeEditor.deleteCommand(request.getCommandName());
            return true;
        } catch (BotErrorException e) {
            return false;
        }
    }

    public static class DeleteCommandRequest {
        private int botHomeId;
        private String commandName;

        public int getBotHomeId() {
            return botHomeId;
        }

        public String getCommandName() {
            return commandName;
        }
    }

    @PostMapping(value = "/api/delete_statement", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteStatement(@RequestBody final DeleteStatementRequest request) {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        homeEditor.deleteStatement(request.getBookId(), request.getStatementId());
        return true;
    }

    public static class DeleteStatementRequest {
        private int botHomeId;
        private int bookId;
        private int statementId;

        public int getBotHomeId() {
            return botHomeId;
        }

        public int getBookId() {
            return bookId;
        }

        public int getStatementId() {
            return statementId;
        }
    }

    @PostMapping(value = "/api/modify_statement", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean modifyStatement(@RequestBody final ModifyStatementRequest request) {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        homeEditor.modifyStatement(request.getBookId(), request.getStatementId(), request.getText());
        return true;
    }

    public static class ModifyStatementRequest {
        private int botHomeId;
        private int bookId;
        private int statementId;
        private String text;

        public int getBotHomeId() {
            return botHomeId;
        }

        public int getBookId() {
            return bookId;
        }

        public int getStatementId() {
            return statementId;
        }

        public String getText() {
            return text;
        }
    }


    private HomeEditor getHomeEditor(final int botHomeId) {
        return bot.getHomeEditor(botHomeId);
    }
}
