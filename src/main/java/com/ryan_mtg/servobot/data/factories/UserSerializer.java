package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.UserHomeRow;
import com.ryan_mtg.servobot.data.models.UserRow;
import com.ryan_mtg.servobot.data.repositories.UserHomeRepository;
import com.ryan_mtg.servobot.data.repositories.UserRepository;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.user.User;
import com.ryan_mtg.servobot.user.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class UserSerializer {
    private static Logger LOGGER = LoggerFactory.getLogger(UserSerializer.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserHomeRepository userHomeRepository;

    public Iterable<Integer> getAllUserIds() {
        return userRepository.getAllIds();
    }

    public Collection<User> getUsers(final Iterable<Integer> userIds) throws BotErrorException {
        Collection<User> users = new ArrayList<>();
        for (UserRow userRow : userRepository.findAllById(userIds)) {
            users.add(createUser(userRow));
        }
        return users;
    }

    public User lookupById(final int id) throws BotErrorException {
        return createUser(userRepository.findById(id));
    }

    public void saveUser(final User user) {
        UserRow userRow = new UserRow();
        userRow.setId(user.getId());
        userRow.setTwitchId(user.getTwitchId());
        userRow.setTwitchUsername(user.getTwitchUsername());
        userRow.setDiscordId(user.getDiscordId());
        userRow.setDiscordUsername(user.getDiscordUsername());
        userRow.setArenaUsername(user.getArenaUsername());
        userRow.setFlags(user.getFlags());
        userRepository.save(userRow);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public User lookupByTwitchId(final int twitchId, final String twitchUsername) throws BotErrorException {
        UserRow userRow = lookupUserRowByTwitchId(twitchId, twitchUsername);
        return createUser(userRow);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public User lookupByDiscordId(final long discordId, final String discordUsername) throws BotErrorException {
        UserRow userRow = lookupUserRowByDiscordId(discordId, discordUsername);
        return createUser(userRow);
    }

    public List<Integer> getHomesModerated(final int userId) {
        List<UserHomeRow> userHomeRows = userHomeRepository.findByUserId(userId);
        return userHomeRows.stream().filter(userHomeRow -> new UserStatus(userHomeRow.getState()).isModerator())
                .map(UserHomeRow::getBotHomeId).collect(Collectors.toList());
    }

    public List<Integer> getHomesStreamed(final int userId) {
        List<UserHomeRow> userHomeRows = userHomeRepository.findByUserId(userId);
        return userHomeRows.stream().filter(userHomeRow -> new UserStatus(userHomeRow.getState()).isStreamer())
                .map(UserHomeRow::getBotHomeId).collect(Collectors.toList());
    }

    public void saveHomedStatus(final int botHomeId, final HomedUser homedUser) {
        UserHomeRow userHomeRow = new UserHomeRow();
        userHomeRow.setUserId(homedUser.getId());
        userHomeRow.setBotHomeId(botHomeId);
        userHomeRow.setState(homedUser.getUserStatus().getState());
        userHomeRepository.save(userHomeRow);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public HomedUser lookup(final int botHomeId, final User user) throws BotErrorException {
        UserHomeRow userHomeRow = userHomeRepository.findByUserIdAndBotHomeId(user.getId(), botHomeId);
        return createHomedUser(user, new UserStatus(userHomeRow.getState()));
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public Iterable<Integer> getAllHomedUserIds(final int botHomeId) {
        return userHomeRepository.getAllUserIds(botHomeId);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public Iterable<Integer> getModeratorUserIds(final int botHomeId) {
        return StreamSupport.stream(userHomeRepository.findByBotHomeId(botHomeId).spliterator(), false)
            .filter(userHomeRow -> new UserStatus(userHomeRow.getState()).isModerator())
            .map(userHomeRow -> userHomeRow.getUserId()).collect(Collectors.toList());
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public List<HomedUser> getHomedUsers(final int botHomeId, final Iterable<User> users) throws BotErrorException {
        Iterable<Integer> userIds = StreamSupport.stream(users.spliterator(), false).map(user -> user.getId())
                .collect(Collectors.toList());
        Iterable<UserHomeRow> userHomeRows = userHomeRepository.findByBotHomeIdAndUserIdIn(botHomeId, userIds);

        Map<Integer, UserHomeRow> homeRowById = SerializationSupport.getIdUniqueMapping(userHomeRows,
                userHomeRow -> userHomeRow.getUserId());
        userHomeRows.forEach(userHomeRow -> homeRowById.put(userHomeRow.getUserId(), userHomeRow));

        return createHomedUsers(users, homeRowById);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public UserRow lookupUserRowByTwitchId(final int twitchId, final String twitchUsername) {
        UserRow userRow = userRepository.findByTwitchId(twitchId);

        if (userRow == null) {
            userRow = new UserRow();
            userRow.setTwitchId(twitchId);
            userRow.setTwitchUsername(twitchUsername);
            userRow.setFlags(0);
            userRepository.save(userRow);
        } else if(twitchUsername != null && !twitchUsername.equals(userRow.getTwitchUsername())) {
            userRow.setTwitchUsername(twitchUsername);
            userRepository.save(userRow);
        }

        return userRow;
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public UserRow lookupUserRowByDiscordId(final long discordId, final String discordUsername) {
        UserRow userRow = userRepository.findByDiscordId(discordId);

        if (userRow == null) {
            userRow = new UserRow();
            userRow.setDiscordId(discordId);
            userRow.setDiscordUsername(discordUsername);
            userRow.setFlags(0);
            userRepository.save(userRow);
        } else if(discordUsername!= null && !discordUsername.equals(userRow.getDiscordUsername())) {
            userRow.setDiscordUsername(discordUsername);
            userRepository.save(userRow);
        }

        return userRow;
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public User mergeUsers(final List<Integer> userIds) throws BotErrorException {
        Iterable<UserRow> userRows = userRepository.findAllById(userIds);

        UserRow mergedUser = userRows.iterator().next();
        List<UserRow> usersToDeleete = new ArrayList<>();

        for (UserRow userRow : userRows) {
            if (mergedUser.getTwitchId() == 0) {
                mergedUser.setTwitchId(userRow.getTwitchId());
            } else if (userRow.getTwitchId() != 0 && userRow.getTwitchId() != mergedUser.getTwitchId()) {
                throw new BotErrorException("Twitch Ids do not match");
            }

            if (mergedUser.getDiscordId() == 0) {
                mergedUser.setDiscordId(userRow.getDiscordId());
            } else if (userRow.getDiscordId() != 0 && userRow.getDiscordId() != mergedUser.getDiscordId()) {
                throw new BotErrorException("Discord Ids do not match");
            }

            if (mergedUser.getTwitchUsername() == null) {
                mergedUser.setTwitchUsername(userRow.getTwitchUsername());
            } else if (userRow.getTwitchUsername() != null
                    && !userRow.getTwitchUsername().equals(mergedUser.getTwitchUsername())) {
                throw new BotErrorException("Twitch usernames do not match");
            }

            if (mergedUser.getDiscordUsername() == null) {
                mergedUser.setDiscordUsername(userRow.getDiscordUsername());
            } else if (userRow.getDiscordUsername() != null
                    && !userRow.getDiscordUsername().equals(mergedUser.getDiscordUsername())) {
                throw new BotErrorException("Discord usernames do not match");
            }

            if (mergedUser.getArenaUsername() == null) {
                mergedUser.setArenaUsername(userRow.getArenaUsername());
            } else if (userRow.getArenaUsername() != null
                    && !userRow.getArenaUsername().equals(mergedUser.getArenaUsername())) {
                throw new BotErrorException("Arena usernames do not match");
            }

            if (userRow.getId() != mergedUser.getId()) {
                usersToDeleete.add(userRow);
            }
        }

        userRepository.save(mergedUser);
        userRepository.deleteAll(usersToDeleete);

        return createUser(mergedUser);
    }

    public List<User> getArenaUsers(final int botHomeId) throws BotErrorException {
        List<User> users = new ArrayList<>();
        for (UserRow userRow : userRepository.findByArenaUsernameIsNotNull()) {
            users.add(createUser(userRow));
        }
        return users;
    }

    private User createUser(final UserRow userRow) throws BotErrorException {
        return new User(userRow.getId(), userRow.getFlags(), userRow.getTwitchId(), userRow.getTwitchUsername(),
                        userRow.getDiscordId(), userRow.getDiscordUsername(), userRow.getArenaUsername());
    }

    private HomedUser createHomedUser(final User user, final UserStatus userStatus) throws BotErrorException {
        return new HomedUser(user, userStatus);
    }

    private List<HomedUser> createHomedUsers(final Iterable<User> users, final Map<Integer, UserHomeRow> homeRowById)
            throws BotErrorException {
        List<HomedUser> homedUsers = new ArrayList<>();
        for (User user : users) {
            UserHomeRow userHomeRow = homeRowById.get(user.getId());
            UserStatus userStatus;
            if (userHomeRow == null) {
                userStatus = new UserStatus();
            } else {
                userStatus = new UserStatus(userHomeRow.getState());
            }
            homedUsers.add(createHomedUser(user, userStatus));
        }
        return homedUsers;
    }
}
