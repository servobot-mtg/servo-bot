package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.data.factories.UserSerializer;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.user.User;
import com.ryan_mtg.servobot.user.UserTable;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdministrationApiController {
    @Autowired
    private UserSerializer userSerializer;

    @Autowired
    private UserTable userTable;

    @PostMapping(value = "/give_invite", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean giveInvite(@RequestBody final UserRequest request) throws BotErrorException {
        userTable.modifyUser(request.getUserId(), User::invite);
        return true;
    }

    public static class UserRequest {
        @Getter
        private int userId;
    }

    @PostMapping(value = "/merge_users", consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public User mergeUsers(@RequestBody final MergeUsersRequest request) throws BotErrorException {
        return userSerializer.mergeUsers(request.getUserIds());
    }

    public static class MergeUsersRequest {
        @Getter
        private List<Integer> userIds;
    }

    @PostMapping(value = "/delete_arena_username", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteArenaUsername(@RequestBody final DeleteArenaUsernameRequest request) throws BotErrorException {
        userTable.modifyUser(request.getUserId(), user -> user.setArenaUsername(null));
        return true;
    }

    public static class DeleteArenaUsernameRequest {
        @Getter
        private int userId;
    }
}
