package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.data.factories.SerializerContainer;
import com.ryan_mtg.servobot.data.models.GameQueueEntryRow;
import com.ryan_mtg.servobot.data.models.GameQueueRow;
import com.ryan_mtg.servobot.data.models.GiveawayRow;
import com.ryan_mtg.servobot.data.models.PrizeRow;
import com.ryan_mtg.servobot.data.models.StorageValueRow;
import com.ryan_mtg.servobot.data.models.UserHomeRow;
import com.ryan_mtg.servobot.data.repositories.UserHomeRepository;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.game_queue.GameQueue;
import com.ryan_mtg.servobot.model.game_queue.GameQueueEdit;
import com.ryan_mtg.servobot.model.giveaway.Giveaway;
import com.ryan_mtg.servobot.model.giveaway.GiveawayEdit;
import com.ryan_mtg.servobot.model.storage.StorageTable;
import com.ryan_mtg.servobot.model.storage.StorageTableEdit;
import com.ryan_mtg.servobot.user.HomedUserTable;
import com.ryan_mtg.servobot.user.User;
import com.ryan_mtg.servobot.user.UserHomeEdit;
import com.ryan_mtg.servobot.user.UserStatus;
import com.ryan_mtg.servobot.user.UserTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class SystemEditor {
    private final BotRegistrar botRegistrar;
    private final SerializerContainer serializers;

    public SystemEditor(final BotRegistrar botRegistrar, final SerializerContainer serializers) {
        this.botRegistrar = botRegistrar;
        this.serializers = serializers;
    }

    @Transactional(rollbackOn = Exception.class)
    public User mergeUsers(final List<Integer> userIds) throws UserError {
        UserTable userTable = serializers.getUserTable();
        Iterable<User> users = userTable.getUsers(userIds);

        UserMerge merge = attemptUserMerge(users);
        userTable.purge(merge.getUsersToDelete());
        userTable.update(merge.getMergedUser());

        UserHomeEdit userHomeEdit = mergeHomedUser(userIds, merge);
        serializers.getUserSerializer().commit(userHomeEdit);

        GameQueueEdit gameQueueEdit = mergeGameQueueUser(merge);
        serializers.getGameQueueSerializer().commit(gameQueueEdit);

        StorageTableEdit storageTableEdit = mergeStorageUser(merge);
        serializers.getStorageTableSerializer().commit(storageTableEdit);

        // update giveaways
        GiveawayEdit giveawayEdit = mergeGiveawayUser(merge);
        serializers.getGiveawaySerializer().commit(giveawayEdit);

        return merge.getMergedUser();
    }

    private UserMerge attemptUserMerge(final Iterable<User> users) throws UserError {
        List<Integer> usersToDelete = new ArrayList<>();

        int id = users.iterator().next().getId(), flags = 0, twitchId = 0;
        long discordId = 0;
        String twitchUsername = null, discordUsername = null, arenaUsername = null;

        for (User user : users) {
            if (id != user.getId()) {
                usersToDelete.add(Math.max(id, user.getId()));
                id = Math.min(id, user.getId());
            }

            flags |= user.getFlags();

            twitchId = updateMergedUserField(twitchId, user.getTwitchId(), 0, "Twitch IDs");
            discordId = updateMergedUserField(discordId, user.getDiscordId(), 0L, "Discord IDs");

            twitchUsername = updateMergedUserField(twitchUsername, user.getTwitchUsername(), null, "Twitch Usernames");
            discordUsername = updateMergedUserField(discordUsername, user.getDiscordUsername(), null, "Discord Usernames");
            arenaUsername = updateMergedUserField(arenaUsername, user.getArenaUsername(), null, "Arena Usernames");
        }

        User mergedUser = new User(id, flags, twitchId, twitchUsername, discordId, discordUsername, arenaUsername);
        return new UserMerge(mergedUser, usersToDelete);
    }

    private UserHomeEdit mergeHomedUser(final List<Integer> userIds, final UserMerge merge) {
        UserHomeRepository userHomeRepository = serializers.getUserHomeRepository();
        Iterable<UserHomeRow> userHomeRows = userHomeRepository.findByUserIdIn(userIds);
        Map<Integer, List<UserHomeRow>> usersByHomeId = new HashMap<>();
        userHomeRows.forEach(userHomeRow ->
                usersByHomeId.computeIfAbsent(userHomeRow.getBotHomeId(), botHomeId -> new ArrayList<>()).add(userHomeRow)
        );

        UserHomeEdit userHomeEdit = new UserHomeEdit();
        usersByHomeId.forEach((botHomeId, userHowRows) -> {
            UserStatus mergedStatus = new UserStatus();
            List<Integer> userIdsToDelete = new ArrayList<>();
            userHomeRows.forEach(userHomeRow -> {
                mergedStatus.merge(userHomeRow.getState());
                if (userHomeRow.getUserId() != merge.getMergedUser().getId()) {
                    userIdsToDelete.add(userHomeRow.getUserId());
                }
            });
            HomedUserTable homedUserTable = botRegistrar.getBotHome(botHomeId).getHomedUserTable();
            userHomeEdit.merge(homedUserTable.update(merge.getMergedUser(), mergedStatus));
            userHomeEdit.merge(homedUserTable.purge(userIdsToDelete));
        });
        return userHomeEdit;
    }

    private GameQueueEdit mergeGameQueueUser(final UserMerge merge) {
        Iterable<GameQueueEntryRow> gameQueueEntryRows =
                serializers.getGameQueueEntryRepository().findAllByUserIdIn(merge.getUsersToDelete());

        List<Integer> gameQueueIds = StreamSupport.stream(gameQueueEntryRows.spliterator(), false)
                .map(GameQueueEntryRow::getGameQueueId).collect(Collectors.toList());
        Iterable<GameQueueRow> gameQueueRows = serializers.getGameQueueRepository().findAllByIdIn(gameQueueIds);

        Map<Integer, List<Integer>> botHomeIdToGameQueueIdsMap =
                constructMap(gameQueueRows, GameQueueRow::getBotHomeId, GameQueueRow::getId);

        GameQueueEdit gameQueueEdit = new GameQueueEdit();
        int mergedUserId = merge.getMergedUser().getId();
        for(Map.Entry<Integer, List<Integer>> entry : botHomeIdToGameQueueIdsMap.entrySet()) {
            int botHomeId = entry.getKey();
            BotHome botHome = botRegistrar.getBotHome(botHomeId);
            for (int gameQueueId : entry.getValue()) {
                GameQueue gameQueue = botHome.getGameQueueTable().getGameQueue(gameQueueId);
                gameQueueEdit.merge(gameQueue.mergeUser(mergedUserId, merge.getUsersToDelete()));
            }
        }
        return gameQueueEdit;
    }

    private StorageTableEdit mergeStorageUser(final UserMerge merge) {
        Iterable<StorageValueRow> storageValueRows =
                serializers.getStorageValueRepository().findAllByUserId(merge.getUsersToDelete());

        Map<Integer, List<StorageTable.StorageKey>> botHomeIdToStorageKeysMap =
                constructMap(storageValueRows, StorageValueRow::getBotHomeId, storageValueRow ->
                        new StorageTable.StorageKey(storageValueRow.getUserId(), storageValueRow.getName()));

        StorageTableEdit storageTableEdit = new StorageTableEdit();
        int mergedUserId = merge.getMergedUser().getId();
        for(Map.Entry<Integer, List<StorageTable.StorageKey>> entry : botHomeIdToStorageKeysMap.entrySet()) {
            int botHomeId = entry.getKey();
            BotHome botHome = botRegistrar.getBotHome(botHomeId);
            storageTableEdit.merge(botHome.getStorageTable().mergeUser(botHomeId, mergedUserId, entry.getValue()));
        }
        return storageTableEdit;
    }

    private GiveawayEdit mergeGiveawayUser(final UserMerge merge) {
        Iterable<PrizeRow> prizeRows = serializers.getPrizeRepository().findAllByWinnerId(merge.getUsersToDelete());
        List<Integer> giveawayIds = StreamSupport.stream(prizeRows.spliterator(), false)
                .map(PrizeRow::getGiveawayId).collect(Collectors.toList());
        Iterable<GiveawayRow> giveawayRows =
                serializers.getGiveawayRepository().findAllByIdIn(giveawayIds);
        Map<Integer, List<Integer>> botHomeIdToGiveawayIdsMap =
                constructMap(giveawayRows, GiveawayRow::getBotHomeId, GiveawayRow::getId);

        GiveawayEdit giveawayEdit = new GiveawayEdit();
        int mergedUserId = merge.getMergedUser().getId();
        for(Map.Entry<Integer, List<Integer>> entry : botHomeIdToGiveawayIdsMap.entrySet()) {
            int botHomeId = entry.getKey();
            BotHome botHome = botRegistrar.getBotHome(botHomeId);
            for (int giveawayId : entry.getValue()) {
                Giveaway giveaway = botHome.getGiveaway(giveawayId);
                giveawayEdit.merge(
                        giveaway.mergeUser(botHome.getHomedUserTable(), mergedUserId, merge.getUsersToDelete()));
            }
        }
        return giveawayEdit;
    }

    @Data @AllArgsConstructor
    private class UserMerge {
        private User mergedUser;
        private List<Integer> usersToDelete;
    }

    private <FieldType> FieldType updateMergedUserField(final FieldType mergedField, final FieldType newField,
            final FieldType nullValue, final String fieldDescription) throws UserError {
        if (mergedField == nullValue) {
            return newField;
        } else if (newField == nullValue) {
            return mergedField;
        } else if (!mergedField.equals(newField)) {
            throw new UserError("%s do not match", fieldDescription);
        }
        return mergedField;
    }

    private <T, K, V> Map<K, List<V>> constructMap(final Iterable<T> iterable, final Function<T, K> keyExtractor,
            final Function<T,V> valueExtractor) {
        Map<K, List<V>> map = new HashMap<>();
        for (T t : iterable) {
            K key = keyExtractor.apply(t);
            V value = valueExtractor.apply(t);
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }
        return map;
    }
}
