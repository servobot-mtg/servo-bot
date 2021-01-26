package com.ryan_mtg.servobot.model.game_queue;

public class BattleGroundsBehavior implements GameBehavior {
    @Override
    public String respondToAction(final GameQueueAction action, final boolean verbose) {
        return "";
    }

    @Override
    public void appendHelpMessage(final StringBuilder text) {}

    @Override
    public void appendModHelpMessage(final StringBuilder text) {
        text.append("\n");
        text.append("partial: 4: Sets the player amount to at most 4 players. Use for queueing into a public lobby.\n");
        text.append("full: 8: Sets the player amount to exactly 8 players allowed. Use for a private lobby.\n");
    }

    @Override
    public void appendMessageHeader(final StringBuilder text, final GameQueue gameQueue) {}

    @Override
    public void appendMessageFooter(final StringBuilder text, final GameQueue gameQueue) {}
}
