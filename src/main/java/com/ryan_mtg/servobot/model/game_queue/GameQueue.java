package com.ryan_mtg.servobot.model.game_queue;

import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.user.HomedUserTable;
import com.ryan_mtg.servobot.utility.Flags;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameQueue {
    public static final int UNREGISTERED_ID = 0;

    private static int ON_BETA_FLAG = 1;

    @Getter @Setter
    private int id;

    @Getter
    private Game game;

    @Getter @Setter
    private int flags;

    @Getter @Setter
    private Integer maxPlayers;

    @Getter @Setter
    private Integer minPlayers;

    @Getter @Setter
    private String code;

    @Getter @Setter
    private String server;

    @Getter @Setter
    private String gamerTagVariable;

    @Getter @Setter
    private String proximityServer;

    @Getter @Setter
    private State state;

    @Getter @Setter
    private Instant startTime;

    @Getter @Setter
    private Message message;

    private List<GameQueueEntry> playing = new ArrayList<>();
    private List<GameQueueEntry> waitQueue = new ArrayList<>();
    private List<GameQueueEntry> rsvped = new ArrayList<>();
    private List<GameQueueEntry> onDeck = new ArrayList<>();
    private Map<Integer, GameQueueEntry> userMap = new HashMap<>();

    public enum State {
        IDLE,
        PLAYING,
        CLOSED,
    }

    public GameQueue(final int id, final Game game, final int flags, final int minPlayers, final int maxPlayers,
             final State state, final String code, final String server, final String proximityServer,
             final String gamerTagVariable, final Instant startTime, final Message message,
             final List<GameQueueEntry> gameQueueEntries) throws UserError {
        this.id = id;
        this.flags = flags;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.game = game;
        this.state = state;
        this.code = code;
        this.server = server;
        this.proximityServer = proximityServer;
        this.gamerTagVariable = gamerTagVariable;
        this.startTime = startTime;
        this.message = message;

        Validation.validateStringLength(code, Validation.MAX_NAME_LENGTH, "Code");
        Validation.validateStringLength(server, Validation.MAX_NAME_LENGTH, "Server");
        Validation.validateStringLength(proximityServer, Validation.MAX_CHANNEL_NAME_LENGTH, "Proximity Server");

        for (GameQueueEntry gameQueueEntry : gameQueueEntries) {
            userMap.put(gameQueueEntry.getUser().getId(), gameQueueEntry);
            switch (gameQueueEntry.getState()) {
                case WAITING:
                case ON_CALL:
                    waitQueue.add(gameQueueEntry);
                    break;
                case ON_DECK:
                    onDeck.add(gameQueueEntry);
                    break;
                case PERMANENT:
                case PLAYING:
                case LG:
                    playing.add(gameQueueEntry);
                    break;
                case RSVPED:
                case RSVP_EXPIRED:
                    rsvped.add(gameQueueEntry);
                    break;
            }
        }
    }

    public boolean isOnBeta() {
        return Flags.hasFlag(flags, ON_BETA_FLAG);
    }

    public void setVersion(final boolean onBeta) {
        flags = Flags.setFlag(flags, ON_BETA_FLAG, onBeta);
    }

    public boolean matches(final Message message) {
        return message.getChannelId() == this.message.getChannelId() && message.getId() == this.message.getId();
    }

    public boolean isLg(final HomedUser player) {
        return isStatus(player, PlayerState.LG);
    }

    public boolean isOnCall(final HomedUser player) {
        return isStatus(player, PlayerState.ON_CALL);
    }

    public boolean isPermanent(final HomedUser player) {
        return isStatus(player, PlayerState.PERMANENT);
    }

    private boolean isStatus(final HomedUser player, final PlayerState state) {
        int playerId = player.getId();
        if (userMap.containsKey(playerId)) {
            return userMap.get(playerId).getState() == state;
        }
        return false;
    }

    public Instant getRsvpTime(final HomedUser player) throws UserError {
        int playerId = player.getId();
        if (!userMap.containsKey(playerId)) {
            throw new UserError("%s is not in the queue.", player.getName());
        }

        GameQueueEntry entry = userMap.get(playerId);

        if (entry.getState() != PlayerState.RSVPED && entry.getState() != PlayerState.RSVP_EXPIRED) {
            throw new UserError("%s has not made a reservation.", player.getName());
        }

        return entry.getEnqueueTime();
    }

    public GameQueueAction setMinimumPlayers(final int botHomeId, final GameQueueEdit edit, final int minimum,
            final boolean check) throws UserError {
        if (minimum < 0) {
            throw new UserError("Minimum number of players must be positive.");
        }
        if (check && minimum > getMaxPlayers()) {
            throw new UserError(
                "Minimum number of players must be less than or equal to the maximum number of players %d.",
                getMaxPlayers());
        }

        this.minPlayers = minimum;
        edit.save(botHomeId, this);
        GameQueueAction action = GameQueueAction.setMinimumPlayers(minimum);
        promotePlayersToOnDeck(botHomeId, edit, action);
        return action;
    }

    public GameQueueAction setMaximumPlayers(final int botHomeId, final GameQueueEdit edit, final int maximum,
            final boolean check) throws UserError {
        if (check && maximum < getMinPlayers()) {
            throw new UserError(
                    "Maximum number of players must be greater than or equal to the minimum number of players %d.",
                    getMinPlayers());
        }

        this.maxPlayers = maximum;
        edit.save(botHomeId, this);
        GameQueueAction action = GameQueueAction.setMaximumPlayers(maximum);
        promotePlayersToOnDeck(botHomeId, edit, action);
        return action;
    }

    public GameQueueAction start(final int botHomeId, final GameQueueEdit edit) throws UserError {
        if (startTime == null) {
            throw new UserError("No game was scheduled.");
        }

        startTime = null;
        edit.save(botHomeId, this);
        GameQueueAction action = GameQueueAction.gameStarted();
        promotePlayersToOnDeck(botHomeId, edit, action);
        return action;
    }

    public GameQueueAction enqueue(final int botHomeId, final HomedUser player, final GameQueueEdit edit)
            throws UserError {
        int playerId = player.getId();
        if (userMap.containsKey(playerId)) {
            GameQueueEntry entry = userMap.get(playerId);

            if (entry.getState() == PlayerState.RSVPED) {

                edit.save(getId(), entry);
                GameQueueAction action = GameQueueAction.playerQueued(player);
                promotePlayersToOnDeck(botHomeId, edit, action);
                checkForRsvpExpirations(edit, action);
                return action;
            } else {
                throw new UserError("%s is already queued.", player.getName());
            }
        }

        GameQueueEntry newEntry = new GameQueueEntry(player, Instant.now(), PlayerState.WAITING);
        edit.save(getId(), newEntry);
        waitQueue.add(newEntry);
        userMap.put(player.getId(), newEntry);

        GameQueueAction action = GameQueueAction.playerQueued(player);
        promotePlayersToOnDeck(botHomeId, edit, action);
        checkForRsvpExpirations(edit, action);
        return action;
    }

    public GameQueueAction dequeue(final int botHomeId, final HomedUser player, final GameQueueEdit edit)
            throws UserError {
        int playerId = player.getId();
        if (!userMap.containsKey(playerId)) {
            throw new UserError("%s is not in the queue.", player.getName());
        }

        removeEntry(edit, playerId);
        GameQueueAction action = GameQueueAction.playerDequeued(player);
        promotePlayersToOnDeck(botHomeId, edit, action);
        checkForRsvpExpirations(edit, action);

        return action;
    }

    public GameQueueAction rotate(final int botHomeId, final HomedUser player, final GameQueueEdit edit)
            throws UserError {
        int playerId = player.getId();
        if (!userMap.containsKey(playerId)) {
            return enqueue(botHomeId, player, edit);
        }

        GameQueueEntry entry = userMap.get(playerId);
        removeFromLists(entry);
        entry.setState(PlayerState.WAITING);
        entry.setEnqueueTime(Instant.now());
        waitQueue.add(entry);
        edit.save(getId(), entry);
        GameQueueAction action = GameQueueAction.playerQueued(player);
        promotePlayersToOnDeck(botHomeId, edit, action);
        checkForRsvpExpirations(edit, action);

        return action;
    }

    public GameQueueAction rotateLg(final int botHomeId, final GameQueueEdit edit) throws UserError {
        List<GameQueueEntry> playersToRotate = new ArrayList<>();
        for (GameQueueEntry entry : playing) {
            if (entry.getState() == PlayerState.LG) {
                playersToRotate.add(entry);
            }
        }

        if (playing.isEmpty()) {
            throw new UserError("There is no one playing.");
        } else if (playersToRotate.isEmpty()) {
            throw new UserError("No players have been marked LG. ");
        }

        GameQueueAction action = GameQueueAction.emptyAction();
        for (GameQueueEntry entry : playersToRotate) {
            removeFromLists(entry);
            entry.setState(PlayerState.WAITING);
            entry.setEnqueueTime(Instant.now());
            waitQueue.add(entry);
            edit.save(getId(), entry);
            action.merge(GameQueueAction.playerQueued(entry.getUser()));
        }

        promotePlayersToOnDeck(botHomeId, edit, action);
        checkForRsvpExpirations(edit, action);
        return action;
    }

    public GameQueueEdit move(final HomedUser player, final int position) throws UserError {
        int playerId = player.getId();
        GameQueueEntry entry = userMap.get(playerId);
        if (!userMap.containsKey(playerId) || entry.getState() != PlayerState.WAITING) {
            throw new UserError("%s is not in the queue.", player.getName());
        }

        if (position < 1) {
            throw new UserError("Position %d is too low.", position);
        }

        if (position > waitQueue.size()) {
            throw new UserError("Position %d is too high.", position);
        }

        if (waitQueue.get(position-1).getUser().getId() == playerId) {
            throw new UserError("%s is already in position %d.", player.getName(), position);
        }

        GameQueueEdit gameQueueEdit = new GameQueueEdit();

        if (position == 1) {
            entry.setEnqueueTime(Instant.ofEpochMilli(waitQueue.get(0).getEnqueueTime().toEpochMilli() - 512));
        } else if (position == waitQueue.size()) {
            entry.setEnqueueTime(Instant.ofEpochMilli(waitQueue.get(position-1).getEnqueueTime().toEpochMilli() + 512));
        } else {
            long beforeTime = waitQueue.get(position-2).getEnqueueTime().toEpochMilli();
            long afterTime = waitQueue.get(position-1).getEnqueueTime().toEpochMilli();
            long setTime = (beforeTime + afterTime) / 2;
            entry.setEnqueueTime(Instant.ofEpochMilli(setTime));
        }
        Collections.sort(waitQueue);
        gameQueueEdit.save(getId(), entry);

        return gameQueueEdit;
    }

    public GameQueueAction ready(final HomedUser player, final GameQueueEdit edit) throws UserError {
        int playerId = player.getId();
        if (!userMap.containsKey(playerId) || userMap.get(playerId).getState() != PlayerState.ON_DECK) {
            throw new UserError("%s is not on deck.", player.getName());
        }

        if (getPlayingCount() >= getMaxPlayers()) {
            throw new UserError("Not enough room in the game for %s.", player.getName());
        }

        GameQueueEntry entry = userMap.get(playerId);
        entry.setState(PlayerState.PLAYING);
        edit.save(getId(), entry);
        playing.add(entry);
        onDeck.remove(entry);
        GameQueueAction action = GameQueueAction.playerReadied(player);
        checkForRsvpExpirations(edit, action);

        return action;
    }

    public GameQueueAction unready(final HomedUser player, final GameQueueEdit edit) throws UserError {
        int playerId = player.getId();
        if (!userMap.containsKey(playerId) || !userMap.get(playerId).getState().isReady()) {
            throw new UserError("%s is neither playing nor on deck.", player.getName());
        }

        GameQueueEntry entry = userMap.get(playerId);
        removeFromLists(entry);
        entry.setState(PlayerState.WAITING);
        waitQueue.add(entry);
        edit.save(getId(), entry);
        GameQueueAction action = GameQueueAction.playerUnreadied(player);
        checkForRsvpExpirations(edit, action);

        return action;
    }

    public GameQueueAction cut(final HomedUser player, final GameQueueEdit edit) throws UserError {
        int playerId = player.getId();
        if (!userMap.containsKey(playerId) || !userMap.get(playerId).getState().isWaiting()) {
            throw new UserError("%s is not in the queue.", player.getName());
        }

        GameQueueEntry entry = userMap.get(playerId);
        removeFromLists(entry);
        entry.setState(PlayerState.ON_DECK);
        onDeck.add(entry);
        edit.save(getId(), entry);
        GameQueueAction action = GameQueueAction.playerOnDecked(player);
        checkForRsvpExpirations(edit, action);

        return action;
    }


    public GameQueueAction lg(final int botHomeId, final HomedUser player, final GameQueueEdit edit) throws UserError {
        int playerId = player.getId();
        if (userMap.containsKey(playerId) && userMap.get(playerId).getState() == PlayerState.LG) {
            throw new UserError("%s is already marked LG.", player.getName());
        }

        if (!userMap.containsKey(playerId) || !userMap.get(playerId).getState().isPlaying()) {
            throw new UserError("%s is not playing.", player.getName());
        }

        GameQueueEntry entry = userMap.get(playerId);
        entry.setState(PlayerState.LG);
        edit.save(getId(), entry);
        GameQueueAction action = GameQueueAction.playerLged(player);
        promotePlayersToOnDeck(botHomeId, edit, action);
        checkForRsvpExpirations(edit, action);

        return action;
    }

    public GameQueueAction lgAll(final int botHomeId, final GameQueueEdit edit) throws UserError {
        int changed = 0;
        int lged = 0;
        GameQueueAction action = GameQueueAction.emptyAction();
        for (GameQueueEntry entry : playing) {
            if (entry.getState() == PlayerState.PLAYING) {
                changed++;
                entry.setState(PlayerState.LG);
                edit.save(getId(), entry);
                action.merge(GameQueueAction.playerLged(entry.getUser()));
            } else if (entry.getState() == PlayerState.LG) {
                lged++;
            }
        }
        if (changed == 0) {
            if (playing.size() == 0) {
                throw new UserError("There is no one playing.");
            } else if (lged == 0) {
                throw new UserError("All players are permanent.");
            } else {
                throw new UserError("All players have already been marked LG.");
            }
        }

        promotePlayersToOnDeck(botHomeId, edit, action);
        checkForRsvpExpirations(edit, action);

        return action;
    }


    public GameQueueAction permanent(final int botHomeId, final HomedUser player, final GameQueueEdit edit)
            throws UserError {
        int playerId = player.getId();
        if (userMap.containsKey(playerId) && userMap.get(playerId).getState() == PlayerState.PERMANENT) {
            throw new UserError("%s is already marked LG.", player.getName());
        }

        if (!userMap.containsKey(playerId) || !userMap.get(playerId).getState().isPlaying()) {
            throw new UserError("%s is not playing.", player.getName());
        }

        GameQueueEntry entry = userMap.get(playerId);
        entry.setState(PlayerState.PERMANENT);
        edit.save(getId(), entry);
        GameQueueAction action = GameQueueAction.playerPermanented(player);
        promotePlayersToOnDeck(botHomeId, edit, action);
        checkForRsvpExpirations(edit, action);

        return action;
    }

    public GameQueueAction onCall(final int botHomeId, final HomedUser player, final GameQueueEdit edit)
            throws UserError {
        int playerId = player.getId();

        if (!userMap.containsKey(playerId)) {
            GameQueueEntry newEntry = new GameQueueEntry(player, Instant.now(), PlayerState.ON_CALL);
            edit.save(getId(), newEntry);
            waitQueue.add(newEntry);
            userMap.put(player.getId(), newEntry);

            GameQueueAction action = GameQueueAction.playerQueued(player);
            promotePlayersToOnDeck(botHomeId, edit, action);
            checkForRsvpExpirations(edit, action);
            return action;
        }

        GameQueueEntry entry = userMap.get(playerId);
        if (!entry.getState().isWaiting()) {
            throw new UserError("%s not in the queue.", player.getName());
        }

        if (entry.getState() == PlayerState.ON_CALL) {
            throw new UserError("%s is already on call.", player.getName());
        }

        entry.setState(PlayerState.ON_CALL);
        edit.save(getId(), entry);
        GameQueueAction action = GameQueueAction.playerOnCalled(player);
        promotePlayersToOnDeck(botHomeId, edit, action);
        checkForRsvpExpirations(edit, action);

        return action;
    }

    public GameQueueAction rsvp(final HomedUser player, final Instant rsvpTime, final GameQueueEdit edit)
            throws UserError {
        int playerId = player.getId();
        GameQueueEntry entry;
        if (userMap.containsKey(playerId)) {
            entry = userMap.get(playerId);
            switch (entry.getState()) {
                case PERMANENT:
                case PLAYING:
                case LG:
                    throw new UserError("%s is already playing.", player.getName());
                case ON_DECK:
                case WAITING:
                case ON_CALL:
                    throw new UserError("%s is already in the queue.", player.getName());
            }
            entry.setState(PlayerState.RSVPED);
            entry.setEnqueueTime(rsvpTime);
        } else {
            entry = new GameQueueEntry(player, rsvpTime, PlayerState.RSVPED);
            userMap.put(playerId, entry);
            rsvped.add(entry);
        }

        edit.save(getId(), entry);
        GameQueueAction action = GameQueueAction.playerRsvped(player);
        checkForRsvpExpirations(edit, action);

        return action;
    }

    public GameQueueEdit clear(final int botHomeId) {
        GameQueueEdit gameQueueEdit = new GameQueueEdit();

        for (GameQueueEntry entry : userMap.values()) {
            gameQueueEdit.delete(id, entry);
        }

        code = null;
        server = null;

        playing.clear();
        waitQueue.clear();
        onDeck.clear();
        userMap.clear();

        gameQueueEdit.save(botHomeId, this);

        return gameQueueEdit;
    }

    private void removeEntry(final GameQueueEdit gameQueueEdit, final int playerId) {
        GameQueueEntry entry = userMap.get(playerId);
        removeFromLists(entry);
        gameQueueEdit.delete(id, entry);
        userMap.remove(playerId);
    }

    private void removeFromLists(GameQueueEntry entry) {
        switch (entry.getState()) {
            case PERMANENT:
            case PLAYING:
            case LG:
                playing.remove(entry);
                break;
            case ON_DECK:
                onDeck.remove(entry);
                break;
            case WAITING:
            case ON_CALL:
                waitQueue.remove(entry);
                break;
            case RSVPED:
            case RSVP_EXPIRED:
                rsvped.remove(entry);
                break;
        }
    }

    public List<HomedUser> getGamePlayers() {
        return makeList(playing);
    }

    public List<HomedUser> getWaitQueue() {
        return makeList(waitQueue);
    }

    public List<HomedUser> getOnDeck() {
        return makeList(onDeck);
    }

    public List<HomedUser> getRsvpList() {
        return makeList(rsvped);
    }

    public GameQueueEdit mergeUser(final HomedUserTable homedUserTable, final int newUserId,
            final List<Integer> oldUserIds) {
        GameQueueEdit gameQueueEdit = new GameQueueEdit();

        Instant minEnqueueTime = Instant.MAX;
        PlayerState playerState = PlayerState.WAITING;
        boolean hasOldPlayer = false;
        for (int oldUserId : oldUserIds) {
            if (userMap.containsKey(oldUserId)) {
                GameQueueEntry oldEntry = userMap.get(oldUserId);
                if (oldEntry != null) {
                    hasOldPlayer = true;

                    if (oldEntry.getEnqueueTime().compareTo(minEnqueueTime) < 0) {
                        minEnqueueTime = oldEntry.getEnqueueTime();
                    }

                    if (oldEntry.getState() == PlayerState.PLAYING) {
                        playerState = PlayerState.PLAYING;
                    }

                    removeEntry(gameQueueEdit, oldUserId);
                }
            }
        }

        if (hasOldPlayer) {
            GameQueueEntry newEntry = userMap.get(newUserId);
            if (newEntry == null) {
                newEntry.setUser(homedUserTable.getById(newUserId));
                if (minEnqueueTime.compareTo(newEntry.getEnqueueTime()) < 0) {
                    newEntry.setEnqueueTime(minEnqueueTime);
                }

                if (playerState == PlayerState.PLAYING) {
                    newEntry.setState(playerState);
                }
            } else {
                newEntry = new GameQueueEntry(homedUserTable.getById(newUserId), minEnqueueTime, playerState);
                userMap.put(newUserId, newEntry);
            }
            gameQueueEdit.save(id, newEntry);
        }

        return gameQueueEdit;
    }

    private void promotePlayersToOnDeck(final int botHomeId, final GameQueueEdit gameQueueEdit,
            final GameQueueAction action) {
        int playingCount = getPlayingCount();

        if (startTime != null) {
            if (Instant.now().compareTo(startTime) < 0) {
                return;
            } else {
                startTime = null;
                gameQueueEdit.save(botHomeId, this);
            }
        }

        if (waitQueue.size() + onDeck.size() + playingCount >= getMinPlayers()) {
            Collections.sort(waitQueue);

            while (!waitQueue.isEmpty() && playingCount + onDeck.size() < getMaxPlayers()) {
                GameQueueEntry joiningEntry = waitQueue.remove(0);
                joiningEntry.setState(PlayerState.ON_DECK);
                gameQueueEdit.save(getId(), joiningEntry);
                action.merge(GameQueueAction.playerOnDecked(joiningEntry.getUser()));
                onDeck.add(joiningEntry);
            }
        }
    }

    private void checkForRsvpExpirations(final GameQueueEdit gameQueueEdit, final GameQueueAction action) {
        Instant now = Instant.now();
        for (GameQueueEntry entry : rsvped) {
            if (entry.getState() == PlayerState.RSVPED && entry.getEnqueueTime().compareTo(now) < 0) {
                gameQueueEdit.save(getId(), entry);
                entry.setState(PlayerState.RSVP_EXPIRED);
                action.merge(GameQueueAction.playerReservationExpired(entry.getUser()));
            }
        }
    }

    private int getPlayingCount() {
        return (int) playing.stream().filter(entry -> entry.getState() != PlayerState.LG).count();
    }

    private List<HomedUser> makeList(final List<GameQueueEntry> gameQueueEntries) {
        Collections.sort(gameQueueEntries);
        return gameQueueEntries.stream().map(entry -> entry.getUser()).collect(Collectors.toList());
    }
}
