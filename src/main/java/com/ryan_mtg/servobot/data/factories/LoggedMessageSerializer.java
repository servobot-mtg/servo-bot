package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.LoggedMessageRow;
import com.ryan_mtg.servobot.data.repositories.LoggedMessageRepository;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.LoggedMessage;
import com.ryan_mtg.servobot.user.User;
import com.ryan_mtg.servobot.user.UserTable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ryan_mtg.servobot.model.LoggedMessage.FROM_BOT;
import static com.ryan_mtg.servobot.model.LoggedMessage.TO_BOT;

@Component
public class LoggedMessageSerializer {
    private final LoggedMessageRepository loggedMessageRepository;
    private final UserTable userTable;

    public LoggedMessageSerializer(final LoggedMessageRepository loggedMessageRepository, final UserTable userTable) {
        this.loggedMessageRepository = loggedMessageRepository;
        this.userTable = userTable;
    }

    public void logReceivedMessage(final User sender, final String message, final int serviceType) {
        logMessage(sender, message, serviceType, TO_BOT);
    }

    public void logSentMessage(final User receiver, final String message, final int serviceType) {
        logMessage(receiver, message, serviceType, FROM_BOT);
    }

    public List<LoggedMessage> getLoggedMessages() throws BotErrorException {
        Iterable<LoggedMessageRow> loggedMessageRows = loggedMessageRepository.findAll();

        List<Integer> userIds = new ArrayList<>();
        for (LoggedMessageRow loggedMessageRow : loggedMessageRows) {
            userIds.add(loggedMessageRow.getUserId());
        }

        Map<Integer, User> userMap = new HashMap<>();
        for (User user : userTable.getUsers(userIds)) {
            userMap.put(user.getId(), user);
        }

        List<LoggedMessage> messages = new ArrayList<>();
        for (LoggedMessageRow loggedMessageRow : loggedMessageRows) {
            Instant sentTime = Instant.ofEpochMilli(loggedMessageRow.getSentTime());
            messages.add(new LoggedMessage(userMap.get(loggedMessageRow.getUserId()), loggedMessageRow.getDirection(),
                    loggedMessageRow.getMessage(), loggedMessageRow.getServiceType(), sentTime));
        }
        return messages;
    }

    private void logMessage(final User receiver, final String message, final int serviceType, final int direction) {
        LoggedMessageRow loggedMessageRow = new LoggedMessageRow();
        loggedMessageRow.setUserId(receiver.getId());
        loggedMessageRow.setMessage(message);
        loggedMessageRow.setServiceType(serviceType);
        loggedMessageRow.setSentTime(Instant.now().getEpochSecond());
        loggedMessageRow.setDirection(direction);
        loggedMessageRepository.save(loggedMessageRow);
    }
}
