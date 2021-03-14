package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.controllers.error.BotError;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.BotEditor;
import com.ryan_mtg.servobot.model.BotRegistrar;
import com.ryan_mtg.servobot.model.SystemEditor;
import com.ryan_mtg.servobot.user.User;
import com.ryan_mtg.servobot.user.UserTable;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdministrationApiController {
    private final BotRegistrar botRegistrar;
    private final SystemEditor systemEditor;
    private final UserTable userTable;

    public AdministrationApiController(final BotRegistrar botRegistrar,final SystemEditor systemEditor,
            final UserTable userTable) {
        this.botRegistrar = botRegistrar;
        this.systemEditor = systemEditor;
        this.userTable = userTable;
    }

    @PostMapping(value = "/give_invite", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean giveInvite(@RequestBody final UserRequest request) {
        userTable.modifyUser(request.getUserId(), User::invite);
        return true;
    }

    @PostMapping(value = "/make_editor", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean makeEditor(@RequestBody final UserRequest request) {
        userTable.modifyUser(request.getUserId(), User::makeEditor);
        return true;
    }

    @Getter
    static class UserRequest {
        private int userId;
    }

    @PostMapping(value = "/merge_users", consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public User mergeUsers(@RequestBody final MergeUsersRequest request) throws UserError {
        return systemEditor.mergeUsers(request.getUserIds());
    }

    @Getter
    static class MergeUsersRequest {
        private List<Integer> userIds;
    }

    @PostMapping(value = "/delete_arena_username", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteArenaUsername(@RequestBody final DeleteArenaUsernameRequest request) {
        userTable.modifyUser(request.getUserId(), user -> user.setArenaUsername(null));
        return true;
    }

    @Getter
    static class DeleteArenaUsernameRequest {
        private int userId;
    }

    @PostMapping(value = "/send_message", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void sendMessage(@RequestBody final SendMessageRequest request) {
        BotEditor botEditor = botRegistrar.getDefaultBot().getBotEditor();
        User receiver = userTable.getById(request.getReceiverId());
        botEditor.sendMessage(receiver, request.getMessage(), request.getServiceType());
    }

    @Getter
    static class SendMessageRequest {
        private int receiverId;
        private String message;
        private int serviceType;
    }

    @ExceptionHandler(UserError.class)
    public ResponseEntity<BotError> botErrorExceptionHandler(final UserError userError) {
        userError.printStackTrace();
        return new ResponseEntity<>(new BotError(userError.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BotHomeError.class)
    public ResponseEntity<BotError> botErrorExceptionHandler(final BotHomeError botHomeError) {
        botHomeError.printStackTrace();
        return new ResponseEntity<>(new BotError(botHomeError.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BotHomeError.class)
    public ResponseEntity<BotError> botErrorExceptionHandler(final SystemError systemError) {
        systemError.printStackTrace();
        return new ResponseEntity<>(new BotError(systemError.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BotError> botErrorHandler(final Exception exception) {
        exception.printStackTrace();
        return new ResponseEntity<>(new BotError(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
