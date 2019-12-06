package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.data.factories.UserSerializer;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.user.User;
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

    @PostMapping(value = "/merge_users", consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public User mergeUsers(@RequestBody final MergeUsersRequest request) throws BotErrorException {
        System.out.println(request.getUserIds());
        User user = userSerializer.mergeUsers(request.getUserIds());
        return user;
    }

    public static class MergeUsersRequest {
        private List<Integer> userIds;

        public List<Integer> getUserIds() {
            return userIds;
        }
    }
}
