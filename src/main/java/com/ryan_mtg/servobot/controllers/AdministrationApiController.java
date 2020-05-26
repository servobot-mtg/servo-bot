package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.data.factories.UserSerializer;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.BotEditor;
import com.ryan_mtg.servobot.model.BotRegistrar;
import com.ryan_mtg.servobot.user.User;
import com.ryan_mtg.servobot.user.UserTable;
import lombok.Getter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdministrationApiController {
    private final BotRegistrar botRegistrar;
    private final UserSerializer userSerializer;
    private final UserTable userTable;

    public AdministrationApiController(final BotRegistrar botRegistrar,final UserSerializer userSerializer, final UserTable userTable) {
        this.botRegistrar = botRegistrar;
        this.userSerializer = userSerializer;
        this.userTable = userTable;
    }

    @PostMapping(value = "/give_invite", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean giveInvite(@RequestBody final UserRequest request) throws BotErrorException {
        userTable.modifyUser(request.getUserId(), User::invite);
        return true;
    }

    @Getter
    static class UserRequest {
        private int userId;
    }

    @PostMapping(value = "/merge_users", consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public User mergeUsers(@RequestBody final MergeUsersRequest request) throws BotErrorException {
        return userSerializer.mergeUsers(request.getUserIds());
    }

    @Getter
    static class MergeUsersRequest {
        private List<Integer> userIds;
    }

    @PostMapping(value = "/delete_arena_username", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteArenaUsername(@RequestBody final DeleteArenaUsernameRequest request) throws BotErrorException {
        userTable.modifyUser(request.getUserId(), user -> user.setArenaUsername(null));
        return true;
    }

    @Getter
    static class DeleteArenaUsernameRequest {
        private int userId;
    }

    @PostMapping(value = "/send_message", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void sendMessage(@RequestBody final SendMessageRequest request) throws BotErrorException {
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
}
