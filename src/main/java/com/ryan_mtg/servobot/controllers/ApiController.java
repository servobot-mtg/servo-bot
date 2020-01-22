package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.commands.Trigger;
import com.ryan_mtg.servobot.data.models.CommandRow;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.Bot;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.Statement;
import com.ryan_mtg.servobot.model.reaction.Pattern;
import com.ryan_mtg.servobot.model.reaction.Reaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    public static class BotHomeRequest {
        private int botHomeId;

        public int getBotHomeId() {
            return botHomeId;
        }
    }

    public static class SetBotHomeTimeZoneRequest extends BotHomeRequest {
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

    public static class SecureRequest extends BotHomeRequest {
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

    public static class SetPermissionRequest extends BotHomeRequest {
        private int commandId;
        private Permission permission;

        public int getCommandId() {
            return commandId;
        }

        public Permission getPermission() {
            return permission;
        }
    }

    @PostMapping(value = "/api/add_statement", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Statement addStatement(@RequestBody final AddStatementRequest request) throws BotErrorException {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.addStatement(request.getBookId(), request.getText());
    }

    public static class BookRequest extends BotHomeRequest {
        private int bookId;

        public int getBookId() {
            return bookId;
        }
    }

    public static class AddStatementRequest extends BookRequest {
        private String text;

        public String getText() {
            return text;
        }
    }

    @PostMapping(value = "/api/delete_statement", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteStatement(@RequestBody final DeleteStatementRequest request) {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        homeEditor.deleteStatement(request.getBookId(), request.getStatementId());
        return true;
    }

    public static class DeleteStatementRequest extends BookRequest {
        private int statementId;

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

    public static class ModifyStatementRequest extends BookRequest {
        private int statementId;
        private String text;

        public int getStatementId() {
            return statementId;
        }

        public String getText() {
            return text;
        }
    }

    @PostMapping(value = "/api/add_trigger", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AddTriggerResponse addTrigger(@RequestBody final AddTriggerRequest request) throws BotErrorException {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        List<Trigger> a = homeEditor.addTrigger(request.getCommandId(), request.getTriggerType(), request.getText());
        return new AddTriggerResponse(a.get(0), a.size() > 1 ? a.get(1): null);
    }

    public static class AddTriggerRequest extends BotHomeRequest {
        private int commandId;
        private int triggerType;
        private String text;

        public int getCommandId() {
            return commandId;
        }

        public int getTriggerType() {
            return triggerType;
        }

        public String getText() {
            return text;
        }
    }

    public static class AddTriggerResponse {
        private Trigger addedTrigger;
        private Trigger deletedTrigger;

        public AddTriggerResponse(final Trigger addedTrigger, final Trigger deletedTrigger) {
            this.addedTrigger = addedTrigger;
            this.deletedTrigger = deletedTrigger;
        }

        public Trigger getAddedTrigger() {
            return addedTrigger;
        }

        public Trigger getDeletedTrigger() {
            return deletedTrigger;
        }
    }

    @PostMapping(value = "/api/add_command", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CommandDescriptor addCommand(@RequestBody final AddCommandRequest request) {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        CommandRow commandRow = new CommandRow();
        commandRow.setType(request.getType());
        commandRow.setPermission(request.getPermission());
        commandRow.setSecure(request.isSecure());
        commandRow.setStringParameter(request.getStringParameter());
        commandRow.setStringParameter2(request.getStringParameter2());
        commandRow.setLongParameter(request.getLongParameter());
        return homeEditor.addCommand(commandRow);
    }

    public static class AddCommandRequest extends BotHomeRequest {
        private int type;
        private Permission permission;
        private boolean secure;
        private String stringParameter;
        private String stringParameter2;
        private Long longParameter;

        public int getType() {
            return type;
        }

        public Permission getPermission() {
            return permission;
        }

        public boolean isSecure() {
            return secure;
        }

        public void setPermission(Permission permission) {
            this.permission = permission;
        }

        public String getStringParameter() {
            return stringParameter;
        }

        public String getStringParameter2() {
            return stringParameter2;
        }

        public Long getLongParameter() {
            return longParameter;
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

    public static class DeleteObjectRequest extends BotHomeRequest {
        private int objectId;

        public int getObjectId() {
            return objectId;
        }
    }

    @PostMapping(value = "/api/add_reaction", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Reaction addReaction(@RequestBody final AddReactionRequest request) throws BotErrorException {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.addReaction(request.getEmote(), request.isSecure());
    }

    public static class AddReactionRequest extends BotHomeRequest {
        private String emote;
        private boolean secure;

        public String getEmote() {
            return emote;
        }

        public boolean isSecure() {
            return secure;
        }
    }

    @PostMapping(value = "/api/add_pattern", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Pattern addPattern(@RequestBody final AddPatternRequest request) throws BotErrorException {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.addPattern(request.getReactionId(), request.getPattern());
    }

    public static class AddPatternRequest extends BotHomeRequest {
        private int reactionId;
        private String pattern;

        public int getReactionId() {
            return reactionId;
        }

        public String getPattern() {
            return pattern;
        }
    }

    @PostMapping(value = "/api/delete_reaction", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteReaction(@RequestBody final DeleteObjectRequest request) {
        return wrapCall(() -> {
            HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
            homeEditor.deleteReaction(request.getObjectId());
        });
    }

    @PostMapping(value = "/api/delete_pattern", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteReaction(@RequestBody final DeletePatternRequest request) {
        return wrapCall(() -> {
            HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
            homeEditor.deletePattern(request.getReactionId(), request.getPatternId());
        });
    }

    public static class DeletePatternRequest extends BotHomeRequest {
        private int reactionId;
        private int patternId;

        public int getReactionId() {
            return reactionId;
        }

        public int getPatternId() {
            return patternId;
        }
    }

    @PostMapping(value = "/api/stop_home", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean stopHome(@RequestBody final BotHomeRequest request) {
        return wrapCall(() -> bot.getBotEditor().stopHome(request.getBotHomeId()));
    }

    @PostMapping(value = "/api/start_home", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean restartHome(@RequestBody final BotHomeRequest request) {
        return wrapCall(() -> bot.getBotEditor().restartHome(request.getBotHomeId()));
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

    @ExceptionHandler(Exception.class)
    public BotError botErrorHandler(final Exception exception) {
        exception.printStackTrace();
        return new BotError(exception.getMessage());
    }

    public class BotError {
        private String message;

        public BotError(final String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
