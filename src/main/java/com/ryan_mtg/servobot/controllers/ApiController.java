package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.commands.Trigger;
import com.ryan_mtg.servobot.data.models.CommandRow;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.BotEditor;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.BotRegistrar;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.alerts.AlertGenerator;
import com.ryan_mtg.servobot.model.giveaway.Giveaway;
import com.ryan_mtg.servobot.model.giveaway.Prize;
import com.ryan_mtg.servobot.model.Statement;
import com.ryan_mtg.servobot.model.reaction.Pattern;
import com.ryan_mtg.servobot.model.reaction.Reaction;
import com.ryan_mtg.servobot.security.WebsiteUser;
import com.ryan_mtg.servobot.security.WebsiteUserFactory;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.utility.Flags;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {
    private static Logger LOGGER = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    private BotRegistrar botRegistrar;

    @Autowired
    private WebsiteUserFactory websiteUserFactory;

    @PostMapping(value = "/create_bot_home", consumes = MediaType.APPLICATION_JSON_VALUE)
    public BotHome createBotHome(final Authentication authentication, @RequestBody final CreateBotHomeRequest request)
            throws BotErrorException {
        WebsiteUser websiteUser = websiteUserFactory.createWebsiteUser(authentication);
        BotEditor botEditor = botRegistrar.getBotEditor(request.getBotName());
        return botEditor.createBotHome(websiteUser.getUserId(), request);
    }

    public static class TextCommandRequest {
        private String name;
        private String value;

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

    public static class CreateBotHomeRequest {
        private String botName;
        private String timeZone;
        private String addCommandName;
        private String deleteCommandName;
        private String showCommandsName;
        private List<TextCommandRequest> textCommands;

        public String getBotName() {
            return botName;
        }

        public String getTimeZone() {
            return timeZone;
        }

        public String getAddCommandName() {
            return addCommandName;
        }

        public String getDeleteCommandName() {
            return deleteCommandName;
        }

        public String getShowCommandsName() {
            return showCommandsName;
        }

        public List<TextCommandRequest> getTextCommands() {
            return textCommands;
        }
    }


    @PostMapping(value = "/modify_bot_name", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean modifyBotName(@RequestBody final ModifyBotNameRequest request) {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        homeEditor.modifyBotName(request.getText());
        return true;
    }

    public static class BotHomeRequest {
        private int botHomeId;

        public int getBotHomeId() {
            return botHomeId;
        }
    }

    public static class ModifyBotNameRequest extends BotHomeRequest {
        private String text;

        public String getText() {
            return text;
        }
    }

    @PostMapping(value = "/set_home_time_zone", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void setBotHomeTimeZone(@RequestBody final SetBotHomeTimeZoneRequest request) {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        homeEditor.setTimeZone(request.getTimeZone());
    }

    public static class SetBotHomeTimeZoneRequest extends BotHomeRequest {
        private String timeZone;

        public String getTimeZone() {
            return timeZone;
        }
    }

    @PostMapping(value = "/secure_command", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean secureCommand(@RequestBody final SecureRequest request) {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.secureCommand(request.getObjectId(), request.getSecure());
    }

    @PostMapping(value = "/secure_reaction", consumes = MediaType.APPLICATION_JSON_VALUE)
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

    @PostMapping(value = "/set_command_service", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean setCommandService(@RequestBody final SetCommandServiceRequest request) {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.setCommandService(request.getCommandId(), request.getServiceType(), request.getValue());
    }

    public static abstract class CommandRequest extends BotHomeRequest {
        @Getter
        private int commandId;
    }

    public static class SetCommandServiceRequest extends CommandRequest {
        private int serviceType;
        private boolean value;

        public int getServiceType() {
            return serviceType;
        }

        public boolean getValue() {
            return value;
        }
    }

    @PostMapping(value = "/set_command_permission", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Permission setCommandPermission(@RequestBody final SetPermissionRequest request) {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.setCommandPermission(request.getCommandId(), request.getPermission());
    }

    public static class SetPermissionRequest extends CommandRequest {
        private Permission permission;

        public Permission getPermission() {
            return permission;
        }
    }

    @PostMapping(value = "/set_command_only_while_streaming", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean setIsOnlyWhileStreaming(@RequestBody final SetCommandOnlyWhileStreamingRequest request) {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.setCommandOnlyWhileStreaming(request.getCommandId(), request.isOnlyWhileStreaming());
    }

    public static class SetCommandOnlyWhileStreamingRequest extends CommandRequest {
        @Getter
        private boolean onlyWhileStreaming;
    }

    @PostMapping(value = "/add_statement", consumes = MediaType.APPLICATION_JSON_VALUE,
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

    @PostMapping(value = "/delete_statement", consumes = MediaType.APPLICATION_JSON_VALUE)
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

    @PostMapping(value = "/modify_statement", consumes = MediaType.APPLICATION_JSON_VALUE)
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

    @PostMapping(value = "/add_trigger", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AddTriggerResponse addTrigger(@RequestBody final AddTriggerRequest request) throws BotErrorException {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        List<Trigger> a = homeEditor.addTrigger(request.getCommandId(), request.getTriggerType(), request.getText());
        return new AddTriggerResponse(a.get(0), a.size() > 1 ? a.get(1): null);
    }

    public static class AddTriggerRequest extends CommandRequest {
        private int triggerType;
        private String text;

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

    @PostMapping(value = "/trigger_alert", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean triggerAlert(@RequestBody final TriggerAlertRequest request) {
        return wrapCall(() -> {
            HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
            homeEditor.alert(request.getAlertToken());
        });
    }

    public static class TriggerAlertRequest extends BotHomeRequest {
        private String alertToken;

        public String getAlertToken() {
            return alertToken;
        }
    }

    @PostMapping(value = "/add_command", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CommandDescriptor addCommand(@RequestBody final AddCommandRequest request) throws BotErrorException {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        CommandRow commandRow = new CommandRow();
        commandRow.setType(request.getType());
        commandRow.setPermission(request.getPermission());
        commandRow.setFlags(request.getFlags());
        commandRow.setStringParameter(request.getStringParameter());
        commandRow.setStringParameter2(request.getStringParameter2());
        commandRow.setLongParameter(request.getLongParameter());
        return homeEditor.addCommand(commandRow);
    }

    public static class AddCommandRequest extends BotHomeRequest {
        private int type;
        private Permission permission;
        private int flags;
        private String stringParameter;
        private String stringParameter2;
        private Long longParameter;

        public int getType() {
            return type;
        }

        public Permission getPermission() {
            return permission;
        }

        public int getFlags() {
            return flags;
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

    @PostMapping(value = "/delete_command", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteCommand(@RequestBody final DeleteObjectRequest request) {
        return wrapCall(() -> {
            HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
            homeEditor.deleteCommand(request.getObjectId());
        });
    }

    @PostMapping(value = "/delete_trigger", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteTrigger(@RequestBody final DeleteObjectRequest request) {
        return wrapCall(() -> {
            HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
            homeEditor.deleteTrigger(request.getObjectId());
        });
    }

    public static class DeleteObjectRequest extends BotHomeRequest {
        private int objectId;

        public int getObjectId() {
            return objectId;
        }
    }

    @PostMapping(value = "/add_reaction", consumes = MediaType.APPLICATION_JSON_VALUE,
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

    @PostMapping(value = "/add_pattern", consumes = MediaType.APPLICATION_JSON_VALUE,
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

    @PostMapping(value = "/delete_reaction", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteReaction(@RequestBody final DeleteObjectRequest request) {
        return wrapCall(() -> {
            HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
            homeEditor.deleteReaction(request.getObjectId());
        });
    }

    @PostMapping(value = "/delete_pattern", consumes = MediaType.APPLICATION_JSON_VALUE,
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

    @PostMapping(value = "/add_alert", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AlertGenerator addAlert(@RequestBody final AddAlertRequest request) throws BotErrorException {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.addAlert(request.getType(), request.getKeyword(), request.getTime());
    }

    public static class AddAlertRequest extends BotHomeRequest {
        @Getter
        private int type;

        @Getter
        private String keyword;

        @Getter
        private int time;
    }

    @PostMapping(value = "/delete_alert", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteAlert(@RequestBody final DeleteObjectRequest request) {
        return wrapCall(() -> {
            HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
            homeEditor.deleteAlert(request.getObjectId());
        });
    }

    @PostMapping(value = "/stop_home", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean stopHome(@RequestBody final BotHomeRequest request) {
        return wrapCall(() -> botRegistrar.getBotEditor(request.getBotHomeId()).stopHome(request.getBotHomeId()));
    }

    @PostMapping(value = "/start_home", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean restartHome(@RequestBody final BotHomeRequest request) {
        return wrapCall(() -> botRegistrar.getBotEditor(request.getBotHomeId()).restartHome(request.getBotHomeId()));
    }

    @PostMapping(value = "/add_giveaway", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Giveaway addGiveaway(@RequestBody final AddGiveawayRequest request) throws BotErrorException {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.addGiveaway(request.getName(), request.isSelfService(), request.isRaffle());
    }

    public static class AddGiveawayRequest extends BotHomeRequest {
        private String name;
        private boolean selfService;
        private boolean raffle;

        public String getName() {
            return name;
        }

        public boolean isSelfService() {
            return selfService;
        }

        public boolean isRaffle() {
            return raffle;
        }
    }

    @PostMapping(value = "/save_giveaway_self_service", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Giveaway saveGiveawaySelfService(@RequestBody final SaveSelfServiceRequest request)
            throws BotErrorException {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.saveGiveawaySelfService(request.getGiveawayId(), request.getRequestPrizeCommandName(),
                request.getPrizeRequestLimit(), request.getPrizeRequestUserLimit());
    }

    public static class GiveawayRequest extends BotHomeRequest {
        private int giveawayId;

        public int getGiveawayId() {
            return giveawayId;
        }
    }

    public static class SaveSelfServiceRequest extends GiveawayRequest {
        private String requestPrizeCommandName;
        private int prizeRequestLimit;
        private int prizeRequestUserLimit;

        public String getRequestPrizeCommandName() {
            return requestPrizeCommandName;
        }

        public int getPrizeRequestLimit() {
            return prizeRequestLimit;
        }

        public int getPrizeRequestUserLimit() {
            return prizeRequestUserLimit;
        }
    }

    @PostMapping(value = "/save_giveaway_raffle_settings", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Giveaway saveGiveawaySelfService(@RequestBody final SaveRaffleSettingsRequest request)
            throws BotErrorException {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.saveGiveawayRaffleSettings(request.getGiveawayId(),
                Duration.of(request.getDuration(), ChronoUnit.MINUTES), request.getWinnerCount(),
                request.getStartRaffle().toSettings(), request.getEnterRaffle().toSettings(),
                request.getRaffleStatus().toSettings(), request.getWinnerResponse(), request.getDiscordChannel());
    }

    public static class CommandSettings {
        @Getter
        private String name;

        @Getter
        private Permission permission;

        @Getter
        private String message;

        @Getter
        private boolean twitch;

        @Getter
        private boolean discord;

        @Getter
        private boolean secure;

        public com.ryan_mtg.servobot.model.giveaway.CommandSettings toSettings() {
            int flags = Flags.value(Command.SECURE_FLAG, secure) | Flags.value(Command.TWITCH_FLAG, twitch)
                    | Flags.value(Command.DISCORD_FLAG, discord) | Command.TEMPORARY_FLAG;
            return new com.ryan_mtg.servobot.model.giveaway.CommandSettings(name, flags, permission, message);
        }
    }

    public static class SaveRaffleSettingsRequest extends GiveawayRequest {
        @Getter
        private int duration;

        @Getter
        private int winnerCount;

        @Getter
        private String winnerResponse;

        @Getter
        private String discordChannel;

        @Getter
        private CommandSettings startRaffle;

        @Getter
        private CommandSettings enterRaffle;

        @Getter
        private CommandSettings raffleStatus;
    }

    @PostMapping(value = "/start_giveaway", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Giveaway startGiveaway(@RequestBody final GiveawayRequest request) throws BotErrorException {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.startGiveaway(request.getGiveawayId());
    }

    @PostMapping(value = "/add_prize", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Prize addPrize(@RequestBody final AddPrizeRequest request) throws BotErrorException {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.addPrize(request.getGiveawayId(), request.getReward(), request.getDescription());
    }

    public static class AddPrizeRequest extends GiveawayRequest {
        @Getter
        private String reward;

        @Getter
        private String description;
    }

    @PostMapping(value = "/add_prizes", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Prize> addPrizes(@RequestBody final AddPrizesRequest request) throws BotErrorException {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.addPrizes(request.getGiveawayId(), request.getRewards(), request.getDescription());
    }

    public static class AddPrizesRequest extends GiveawayRequest {
        @Getter
        private String rewards;

        @Getter
        private String description;
    }

    @PostMapping(value = "/award_reward", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public HomedUser awardReward(@RequestBody final PrizeRequest request) throws BotErrorException {
        //HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        //return homeEditor.awardReward(request.getGiveawayId(), request.getRewardId());
        return null;
    }

    @PostMapping(value = "/bestow_prize", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean bestowPrize(@RequestBody final PrizeRequest request) throws BotErrorException {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.bestowPrize(request.getGiveawayId(), request.getPrizeId());
    }

    public static class PrizeRequest extends GiveawayRequest {
        private int prizeId;

        public int getPrizeId() {
            return prizeId;
        }
    }

    @PostMapping(value = "/delete_prize", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteReward(@RequestBody final DeleteGiveawayObjectRequest request) {
        return wrapCall(() -> {
            HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
            homeEditor.deletePrize(request.getGiveawayId(), request.getObjectId());
        });
    }

    public static class DeleteGiveawayObjectRequest extends DeleteObjectRequest {
        private int giveawayId;

        public int getGiveawayId() {
            return giveawayId;
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
        return botRegistrar.getHomeEditor(botHomeId);
    }


        /*
    @ExceptionHandler(BotErrorException.class)
    public BotError botErrorExceptionHandler(final BotErrorException exception) {
        exception.printStackTrace();
        return new BotError(exception.getErrorMessage());
        // TODO: make this return an error code
    }
         */

        /*
    @ExceptionHandler(Exception.class)
    public BotError botErrorHandler(final Exception exception) {
        exception.printStackTrace();
        return new BotError(exception.getMessage());
        // TODO: make this return an error code
    }
         */

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
