package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.Bot;
import com.ryan_mtg.servobot.model.HomeEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {
    private static Logger LOGGER = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    private Bot bot;

    @PostMapping(value = "/api/set_home_time_zone", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void setBotHomeTimeZone(@RequestBody final SetBotHomeTimeZoneRequest request) {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        homeEditor.setTimeZone(request.getTimeZone());
    }

    public static abstract class AbstractBotHomeRequest {
        private int botHomeId;

        public int getBotHomeId() {
            return botHomeId;
        }
    }

    public static class SetBotHomeTimeZoneRequest extends AbstractBotHomeRequest {
        private String timeZone;

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

    public static class SecureRequest extends AbstractBotHomeRequest {
        private int objectId;
        private boolean secure;

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

    public static class SetPermissionRequest extends AbstractBotHomeRequest {
        private int commandId;
        private Permission permission;

        public int getCommandId() {
            return commandId;
        }

        public Permission getPermission() {
            return permission;
        }
    }

    @PostMapping(value = "/api/delete_statement", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteStatement(@RequestBody final DeleteStatementRequest request) {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        homeEditor.deleteStatement(request.getBookId(), request.getStatementId());
        return true;
    }

    public static class DeleteStatementRequest extends AbstractBotHomeRequest {
        private int bookId;
        private int statementId;

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

    public static class ModifyStatementRequest extends AbstractBotHomeRequest {
        private int bookId;
        private int statementId;
        private String text;

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

    @PostMapping(value = "/api/delete_command", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteCommand(@RequestBody final DeleteObjectRequest request) {
        return wrapCall(() -> {
            HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
            homeEditor.deleteCommand(request.getObjectId());
        });
    }

    @PostMapping(value = "/api/delete_alias", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteAlias(@RequestBody final DeleteObjectRequest request) {
        return wrapCall(() -> {
            HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
            homeEditor.deleteAlias(request.getObjectId());
        });
    }

    @PostMapping(value = "/api/delete_event", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteEvent(@RequestBody final DeleteObjectRequest request) {
        return wrapCall(() -> {
            HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
            homeEditor.deleteEvent(request.getObjectId());
        });
    }

    @PostMapping(value = "/api/delete_alert", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteAlert(@RequestBody final DeleteObjectRequest request) {
        return wrapCall(() -> {
            HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
            homeEditor.deleteAlert(request.getObjectId());
        });
    }

    public static class DeleteObjectRequest extends AbstractBotHomeRequest {
        private int objectId;

        public int getObjectId() {
            return objectId;
        }
    }

    private interface ApiCall {
        void call() throws BotErrorException;
    }

    private boolean wrapCall(final ApiCall operation) {
        try {
            operation.call();
            return true;
        } catch (BotErrorException e) {
            LOGGER.warn("Oops" ,e);
            e.printStackTrace();
            return false;
        }
    }

    private HomeEditor getHomeEditor(final int botHomeId) {
        return bot.getHomeEditor(botHomeId);
    }
}
