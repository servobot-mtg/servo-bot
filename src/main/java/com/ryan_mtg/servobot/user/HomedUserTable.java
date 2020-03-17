package com.ryan_mtg.servobot.user;

import com.ryan_mtg.servobot.data.factories.UserSerializer;
import com.ryan_mtg.servobot.discord.model.DiscordUserStatus;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.twitch.model.TwitchUserStatus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class HomedUserTable {
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

    public HomedUser getByUser(final User user) throws BotErrorException {
        HomedUser homedUser = getUser(user.getId());
        if (homedUser != null) {
            return homedUser;
        }

        return store(userSerializer.lookup(botHomeId, user));
    }

    public Iterable<HomedUser> getHomedUsers() throws BotErrorException {
        return getHomedUsers(userSerializer.getAllHomedUserIds(botHomeId));
    }

    public Iterable<HomedUser> getModerators() throws BotErrorException {
        return getHomedUsers(userSerializer.getModeratorUserIds(botHomeId));
    }

    public Iterable<HomedUser> getHomedUsers(final Iterable<Integer> userIds) throws BotErrorException {
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
                                    final TwitchUserStatus twitchUserStatus) throws BotErrorException {
        HomedUser homedUser = findUser(user -> user.getTwitchId() == twitchId);
        if (homedUser != null) {
            if (!twitchUsername.equals(homedUser.getTwitchUsername())) {
                userTable.modifyUser(homedUser.getUser(), user -> user.setTwitchUsername(twitchUsername));
            }
        } else {
            User user = userTable.getByDiscordId(twitchId, twitchUsername);
            homedUser = store(userSerializer.lookup(botHomeId, user));
        }
        updateStatus(homedUser, userStatus -> userStatus.merge(twitchUserStatus));
        return homedUser;
    }

    public HomedUser getByDiscordId(final long discordId, final String discordUsername,
            final DiscordUserStatus discordUserStatus) throws BotErrorException {
        HomedUser homedUser = findUser(user -> user.getDiscordId() == discordId);
        if (homedUser != null) {
            if (!discordUsername.equals(homedUser.getDiscordUsername())) {
                userTable.modifyUser(homedUser.getUser(), user -> user.setDiscordUsername(discordUsername));
            }
        } else {
            User user = userTable.getByDiscordId(discordId, discordUsername);
            homedUser = store(userSerializer.lookup(botHomeId, user));
        }
        updateStatus(homedUser, userStatus -> userStatus.merge(discordUserStatus));
        return homedUser;
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
