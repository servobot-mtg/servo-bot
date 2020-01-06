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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class UserSerializer {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserHomeRepository userHomeRepository;

    public List<User> getAllUsers() {
        return StreamSupport.stream(userRepository.findAll(Sort.by("twitchUsername")).spliterator(), false)
                .map(userRow -> createUser(userRow)).collect(Collectors.toList());
    }

    public User lookupById(final int id) {
        return createUser(userRepository.findById(id));
    }

    @Transactional
    public User lookupByTwitchId(final int twitchId, final String twitchUsername) {
        UserRow userRow = lookupUserRowByTwitchId(twitchId, twitchUsername);
        return createUser(userRow);
    }

    @Transactional
    public HomedUser lookupByTwitchId(final int botHomeId, final int twitchId, final String twitchUsername,
                                      final TwitchUserStatus twitchUserStatus) {
        UserRow userRow = lookupUserRowByTwitchId(twitchId, twitchUsername);

        UserStatus userStatus = updateStatus(userRow.getId(), botHomeId, twitchUserStatus);
        return createHomedUser(userRow, userStatus);
    }

    public HomedUser lookupByDiscordId(final int botHomeId, final long discordId, final String discordUsername,
                                       final DiscordUserStatus discordUserStatus) {
        UserRow userRow = userRepository.findByDiscordId(discordId);

        if (userRow == null) {
            userRow = new UserRow();
            userRow.setDiscordId(discordId);
            userRow.setDiscordUsername(discordUsername);
            userRow.setAdmin(false);
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
        return userHomeRows.stream().filter(userHomeRow -> new TwitchUserStatus(userHomeRow.getState()).isModerator())
                .map(userHomeRow -> userHomeRow.getBotHomeId()).collect(Collectors.toList());
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

    private UserStatus updateStatus(final int userId, final int botHomeId, final TwitchUserStatus twitchStatus) {
        return updateStatus(userId, botHomeId, userStatus -> userStatus.merge(twitchStatus));
    }

    private UserStatus updateStatus(final int userId, final int botHomeId, final DiscordUserStatus discordUserStatus) {
        return updateStatus(userId, botHomeId, userStatus -> userStatus.merge(discordUserStatus));
    }

    public List<HomedUser> getHomedUsers(final int botHomeId) {
        List<UserHomeRow> userHomeRows = userHomeRepository.findByBotHomeId(botHomeId);
        return userHomeRows.stream().map(userHomeRow -> {
            UserRow userRow = userRepository.findById(userHomeRow.getUserId());
            return createHomedUser(userRow, new UserStatus(userHomeRow.getState()));
        }).collect(Collectors.toList());
    }

    public List<HomedUser> getModerators(final int botHomeId) {
        List<UserHomeRow> userHomeRows = userHomeRepository.findByBotHomeId(botHomeId);
        return userHomeRows.stream().map(userHomeRow -> {
            UserRow userRow = userRepository.findById(userHomeRow.getUserId());
            return createHomedUser(userRow, new UserStatus(userHomeRow.getState()));
        }).filter(user -> user.isModerator()).collect(Collectors.toList());
    }

    @Transactional
    public UserRow lookupUserRowByTwitchId(final int twitchId, final String twitchUsername) {
        UserRow userRow = userRepository.findByTwitchId(twitchId);

        if (userRow == null) {
            userRow = new UserRow();
            userRow.setTwitchId(twitchId);
            userRow.setTwitchUsername(twitchUsername);
            userRow.setAdmin(false);
            userRepository.save(userRow);
        } else if(twitchUsername != null && !twitchUsername.equals(userRow.getTwitchUsername())) {
            userRow.setTwitchUsername(twitchUsername);
            userRepository.save(userRow);
        }

        return userRow;
    }

    @Transactional
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

    @Transactional
    public void setArenaUsername(final int id, final String arenaUsername) {
        UserRow userRow = userRepository.findById(id);
        userRow.setArenaUsername(arenaUsername);
        userRepository.save(userRow);
    }

    @Transactional
    public void deleteArenaUsername(final int id) {
        UserRow userRow = userRepository.findById(id);
        userRow.setArenaUsername(null);
        userRepository.save(userRow);
    }

    public List<User> getArenaUsers() {
        List<UserRow> userRows = userRepository.findByArenaUsernameIsNotNull();
        return userRows.stream().map(userRow -> createUser(userRow)).collect(Collectors.toList());
    }

    private User createUser(final UserRow userRow)  {
        return new User(userRow.getId(), userRow.isAdmin(), userRow.getTwitchId(), userRow.getTwitchUsername(),
                        userRow.getDiscordId(), userRow.getDiscordUsername(), userRow.getArenaUsername());
    }

    private HomedUser createHomedUser(final UserRow userRow, final UserStatus userStatus)  {
        return new HomedUser(userRow.getId(), userRow.isAdmin(), userRow.getTwitchId(), userRow.getTwitchUsername(),
                userRow.getDiscordId(), userRow.getDiscordUsername(), userRow.getArenaUsername(), userStatus);
    }
}
