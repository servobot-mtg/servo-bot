package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.UserHomeRow;
import com.ryan_mtg.servobot.data.models.UserRow;
import com.ryan_mtg.servobot.data.repositories.UserHomeRepository;
import com.ryan_mtg.servobot.data.repositories.UserRepository;
import com.ryan_mtg.servobot.discord.model.DiscordUserStatus;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.twitch.model.TwitchUserStatus;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.user.User;
import com.ryan_mtg.servobot.user.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.ryan_mtg.servobot.user.User.INVITE_FLAG;

@Component
public class UserSerializer {
    private static Logger LOGGER = LoggerFactory.getLogger(UserSerializer.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserHomeRepository userHomeRepository;

    public List<User> getAllUsers() throws BotErrorException {
        List<User> users = new ArrayList<>();
        for (UserRow userRow : userRepository.findAll(Sort.by("twitchUsername"))) {
            users.add(createUser(userRow));
        }
        return users;
    }

    public User lookupById(final int id) throws BotErrorException {
        return createUser(userRepository.findById(id));
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public User lookupByTwitchId(final int twitchId, final String twitchUsername) {
        try {
            UserRow userRow = lookupUserRowByTwitchId(twitchId, twitchUsername);
            return createUser(userRow);
        } catch (BotErrorException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public HomedUser lookupByTwitchId(final int botHomeId, final int twitchId, final String twitchUsername,
                                      final TwitchUserStatus twitchUserStatus) throws BotErrorException {
        UserRow userRow = lookupUserRowByTwitchId(twitchId, twitchUsername);

        UserStatus userStatus = updateStatus(userRow.getId(), botHomeId, twitchUserStatus);
        return createHomedUser(userRow, userStatus);
    }

    public HomedUser lookupByDiscordId(final int botHomeId, final long discordId, final String discordUsername,
                                       final DiscordUserStatus discordUserStatus) throws BotErrorException {
        UserRow userRow = userRepository.findByDiscordId(discordId);

        if (userRow == null) {
            userRow = new UserRow();
            userRow.setDiscordId(discordId);
            userRow.setDiscordUsername(discordUsername);
            userRow.setFlags(0);
            userRepository.save(userRow);
        } else if (discordUsername != null && !discordUsername.equals(userRow.getDiscordUsername())) {
            userRow.setDiscordUsername(discordUsername);
            userRepository.save(userRow);
        }

        UserStatus userStatus = updateStatus(userRow.getId(), botHomeId, discordUserStatus);
        return createHomedUser(userRow, userStatus);
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

    private UserStatus updateStatus(final int userId, final int botHomeId, final Consumer<UserStatus> mergeFunction) {
        UserHomeRow userHomeRow = userHomeRepository.findByUserIdAndBotHomeId(userId, botHomeId);

        boolean save = false;

        if (userHomeRow == null) {
            userHomeRow = new UserHomeRow();
            userHomeRow.setUserId(userId);
            userHomeRow.setBotHomeId(botHomeId);
            userHomeRow.setState(0);
            save = true;
        }

        UserStatus userStatus = new UserStatus(userHomeRow.getState());

        mergeFunction.accept(userStatus);

        if (userStatus.getState() != userHomeRow.getState() || save) {
            userHomeRow.setState(userStatus.getState());
            userHomeRepository.save(userHomeRow);
        }

        return userStatus;
    }

    public void setStreamerStatus(final int userId, final int botHomeId) {
        TwitchUserStatus twitchUserStatus = new TwitchUserStatus(true, false, false, true);
        updateStatus(userId, botHomeId, twitchUserStatus);
    }

    private UserStatus updateStatus(final int userId, final int botHomeId, final TwitchUserStatus twitchStatus) {
        return updateStatus(userId, botHomeId, userStatus -> userStatus.merge(twitchStatus));
    }

    private UserStatus updateStatus(final int userId, final int botHomeId, final DiscordUserStatus discordUserStatus) {
        return updateStatus(userId, botHomeId, userStatus -> userStatus.merge(discordUserStatus));
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public HomedUser getHomedUser(final int botHomeId, final int winnerId) throws BotErrorException {
        UserHomeRow userHomeRow = userHomeRepository.findByUserIdAndBotHomeId(winnerId, botHomeId);

        UserRow userRow = userRepository.findById(winnerId);
        return createHomedUser(userRow, new UserStatus(userHomeRow.getState()));
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public List<HomedUser> getHomedUsers(final int botHomeId) throws BotErrorException {
        return getHomedUsersWithFilter(botHomeId, userHomeRow -> true);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public List<HomedUser> getModerators(final int botHomeId) throws BotErrorException {
        return getHomedUsersWithFilter(botHomeId, userHomeRow -> new UserStatus(userHomeRow.getState()).isModerator());
    }

    private List<HomedUser> getHomedUsersWithFilter(final int botHomeId,
            final Predicate<UserHomeRow> filterFunction) throws BotErrorException {

        List<HomedUser> users = new ArrayList<>();
        List<UserHomeRow> userHomeRows = userHomeRepository.findByBotHomeId(botHomeId);
        Map<Integer, UserHomeRow> homeRowById = new HashMap<>();
        List<Integer> userIds = userHomeRows.stream().filter(filterFunction).map(userHomeRow -> {
            homeRowById.put(userHomeRow.getUserId(), userHomeRow);
            return userHomeRow.getUserId();
        }).collect(Collectors.toList());

        for (UserRow userRow : userRepository.findAllById(userIds)) {
            UserHomeRow userHomeRow = homeRowById.get(userRow.getId());
            users.add(createHomedUser(userRow, new UserStatus(userHomeRow.getState())));
        }
        return users;
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
    public void inviteUser(final int userId) {
        UserRow userRow = userRepository.findById(userId);
        int newFlags = userRow.getFlags() | INVITE_FLAG;
        userRow.setFlags(newFlags);
        userRepository.save(userRow);
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

    @Transactional(rollbackOn = BotErrorException.class)
    public void setArenaUsername(final int id, final String arenaUsername) {
        UserRow userRow = userRepository.findById(id);
        userRow.setArenaUsername(arenaUsername);
        userRepository.save(userRow);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void deleteArenaUsername(final int id) {
        UserRow userRow = userRepository.findById(id);
        userRow.setArenaUsername(null);
        userRepository.save(userRow);
    }

    public List<User> getArenaUsers() throws BotErrorException {
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

    private HomedUser createHomedUser(final UserRow userRow, final UserStatus userStatus) throws BotErrorException {
        return new HomedUser(userRow.getId(), userRow.getFlags(), userRow.getTwitchId(), userRow.getTwitchUsername(),
                userRow.getDiscordId(), userRow.getDiscordUsername(), userRow.getArenaUsername(), userStatus);
    }
}
