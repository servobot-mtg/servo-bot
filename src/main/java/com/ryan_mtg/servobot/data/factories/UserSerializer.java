package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.UserHomeRow;
import com.ryan_mtg.servobot.data.models.UserRow;
import com.ryan_mtg.servobot.data.repositories.UserHomeRepository;
import com.ryan_mtg.servobot.data.repositories.UserRepository;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.user.User;
import com.ryan_mtg.servobot.user.UserHomeEdit;
import com.ryan_mtg.servobot.user.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final UserRepository userRepository;
    private final UserHomeRepository userHomeRepository;

    public UserSerializer(final UserRepository userRepository, final UserHomeRepository userHomeRepository) {
        this.userRepository = userRepository;
        this.userHomeRepository = userHomeRepository;
    }

    public Iterable<Integer> getAllUserIds() {
        return userRepository.getAllIds();
    }

    public Collection<User> getUsers(final Iterable<Integer> userIds) {
        Collection<User> users = new ArrayList<>();
        for (UserRow userRow : userRepository.findAllById(userIds)) {
            users.add(createUser(userRow));
        }
        return users;
    }

    public User lookupById(final int id) {
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

    @Transactional(rollbackOn = Exception.class)
    public User lookupByTwitchId(final int twitchId, final String twitchUsername) {
        UserRow userRow = lookupUserRowByTwitchId(twitchId, twitchUsername);
        return createUser(userRow);
    }

    @Transactional(rollbackOn = Exception.class)
    public User lookupByDiscordId(final long discordId, final String discordUsername) {
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
        userHomeRepository.save(createUserHomeRow(botHomeId, homedUser));
    }

    @Transactional(rollbackOn = Exception.class)
    public HomedUser lookup(final int botHomeId, final User user) {
        UserHomeRow userHomeRow = userHomeRepository.findByUserIdAndBotHomeId(user.getId(), botHomeId);
        UserStatus userStatus = userHomeRow != null ? new UserStatus(userHomeRow.getState()) : new UserStatus();
        return createHomedUser(user, userStatus);
    }

    @Transactional(rollbackOn = Exception.class)
    public Iterable<Integer> getAllHomedUserIds(final int botHomeId) {
        return userHomeRepository.getAllUserIds(botHomeId);
    }

    @Transactional(rollbackOn = Exception.class)
    public Iterable<Integer> getModeratorUserIds(final int botHomeId) {
        return StreamSupport.stream(userHomeRepository.findByBotHomeId(botHomeId).spliterator(), false)
            .filter(userHomeRow -> new UserStatus(userHomeRow.getState()).isModerator())
            .map(UserHomeRow::getUserId).collect(Collectors.toList());
    }

    @Transactional(rollbackOn = Exception.class)
    public List<HomedUser> getHomedUsers(final int botHomeId, final Iterable<User> users) {
        Iterable<Integer> userIds = StreamSupport.stream(users.spliterator(), false).map(User::getId)
                .collect(Collectors.toList());
        Iterable<UserHomeRow> userHomeRows = userHomeRepository.findByBotHomeIdAndUserIdIn(botHomeId, userIds);

        Map<Integer, UserHomeRow> homeRowById = SerializationSupport.getIdUniqueMapping(userHomeRows,
                UserHomeRow::getUserId);
        userHomeRows.forEach(userHomeRow -> homeRowById.put(userHomeRow.getUserId(), userHomeRow));

        return createHomedUsers(users, homeRowById);
    }

    @Transactional(rollbackOn = Exception.class)
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

    @Transactional(rollbackOn = Exception.class)
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

    public List<User> getArenaUsers(final int botHomeId) {
        List<User> users = new ArrayList<>();
        for (UserRow userRow : userRepository.findByArenaUsernameIsNotNull()) {
            users.add(createUser(userRow));
        }
        return users;
    }

    @Transactional(rollbackOn = Exception.class)
    public void commit(final UserHomeEdit userHomeEdit) {
        List<UserHomeRow> userHomeRows = new ArrayList<>();
        userHomeEdit.getSavedHomeUsers().forEach(
            (homedUser, botHomeId) -> userHomeRows.add(createUserHomeRow(botHomeId, homedUser))
        );
        userHomeRepository.saveAll(userHomeRows);

        userHomeEdit.getDeletedHomeUsers().forEach(userHomeId -> {
            userHomeRepository.deleteByBotHomeIdAndUserId(userHomeId.getBotHomeId(), userHomeId.getUserId());
        });
    }

    private User createUser(final UserRow userRow) {
        return SystemError.filter(() -> new User(userRow.getId(), userRow.getFlags(), userRow.getTwitchId(),
            userRow.getTwitchUsername(), userRow.getDiscordId(), userRow.getDiscordUsername(),
            userRow.getArenaUsername()));
    }

    private HomedUser createHomedUser(final User user, final UserStatus userStatus) {
        return new HomedUser(user, userStatus);
    }

    private List<HomedUser> createHomedUsers(final Iterable<User> users, final Map<Integer, UserHomeRow> homeRowById) {
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

    private UserHomeRow createUserHomeRow(final int botHomeId, final HomedUser homedUser) {
        UserHomeRow userHomeRow = new UserHomeRow();
        userHomeRow.setUserId(homedUser.getId());
        userHomeRow.setBotHomeId(botHomeId);
        userHomeRow.setState(homedUser.getUserStatus().getState());
        return userHomeRow;
    }
}
