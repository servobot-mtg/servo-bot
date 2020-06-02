package com.ryan_mtg.servobot.user;

import com.ryan_mtg.servobot.data.factories.UserSerializer;
import com.ryan_mtg.servobot.data.repositories.UserRepository;
import com.ryan_mtg.servobot.events.BotErrorException;
import org.springframework.stereotype.Component;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Component
public class UserTable {
    private final UserSerializer userSerializer;
    private final UserRepository userRepository;
    private final Map<Integer, WeakReference<User>> userMap = new HashMap<>();

    public UserTable(final UserSerializer userSerializer, final UserRepository userRepository) {
        this.userSerializer = userSerializer;
        this.userRepository = userRepository;
    }

    public void save(final User user) {
        userSerializer.saveUser(user);
    }

    public User getById(final int id) throws BotErrorException {
        User user = getUser(id);
        if (user != null) {
            return user;
        }

        return store(userSerializer.lookupById(id));
    }

    public void modifyUser(final User user, final Consumer<User> modifier) throws BotErrorException {
        modifier.accept(user);
        save(user);
    }

    public void modifyUser(final int userId, final Consumer<User> modifier) throws BotErrorException {
        modifyUser(getById(userId), modifier);
    }

    public Iterable<User> getAllUsers() throws BotErrorException {
        return getUsers(userSerializer.getAllUserIds());
    }

    public Iterable<User> getUsers(final Iterable<Integer> userIds) throws BotErrorException {
        List<User> users = new ArrayList<>();
        List<Integer> usersToLoad = new ArrayList<>();
        for (int userId : userIds) {
            User user = getUser(userId);
            if (user == null) {
                usersToLoad.add(userId);
            } else {
                users.add(user);
            }
        }

        users.addAll(store(userSerializer.getUsers(usersToLoad)));
        return users;
    }

    public List<Integer> getHomesStreamed(final int userId) {
        return userSerializer.getHomesStreamed(userId);
    }

    public List<Integer> getHomesModerated(final int userId) {
        return userSerializer.getHomesModerated(userId);
    }

    public User getByTwitchId(final int twitchId, final String twitchUserName) throws BotErrorException {
        User user = findUser(u -> twitchId == u.getTwitchId());
        if (user != null) {
            if (!twitchUserName.equals(user.getTwitchUsername())) {
                user.setTwitchUsername(twitchUserName);
                save(user);
            }
            return user;
        }

        return store(userSerializer.lookupByTwitchId(twitchId, twitchUserName));
    }

    public User getByDiscordId(final long discordId, final String discordUsername) throws BotErrorException {
        User user = findUser(u -> discordId == u.getDiscordId());
        if (user != null) {
            if (!discordUsername.equals(user.getDiscordUsername())) {
                user.setDiscordUsername(discordUsername);
                save(user);
            }
            return user;
        }

        return store(userSerializer.lookupByDiscordId(discordId, discordUsername));
    }

    public void purge(final List<Integer> usersToDelete) {
        usersToDelete.forEach(userMap::remove);
        userRepository.deleteAllById(usersToDelete);
    }

    public void update(final User user) {
        userSerializer.saveUser(store(user));
    }

    private User getUser(final int userId) {
        WeakReference<User> reference = userMap.get(userId);
        if (reference == null) {
            return null;
        }
        return reference.get();
    }

    private Collection<User> store(final Collection<User> users) {
        users.forEach(this::store);
        return users;
    }

    private User store(final User user) {
        userMap.put(user.getId(), new WeakReference<>(user));
        return user;
    }

    private User findUser(final Predicate<User> query) {
        for(WeakReference<User> reference : userMap.values()) {
            User user = reference.get();
            if(user != null && query.test(user)) {
                return user;
            }
        }
        return null;
    }
}
