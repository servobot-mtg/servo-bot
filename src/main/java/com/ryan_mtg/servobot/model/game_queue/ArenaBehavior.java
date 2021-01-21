package com.ryan_mtg.servobot.model.game_queue;

public class ArenaBehavior implements GameBehavior {
    @Override
    public String respondToAction(final GameQueueAction action, final boolean verbose) {
        return "";
    }

    @Override
    public void appendMessageHeader(final StringBuilder text, final GameQueue gameQueue) {}

    @Override
    public void appendMessageFooter(final StringBuilder text, final GameQueue gameQueue) {}
}
