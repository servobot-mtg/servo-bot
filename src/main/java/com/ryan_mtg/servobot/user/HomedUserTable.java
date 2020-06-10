package com.ryan_mtg.servobot.user;

import com.ryan_mtg.servobot.data.factories.UserSerializer;
import com.ryan_mtg.servobot.discord.model.DiscordUserStatus;
import com.ryan_mtg.servobot.twitch.model.TwitchUserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class HomedUserTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomedUserTable.class);

    private UserSerializer userSerializer;
    private UserTable userTable;
    private int botHomeId;
    private Map<Integer, WeakReference<HomedUser>> userMap = new HashMap<>();

    public HomedUserTable(final UserSerializer userSerializer, final UserTable userTable, final int botHomeId) {
        this.userSerializer = userSerializer;
        this.userTable = userTable;
        this.botHomeId = botHomeId;
    }

    public void save(final HomedUser homedUser) {
        userTable.save(homedUser.getUser());
        userSerializer.saveHomedStatus(botHomeId, homedUser);
    }

    public HomedUser getByUser(final User user) {
        HomedUser homedUser = getUser(user.getId());
        if (homedUser != null) {
            return homedUser;
        }

        return store(userSerializer.lookup(botHomeId, user));
    }

    public HomedUser getById(final int userId) {
        HomedUser homedUser = findUser(user -> user.getId() == userId);
        if (homedUser == null) {
            User user = userTable.getById(userId);
            homedUser = store(userSerializer.lookup(botHomeId, user));
        }
        return homedUser;
    }

    public Iterable<HomedUser> getHomedUsers() {
        return getHomedUsers(userSerializer.getAllHomedUserIds(botHomeId));
    }

    public Iterable<HomedUser> getModerators() {
        return getHomedUsers(userSerializer.getModeratorUserIds(botHomeId));
    }

    public Iterable<HomedUser> getHomedUsers(final Iterable<Integer> userIds) {
        List<HomedUser> users = new ArrayList<>();
        List<Integer> usersToLoad = new ArrayList<>();
        for (int userId : userIds) {
            HomedUser user = getUser(userId);
            if (user == null) {
                usersToLoad.add(userId);
            } else {
                users.add(user);
            }
        }

        Iterable<User> unhomedUsers = userTable.getUsers(usersToLoad);
        users.addAll(store(userSerializer.getHomedUsers(botHomeId, unhomedUsers)));
        return users;
    }

    public HomedUser getByTwitchId(final int twitchId, final String twitchUsername,
            final TwitchUserStatus twitchUserStatus) {
        HomedUser homedUser = getByTwitchId(twitchId, twitchUsername);
        updateStatus(homedUser, userStatus -> userStatus.update(twitchUserStatus));
        return homedUser;
    }

    public HomedUser getByTwitchId(final int twitchId, final String twitchUsername) {
        HomedUser homedUser = findUser(user -> user.getTwitchId() == twitchId);
        if (homedUser != null) {
            if (!twitchUsername.equals(homedUser.getTwitchUsername())) {
                userTable.modifyUser(homedUser.getUser(), user -> user.setTwitchUsername(twitchUsername));
            }
        } else {
            User user = userTable.getByTwitchId(twitchId, twitchUsername);
            homedUser = store(userSerializer.lookup(botHomeId, user));
        }
        return homedUser;
    }

    public HomedUser getByDiscordId(final long discordId, final String discordUsername,
                                    final DiscordUserStatus discordUserStatus) {
        HomedUser homedUser = getByDiscordId(discordId, discordUsername);
        updateStatus(homedUser, userStatus -> userStatus.update(discordUserStatus));
        return homedUser;
    }

    public HomedUser getByDiscordId(final long discordId, final String discordUsername) {
        HomedUser homedUser = findUser(user -> user.getDiscordId() == discordId);
        if (homedUser != null) {
            if (!discordUsername.equals(homedUser.getDiscordUsername())) {
                userTable.modifyUser(homedUser.getUser(), user -> user.setDiscordUsername(discordUsername));
            }
        } else {
            User user = userTable.getByDiscordId(discordId, discordUsername);
            homedUser = store(userSerializer.lookup(botHomeId, user));
        }
        return homedUser;
    }

    public UserHomeEdit update(final User user, final UserStatus userStatus) {
        HomedUser oldUser = getUser(user.getId());
        HomedUser newUser = new HomedUser(user, userStatus);
        store(newUser);

        UserHomeEdit userHomeEdit = new UserHomeEdit();
        if (oldUser == null || oldUser.getUserStatus().getState() != userStatus.getState())  {
            userHomeEdit.save(botHomeId, newUser);
        }
        return userHomeEdit;
    }

    public UserHomeEdit purge(final List<Integer> userIdsToDelete) {
        UserHomeEdit userHomeEdit = new UserHomeEdit();
        userIdsToDelete.forEach(userId -> {
            userMap.remove(userId);
            userHomeEdit.delete(botHomeId, userId);
        });
        return userHomeEdit;
    }

    private Collection<HomedUser> store(final Collection<HomedUser> users) {
        users.forEach(this::store);
        return users;
    }

    private HomedUser store(final HomedUser user) {
        userMap.put(user.getId(), new WeakReference<>(user));
        return user;
    }

    private HomedUser getUser(final int userId) {
        WeakReference<HomedUser> reference = userMap.get(userId);
        if (reference == null) {
            return null;
        }
        return reference.get();
    }

    private HomedUser findUser(final Predicate<HomedUser> query) {
        for(WeakReference<HomedUser> reference : userMap.values()) {
            HomedUser user = reference.get();
            if(user != null && query.test(user)) {
                return user;
            }
        }
        return null;
    }

    private UserStatus updateStatus(final HomedUser homedUser, final Consumer<UserStatus> mergeFunction) {
        UserStatus userStatus = homedUser.getUserStatus();
        int oldState = userStatus.getState();
        mergeFunction.accept(userStatus);

        if (userStatus.getState() != oldState) {
            userSerializer.saveHomedStatus(botHomeId, homedUser);
        }

        return userStatus;
    }
}
