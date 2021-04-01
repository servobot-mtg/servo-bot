package com.ryan_mtg.servobot.user;

import com.ryan_mtg.servobot.data.models.UserHomeRow;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class UserHomeEdit {
    private final Map<HomedUser, Integer> savedHomeUsers = new HashMap<>();
    private final List<UserHomeRow.UserHomeId> deletedHomeUsers = new ArrayList<>();

    public void save(final int botHomeId, final HomedUser homedUser) {
        savedHomeUsers.put(homedUser, botHomeId);
    }

    public void delete(final int botHomeId, final int userId) {
        UserHomeRow.UserHomeId userHomeId = new UserHomeRow.UserHomeId();
        userHomeId.setBotHomeId(botHomeId);
        userHomeId.setUserId(userId);
        deletedHomeUsers.add(userHomeId);
    }

    public void merge(final UserHomeEdit userHomeEdit) {
        savedHomeUsers.putAll(userHomeEdit.savedHomeUsers);
        deletedHomeUsers.addAll(userHomeEdit.deletedHomeUsers);
    }
}
