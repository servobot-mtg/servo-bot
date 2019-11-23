package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.UserHomeRow;
import com.ryan_mtg.servobot.data.models.UserRow;
import com.ryan_mtg.servobot.data.repositories.BotHomeRepository;
import com.ryan_mtg.servobot.data.repositories.ServiceHomeRepository;
import com.ryan_mtg.servobot.data.repositories.UserHomeRepository;
import com.ryan_mtg.servobot.data.repositories.UserRepository;
import com.ryan_mtg.servobot.twitch.model.TwitchUserStatus;
import com.ryan_mtg.servobot.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserSerializer {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserHomeRepository userHomeRepository;

    @Autowired
    private BotHomeRepository botHomeRepository;

    @Autowired
    private ServiceHomeRepository serviceHomeRepository;

    public User lookupById(final int user_id) {
        UserRow userRow = userRepository.findById(user_id);

        if (userRow == null) {
            return null;
        }

        return createUser(userRow);
    }

    @Transactional
    public User lookupByTwitchId(final int twitchId, final String twitchUsername) {
        UserRow userRow = userRepository.findByTwitchId(twitchId);

        if (userRow == null) {
            userRow = new UserRow();
            userRow.setTwitchId(twitchId);
            userRow.setTwitchUsername(twitchUsername);
            userRepository.save(userRow);
        } else if(twitchUsername != null && !twitchUsername.equals(userRow.getTwitchUsername())) {
            userRow.setTwitchUsername(twitchUsername);
            userRepository.save(userRow);
        }

        return createUser(userRow);
    }

    public List<Integer> getHomesModerated(final int userId) {
        List<UserHomeRow> userHomeRows = userHomeRepository.findByUserId(userId);
        return userHomeRows.stream().filter(userHomeRow -> new TwitchUserStatus(userHomeRow.getState()).isModerator())
                .map(userHomeRow -> userHomeRow.getBotHomeId()).collect(Collectors.toList());
    }

    private User createUser(final UserRow userRow)  {
        return new User(userRow.getId(), userRow.getTwitchId(), userRow.getTwitchUsername(), userRow.getDiscordId());
    }

    @Transactional
    public void updateStatus(final User user, final int botHomeId, final TwitchUserStatus status) {
        int state = status.getState();
        UserHomeRow userHomeRow = userHomeRepository.findByUserIdAndBotHomeId(user.getId(), botHomeId);

        if (userHomeRow == null) {
            if (state == 0 ) {
                return;
            }

            userHomeRow = new UserHomeRow();
            userHomeRow.setUserId(user.getId());
            userHomeRow.setBotHomeId(botHomeId);
            userHomeRow.setState(state);
        } else {
            if (state == 0) {
                userHomeRepository.deleteByUserIdAndBotHomeId(user.getId(), botHomeId);
                return;
            }

            if (state == userHomeRow.getState()) {
                return;
            }

            userHomeRow.setState(state);
        }

        userHomeRepository.save(userHomeRow);

    }
}
