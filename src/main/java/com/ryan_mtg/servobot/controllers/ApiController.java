package com.ryan_mtg.servobot.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.commands.hierarchy.CommandDescriptor;
import com.ryan_mtg.servobot.commands.trigger.Trigger;
import com.ryan_mtg.servobot.controllers.error.BotError;
import com.ryan_mtg.servobot.data.models.CommandRow;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.LibraryError;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.EmoteLink;
import com.ryan_mtg.servobot.model.books.Book;
import com.ryan_mtg.servobot.model.BotEditor;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.BotRegistrar;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.alerts.AlertGenerator;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraft;
import com.ryan_mtg.servobot.model.editors.BookTableEditor;
import com.ryan_mtg.servobot.model.editors.ChatDraftEditor;
import com.ryan_mtg.servobot.model.editors.CommandTableEditor;
import com.ryan_mtg.servobot.model.editors.GiveawayEditor;
import com.ryan_mtg.servobot.model.editors.RoleTableEditor;
import com.ryan_mtg.servobot.model.giveaway.Giveaway;
import com.ryan_mtg.servobot.model.giveaway.GiveawayCommandSettings;
import com.ryan_mtg.servobot.model.giveaway.Prize;
import com.ryan_mtg.servobot.model.books.Statement;
import com.ryan_mtg.servobot.model.reaction.Pattern;
import com.ryan_mtg.servobot.model.reaction.Reaction;
import com.ryan_mtg.servobot.model.roles.Role;
import com.ryan_mtg.servobot.model.storage.StorageValue;
import com.ryan_mtg.servobot.security.WebsiteUser;
import com.ryan_mtg.servobot.security.WebsiteUserFactory;
import com.ryan_mtg.servobot.timestamp.VideoTimestampManager;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.user.User;
import com.ryan_mtg.servobot.utility.Flags;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {
    private final BotRegistrar botRegistrar;
    private final WebsiteUserFactory websiteUserFactory;
    private final VideoTimestampManager timestampManager;

    @PostMapping(value = "/create_bot_home", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CreateBotHomeResponse createBotHome(final Authentication authentication,
            @RequestBody final CreateBotHomeRequest request) throws UserError {
        WebsiteUser websiteUser = websiteUserFactory.createWebsiteUser(authentication);
        BotEditor botEditor = botRegistrar.getBotEditor(request.getBotName());
        BotHome botHome = botEditor.createBotHome(websiteUser.getUserId(), request);
        CreateBotHomeResponse createBotHomeResponse = new CreateBotHomeResponse();
        createBotHomeResponse.setName(botHome.getName());
        return createBotHomeResponse;
    }

    @Getter
    public static class TextCommandRequest {
        private String name;
        private String value;
    }

    @Getter
    public static class CreateBotHomeRequest {
        private String botName;
        private String timeZone;
        private String addCommandName;
        private String deleteCommandName;
        private String showCommandsName;
        private List<TextCommandRequest> textCommands;
    }

    @Getter @Setter
    public static class CreateBotHomeResponse {
        private String name;
    }

    @PostMapping(value = "/modify_bot_name", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean modifyBotName(@RequestBody final ModifyBotNameRequest request) {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        homeEditor.modifyBotName(request.getText());
        return true;
    }

    @Getter
    public static class ContextRequest {
        private int contextId;
    }

    @Getter
    public static class BotHomeRequest {
        private int botHomeId;
    }

    @Getter
    public static class ModifyBotNameRequest extends BotHomeRequest {
        private String text;
    }

    @PostMapping(value = "/set_home_time_zone", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void setBotHomeTimeZone(@RequestBody final SetBotHomeTimeZoneRequest request) {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        homeEditor.setTimeZone(request.getTimeZone());
    }

    @Getter
    public static class SetBotHomeTimeZoneRequest extends BotHomeRequest {
        private String timeZone;
    }

    @PostMapping(value = "/secure_command", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean secureCommand(@RequestBody final SecureRequest request) {
        CommandTableEditor commandTableEditor = getCommandTableEditor(request.getContextId());
        return commandTableEditor.secureCommand(request.getObjectId(), request.isSecure());
    }

    @PostMapping(value = "/secure_reaction", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean secureReaction(@RequestBody final SecureRequest request) {
        HomeEditor homeEditor = getHomeEditor(request.getContextId());
        return homeEditor.secureReaction(request.getObjectId(), request.isSecure());
    }

    @Getter
    public static class SecureRequest extends ContextRequest {
        private int objectId;
        private boolean secure;
    }

    @PostMapping(value = "/set_command_service", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean setCommandService(@RequestBody final SetCommandServiceRequest request) {
        CommandTableEditor commandTableEditor = getCommandTableEditor(request.getContextId());
        return commandTableEditor.setCommandService(request.getCommandId(), request.getServiceType(), request.getValue());
    }

    @Getter
    public static abstract class CommandRequest extends ContextRequest {
        private int commandId;
    }

    public static class SetCommandServiceRequest extends CommandRequest {
        @Getter
        private int serviceType;

        private boolean value;

        public boolean getValue() {
            return value;
        }
    }

    @PostMapping(value = "/set_command_permission", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Permission setCommandPermission(final Model model, @RequestBody final SetPermissionRequest request)
            throws UserError {
        WebsiteUser websiteUser = (WebsiteUser) model.asMap().get("user");
        CommandTableEditor commandTableEditor = getCommandTableEditor(request.getContextId());
        if (request.getContextId() > 0) {
            HomedUser homedUser = getHomeEditor(request.getContextId()).getUserById(websiteUser.getUserId());
            return commandTableEditor.setCommandPermission(homedUser, request.getCommandId(), request.getPermission());
        } else {
            User user = getBotEditor(request.getContextId()).getUserById(websiteUser.getUserId());
            return commandTableEditor.setCommandPermission(user, request.getCommandId(), request.getPermission());
        }
    }

    @Getter
    public static class SetPermissionRequest extends CommandRequest {
        private Permission permission;
    }

    @PostMapping(value = "/set_command_only_while_streaming", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean setIsOnlyWhileStreaming(@RequestBody final SetCommandOnlyWhileStreamingRequest request) {
        CommandTableEditor commandTableEditor = getCommandTableEditor(request.getContextId());
        return commandTableEditor.setCommandOnlyWhileStreaming(request.getCommandId(), request.isOnlyWhileStreaming());
    }

    @Getter
    public static class SetCommandOnlyWhileStreamingRequest extends CommandRequest {
        private boolean onlyWhileStreaming;
    }

    @PostMapping(value = "/add_book", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Book addBook(@RequestBody final AddBookRequest request) throws UserError {
        BookTableEditor bookTableEditor = getBookTableEditor(request.getContextId());
        return bookTableEditor.addBook(request.getName(), request.getStatement());
    }

    @Getter
    public static class AddBookRequest extends ContextRequest {
        private String name;
        private String statement;
    }

    @PostMapping(value = "/add_statement", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Statement addStatement(@RequestBody final AddStatementRequest request) throws UserError {
        BookTableEditor bookTableEditor = getBookTableEditor(request.getContextId());
        try {
            return bookTableEditor.addStatement(request.getBookId(), request.getText());
        } catch (LibraryError e) {
           throw new SystemError(e.getMessage(), e);
        }
    }

    @Getter
    public static class BookRequest extends ContextRequest {
        private int bookId;
    }

    @Getter
    public static class AddStatementRequest extends BookRequest {
        private String text;
    }

    @PostMapping(value = "/delete_statement", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteStatement(@RequestBody final DeleteStatementRequest request) {
        return SystemError.filter(() -> {
            BookTableEditor bookTableEditor = getBookTableEditor(request.getContextId());
            bookTableEditor.deleteStatement(request.getBookId(), request.getStatementId());
            return true;
        });
    }

    @Getter
    public static class DeleteStatementRequest extends BookRequest {
        private int statementId;
    }

    @PostMapping(value = "/modify_statement", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean modifyStatement(@RequestBody final ModifyStatementRequest request) {
        try {
            BookTableEditor bookTableEditor = getBookTableEditor(request.getContextId());
            bookTableEditor.modifyStatement(request.getBookId(), request.getStatementId(), request.getText());
            return true;
        } catch (LibraryError e) {
            throw new SystemError(e.getMessage(), e);
        }
    }

    @Getter
    public static class ModifyStatementRequest extends BookRequest {
        private int statementId;
        private String text;
    }

    @PostMapping(value = "/add_trigger", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AddTriggerResponse addTrigger(@RequestBody final AddTriggerRequest request) throws UserError {
        CommandTableEditor commandTableEditor = getCommandTableEditor(request.getContextId());
        List<Trigger> a = commandTableEditor.addTrigger(request.getCommandId(), request.getTriggerType(), request.getText());
        return new AddTriggerResponse(a.get(0), a.size() > 1 ? a.get(1): null);
    }

    @Getter
    public static class AddTriggerRequest extends CommandRequest {
        private int triggerType;
        private String text;
    }

    @AllArgsConstructor
    @Getter
    public static class AddTriggerResponse {
        private final Trigger addedTrigger;
        private final Trigger deletedTrigger;
    }

    @PostMapping(value = "/trigger_alert", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean triggerAlert(@RequestBody final TriggerAlertRequest request) {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        homeEditor.alert(request.getAlertToken());
        return true;
    }

    @Getter
    public static class TriggerAlertRequest extends BotHomeRequest {
        private String alertToken;
    }

    @PostMapping(value = "/add_command", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CommandDescriptor addCommand(@RequestBody final AddCommandRequest request) throws UserError {
        CommandTableEditor commandTableEditor = getCommandTableEditor(request.getContextId());
        CommandRow commandRow = new CommandRow();
        commandRow.setType(request.getType());
        commandRow.setPermission(request.getPermission());
        commandRow.setFlags(request.getFlags());
        commandRow.setStringParameter(request.getStringParameter());
        commandRow.setStringParameter2(request.getStringParameter2());
        commandRow.setLongParameter(request.getLongParameter());
        commandRow.setLongParameter2(request.getLongParameter2());
        return commandTableEditor.addCommand(commandRow);
    }

    @Getter
    public static class AddCommandRequest extends ContextRequest {
        private int type;
        private Permission permission;
        private int flags;
        private String stringParameter;
        private String stringParameter2;
        private Long longParameter;
        private Long longParameter2;
    }

    @PostMapping(value = "/edit_command", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CommandDescriptor editCommand(@RequestBody final EditCommandRequest request) throws UserError {
        CommandTableEditor commandTableEditor = getCommandTableEditor(request.getContextId());
        return commandTableEditor.editCommand(request.getCommandId(), request.getText());
    }

    @Getter
    public static class EditCommandRequest extends ContextRequest {
        private String text;
        private int commandId;
    }


    @PostMapping(value = "/delete_command", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteCommand(@RequestBody final DeleteHomedObjectRequest request) {
        return SystemError.filter(() -> {
            CommandTableEditor commandTableEditor = getCommandTableEditor(request.getBotHomeId());
            commandTableEditor.deleteCommand(request.getObjectId());
            return true;
        });
    }

    @PostMapping(value = "/delete_trigger", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteTrigger(@RequestBody final DeleteHomedObjectRequest request) {
        CommandTableEditor commandTableEditor = getCommandTableEditor(request.getBotHomeId());
        commandTableEditor.deleteTrigger(request.getObjectId());
        return true;
    }

    @Getter
    public static class DeleteHomedObjectRequest extends BotHomeRequest {
        private int objectId;
    }

    @Getter
    public static class DeleteContextObjectRequest extends ContextRequest {
        private int objectId;
    }

    @PostMapping(value = "/add_reaction", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Reaction addReaction(@RequestBody final AddReactionRequest request) throws UserError {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.addReaction(request.getEmote(), request.isSecure());
    }

    @Getter
    public static class AddReactionRequest extends BotHomeRequest {
        private String emote;
        private boolean secure;
    }

    @PostMapping(value = "/add_pattern", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Pattern addPattern(@RequestBody final AddPatternRequest request) throws UserError {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.addPattern(request.getReactionId(), request.getPattern());
    }

    @Getter
    public static class AddPatternRequest extends BotHomeRequest {
        private int reactionId;
        private String pattern;
    }

    @PostMapping(value = "/delete_reaction", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteReaction(@RequestBody final DeleteHomedObjectRequest request) {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        homeEditor.deleteReaction(request.getObjectId());
        return true;
    }

    @PostMapping(value = "/delete_pattern", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteReaction(@RequestBody final DeletePatternRequest request) {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        homeEditor.deletePattern(request.getReactionId(), request.getPatternId());
        return true;
    }

    @Getter
    public static class DeletePatternRequest extends BotHomeRequest {
        private int reactionId;
        private int patternId;
    }

    @PostMapping(value = "/add_role", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Role addRole(@RequestBody final AddRoleRequest request) {
        RoleTableEditor roleTableEditor = getRoleTableEditor(request.getBotHomeId());
        return roleTableEditor.addRole(request.getRole(), request.getRoleId(), request.getEmote(), request.isAppend());
    }

    @Getter
    public static class AddRoleRequest extends BotHomeRequest {
        private String role;
        private long roleId;
        private String emote;
        private boolean append;
    }

    @PostMapping(value = "/delete_role", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteRole(@RequestBody final DeleteHomedObjectRequest request) {
        RoleTableEditor roleTableEditor = getRoleTableEditor(request.getBotHomeId());
        roleTableEditor.deleteRole(request.getObjectId());
        return true;
    }

    @PostMapping(value = "/add_alert", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AlertGenerator addAlert(@RequestBody final AddAlertRequest request) throws UserError {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.addAlert(request.getType(), request.getKeyword(), request.getTime());
    }

    @Getter
    public static class AddAlertRequest extends BotHomeRequest {
        private int type;
        private String keyword;
        private int time;
    }

    @PostMapping(value = "/delete_alert", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteAlert(@RequestBody final DeleteHomedObjectRequest request) {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        homeEditor.deleteAlert(request.getObjectId());
        return true;
    }

    @PostMapping(value = "/add_storage_value", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public StorageValue addStorageValue(@RequestBody final AddStorageValueRequest request) throws UserError {
        HomeEditor homeEditor = getHomeEditor(request.getContextId());
        return homeEditor.addStorageValue(request.getType(), request.getName(), request.getValue());
    }

    @Getter
    public static class AddStorageValueRequest extends ContextRequest {
        private int type;
        private String name;
        private String value;
    }

    @PostMapping(value = "/delete_storage_value", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteStorageValue(@RequestBody final DeleteContextObjectRequest request) {
        return SystemError.filter(() -> {
            HomeEditor homeEditor = getHomeEditor(request.getContextId());
            homeEditor.deleteStorageValue(request.getObjectId());
            return true;
        });
    }


    @PostMapping(value = "/stop_home", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean stopHome(@RequestBody final BotHomeRequest request) {
        botRegistrar.getBotEditor(request.getBotHomeId()).stopHome(request.getBotHomeId());
        return true;
    }

    @PostMapping(value = "/start_home", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean restartHome(@RequestBody final BotHomeRequest request) {
        botRegistrar.getBotEditor(request.getBotHomeId()).restartHome(request.getBotHomeId());
        return true;
    }

    @Getter
    public static class AddGiveawayRequest extends BotHomeRequest {
        private String name;
        private boolean selfService;
        private boolean raffle;
    }

    @PostMapping(value = "/add_giveaway", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Giveaway addGiveaway(@RequestBody final AddGiveawayRequest request) throws UserError {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.addGiveaway(request.getName(), request.isSelfService(), request.isRaffle());
    }

    @PostMapping(value = "/save_giveaway_self_service", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Giveaway saveGiveawaySelfService(@RequestBody final SaveSelfServiceRequest request) throws UserError {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.saveGiveawaySelfService(request.getGiveawayId(), request.getRequestPrizeCommandName(),
                request.getPrizeRequestLimit(), request.getPrizeRequestUserLimit());
    }

    @Getter
    public static class GiveawayRequest extends BotHomeRequest {
        private int giveawayId;
    }

    @Getter
    public static class SaveSelfServiceRequest extends GiveawayRequest {
        private String requestPrizeCommandName;
        private int prizeRequestLimit;
        private int prizeRequestUserLimit;
    }

    @PostMapping(value = "/save_giveaway_raffle_settings", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Giveaway saveGiveawaySelfService(@RequestBody final SaveRaffleSettingsRequest request) throws UserError {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.saveGiveawayRaffleSettings(request.getGiveawayId(),
                Duration.of(request.getDuration(), ChronoUnit.MINUTES), request.getWinnerCount(),
                request.getStartRaffle().toSettings(), request.getEnterRaffle().toSettings(),
                request.getRaffleStatus().toSettings(), request.getSelectWinner().toSettings(),
                request.getDiscordChannel(), request.isTimed());
    }

    @Getter
    public static class CommandSettings {
        private String name;
        private Permission permission;
        private String message;
        private boolean twitch;
        private boolean discord;
        private boolean secure;

        public GiveawayCommandSettings toSettings() {
            int flags = Flags.value(Command.SECURE_FLAG, secure) | Flags.value(Command.TWITCH_FLAG, twitch)
                    | Flags.value(Command.DISCORD_FLAG, discord) | Command.TEMPORARY_FLAG;
            return new GiveawayCommandSettings(name, flags, permission, message);
        }
    }

    @Getter
    public static class SaveRaffleSettingsRequest extends GiveawayRequest {
        private boolean timed;
        private int duration;
        private int winnerCount;
        private String discordChannel;
        private CommandSettings startRaffle;
        private CommandSettings enterRaffle;
        private CommandSettings raffleStatus;
        private CommandSettings selectWinner;
    }

    @PostMapping(value = "/start_giveaway", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Giveaway startGiveaway(@RequestBody final GiveawayRequest request) throws UserError {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.startGiveaway(request.getGiveawayId());
    }

    @PostMapping(value = "/add_prize", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Prize addPrize(@RequestBody final AddPrizeRequest request) throws UserError {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.addPrize(request.getGiveawayId(), request.getReward(), request.getDescription());
    }

    @Getter
    public static class AddPrizeRequest extends GiveawayRequest {
        private String reward;
        private String description;
    }

    @PostMapping(value = "/add_prizes", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Prize> addPrizes(@RequestBody final AddPrizesRequest request) throws UserError {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.addPrizes(request.getGiveawayId(), request.getRewards(), request.getDescription());
    }

    @Getter
    public static class AddPrizesRequest extends GiveawayRequest {
        private String rewards;
        private String description;
    }

    @Getter
    public static class PrizeRequest extends GiveawayRequest {
        private int prizeId;
    }

    @PostMapping(value = "/reserve_prize", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Prize reservePrize(@RequestBody final PrizeRequest request) {
        return SystemError.filter(() -> {
            GiveawayEditor giveawayEditor = getGiveawayEditor(request.getBotHomeId());
            return giveawayEditor.reservePrize(request.getGiveawayId(), request.getPrizeId());
        });
    }

    @PostMapping(value = "/release_prize", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Prize releasePrize(@RequestBody final PrizeRequest request) {
        return SystemError.filter(() -> {
            GiveawayEditor giveawayEditor = getGiveawayEditor(request.getBotHomeId());
            return giveawayEditor.releasePrize(request.getGiveawayId(), request.getPrizeId());
        });
    }

    @Getter
    public static class AwardPrizeRequest extends PrizeRequest {
        private Integer winnerId;
    }

    @PostMapping(value = "/award_prize", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Prize awardPrize(@RequestBody final AwardPrizeRequest request) {
        return SystemError.filter(() -> {
            GiveawayEditor giveawayEditor = getGiveawayEditor(request.getBotHomeId());
            return giveawayEditor.awardPrize(request.getGiveawayId(), request.getPrizeId(), request.getWinnerId());
        });
    }

    @PostMapping(value = "/bestow_prize", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Prize bestowPrize(@RequestBody final PrizeRequest request) {
        return SystemError.filter(() -> {
            GiveawayEditor giveawayEditor = getGiveawayEditor(request.getBotHomeId());
            return giveawayEditor.bestowPrize(request.getGiveawayId(), request.getPrizeId());
        });
    }

    @PostMapping(value = "/delete_prize", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deletePrize(@RequestBody final DeleteGiveawayObjectRequest request) {
        return SystemError.filter(() -> {
            GiveawayEditor giveawayEditor = getGiveawayEditor(request.getBotHomeId());
            giveawayEditor.deletePrize(request.getGiveawayId(), request.getObjectId());
            return true;
        });
    }

    @Getter
    public static class DeleteGiveawayObjectRequest extends DeleteHomedObjectRequest {
        private int giveawayId;
    }

    @PostMapping(value = "/add_chat_draft", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ChatDraft addChatDraft(@RequestBody final BotHomeRequest request) throws UserError {
        ChatDraftEditor chatDraftEditor = getChatDraftEditor(request.getBotHomeId());
        return chatDraftEditor.addChatDraft();
    }

    @PostMapping(value = "/add_emote_link", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public EmoteLink addEmoteLink(@RequestBody final AddEmoteLinkRequest request) throws UserError {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        return homeEditor.addEmoteLink(request.getTwitchEmote(), request.getDiscordEmote());
    }

    @Getter
    public static class AddEmoteLinkRequest extends BotHomeRequest {
        private String twitchEmote;
        private String discordEmote;
    }

    @PostMapping(value = "/delete_emote_link", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteEmoteLink(@RequestBody final DeleteHomedObjectRequest request) {
        HomeEditor homeEditor = getHomeEditor(request.getBotHomeId());
        homeEditor.deleteEmoteLink(request.getObjectId());
        return true;
    }

    @PostMapping(value = "/delete_timestamp", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteTimestamp(@RequestBody final DeleteRequest request) {
        timestampManager.delete(request.getObjectId());
        return true;
    }

    @Getter
    public static class DeleteRequest {
        private int objectId;
    }

    private HomeEditor getHomeEditor(final int botHomeId) {
        return botRegistrar.getHomeEditor(botHomeId);
    }

    private BotEditor getBotEditor(final int contextId) {
        return botRegistrar.getBotEditor(contextId);
    }

    private CommandTableEditor getCommandTableEditor(final int contextId) {
        if (contextId > 0) {
            return botRegistrar.getHomeEditor(contextId).getCommandTableEditor();
        }
        return botRegistrar.getBotEditor(contextId).getCommandTableEditor();
    }

    private BookTableEditor getBookTableEditor(final int contextId) {
        if (contextId > 0) {
            return botRegistrar.getHomeEditor(contextId).getBookTableEditor();
        }
        return botRegistrar.getBotEditor(contextId).getBookTableEditor();
    }

    private RoleTableEditor getRoleTableEditor(final int contextId) {
        if (contextId > 0) {
            return botRegistrar.getHomeEditor(contextId).getRoleTableEditor();
        }
        return null;
    }

    private GiveawayEditor getGiveawayEditor(final int contextId) {
        if (contextId > 0) {
            return botRegistrar.getHomeEditor(contextId).getGiveawayEditor();
        }
        return null;
    }

    private ChatDraftEditor getChatDraftEditor(final int contextId) {
        if (contextId > 0) {
            return botRegistrar.getHomeEditor(contextId).getChatDraftEditor();
        }
        return null;
    }

    @ExceptionHandler(BotHomeError.class)
    public ResponseEntity<BotError> botErrorExceptionHandler(final BotHomeError botHomeError) {
        botHomeError.printStackTrace();
        return new ResponseEntity<>(new BotError(botHomeError.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BotError> botErrorHandler(final Exception exception) {
        exception.printStackTrace();
        return new ResponseEntity<>(new BotError(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper;
    }
}
