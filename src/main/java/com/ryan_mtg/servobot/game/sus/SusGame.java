package com.ryan_mtg.servobot.game.sus;

import com.ryan_mtg.servobot.game.Game;
import com.ryan_mtg.servobot.game.GameCommand;
import com.ryan_mtg.servobot.game.GameManager;
import com.ryan_mtg.servobot.game.GameUtils;
import com.ryan_mtg.servobot.game.Responder;
import com.ryan_mtg.servobot.user.User;
import com.ryan_mtg.servobot.utility.CommandParser;
import com.ryan_mtg.servobot.utility.Strings;

import java.util.regex.Pattern;

public class SusGame implements Game {
    public static final int TYPE = 1;
    public static final int IMPOSTER_COUNT = 1;
    public static final int CREW_COUNT = 3;
    public static final int POD_SIZE = IMPOSTER_COUNT + CREW_COUNT;

    private static final Pattern COMMAND_PATTERN = Pattern.compile("\\w+");
    private static final CommandParser COMMAND_PARSER = new CommandParser(COMMAND_PATTERN);

    private final GameManager gameManager;
    private final Responder responder;
    private int id;
    private final SusGameState gameState;

    public SusGame(final SusGameManager gameManager, final Responder responder) {
        this.gameManager = gameManager;
        this.responder = responder;
        this.gameState = new SusGameState();
    }

    public SusGame(final SusGameManager gameManager, final Responder responder, final int id,
            final SusGameState gameState) {
        this.gameManager = gameManager;
        this.responder = responder;
        this.id = id;
        this.gameState = gameState;
    }

    @Override
    public boolean awaitingPlayers() {
        return gameState.getPlayers().size() < POD_SIZE;
    }

    @Override
    public boolean hasPlayer(final User user) {
        return gameState.hasPlayer(gameState.getPlayers(), user);
    }

    @Override
    public void join(final User player) {
        if (hasPlayer(player)) {
            throw new IllegalArgumentException("Player is already playing!");
        }
        if (!awaitingPlayers()) {
            throw new IllegalArgumentException("The game is full!");
        }
        gameState.getPlayers().add(player);
        int wait = POD_SIZE - gameState.getPlayers().size();
        if (wait == 0) {
            start();
            checkAndComplate();
        } else {
            if (wait > 1) {
                respond(player, "You are in the queue for a game. %s more players are needed.", wait);
            } else {
                respond(player, "You are in the queue for a game. One more player is needed.");
            }
        }
    }

    @Override
    public void start() {
        gameState.getImposters().addAll(gameState.getPlayers());

        String startMessage = "The game is starting. ";
        String imposterStartMessage = String.format("%sYou are an imposter!", startMessage);
        String crewStartMessage = String.format("%sYou are a crew member!", startMessage);

        gameState.getImposters().forEach(imposter -> respond(imposter, imposterStartMessage));
        gameState.getCrew().forEach(crewMember -> respond(crewMember, crewStartMessage));
        gameState.setState(SusGameState.State.STARTED);
        update();
    }

    @Override
    public void sendCommand(final User player, final GameCommand gameCommand) {
        if (!gameState.isAlive(player)) {
            respond(player, "You are a ghost, game!");
            return;
        }

        String gameName = gameCommand.getGame();
        CommandParser.ParseResult parseResult = COMMAND_PARSER.parse(gameCommand.getCommandLine());

        String commandNudge = String.format("Please use either !%s kill <victim> or !%s sabotage.", gameName, gameName);
        if (gameState.isCrew(player)) {
            commandNudge = String.format("Please use either !% task or !% emergency.", gameName, gameName);
        }

        // TODO: TASK, EMERGENCY, SABOTAGE
        String command = parseResult.getCommand();
        switch (parseResult.getStatus()) {
            case NO_COMMAND:
                respond(player, "Command required for %s. %s", gameName, commandNudge);
                return;
            case COMMAND_MISMATCH:
                respond(player, "%s isn't properly formatted. %s", command, commandNudge);
                return;
        }

        switch (command) {
            case "kill":
                kill(player, parseResult.getInput());
                return;
            default:
                respond(player, "%s isn't a known command for %s. %s", gameName, command, commandNudge);
                return;
        }
    }

    private void kill(final User killer, final String victimName) {
        if (Strings.isBlank(victimName)) {
            respond(killer, "The kill command requires a victim name.");
            return;
        }
        if (!gameState.isImposter(killer)) {
            respond(killer, "Only imposters can kill.");
            return;
        }
        User victim = findPlayer(victimName);
        if (victim == null) {
            respond(killer, "No player named %s.", victimName);
            return;
        }

        if (GameUtils.isSame(victim, killer)) {
            respond(killer, "Silly imposter, you can't kill yourself.");
            return;
        }

        if (!gameState.isAlive(victim)) {
            respond(killer, "No kill happened. %s was already dead.", victim.getName());
            return;
        }

        if (gameState.isOnCoolDown(killer)) {
            respond(killer, "No kill happened. The cool down is still in effect. "
                    + "Take some time to mull over your life choices.");
            return;
        }

        gameState.kill(killer, victim);

        respond(killer, "You have killed %s.", victim.getName());
        respond(victim, "You have been killed by %s.", killer.getName());

        if (!checkAndComplate()) {
            update();
        }
    }

    private boolean checkAndComplate() {
        if (getAliveCount() == 0) {
            respondAll("The game is over. Noone won. How did that happen?!");
            completeGame();
            return true;
        }

        if (getAliveCount() > 1) {
            return false;
        }

        if (gameState.getCrew().size() > 0) {
            respondAll("The crew have won the game!");
            return true;
        }

        respondAll("The imposters have won the game!");
        return true;
    }

    private void completeGame() {
        gameState.setState(SusGameState.State.FINISHED);
        update();
        gameManager.completeGame(this);
    }

    private void update() {
        gameManager.save(gameState);
    }

    private void respondAll(final String format, final Object... args) {
        String message = String.format(format, args);
        gameState.getPlayers().forEach(player -> respond(player, message));
    }

    private void respond(final User player, final String message, final Object... args) {
        responder.respond(player, String.format(message, args));
    }

    private int getAliveCount() {
        return gameState.getCrew().size() + gameState.getImposters().size();

    }

    private User findPlayer(final String name) {
        for (User player : gameState.getPlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }
}