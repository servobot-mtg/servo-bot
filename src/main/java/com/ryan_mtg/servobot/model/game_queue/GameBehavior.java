package com.ryan_mtg.servobot.model.game_queue;

public interface GameBehavior {
    String respondToAction(final GameQueueAction action, final boolean verbose);

    void appendHelpMessage(final StringBuilder text);
    void appendModHelpMessage(final StringBuilder text);

    void appendMessageHeader(final StringBuilder text, final GameQueue gameQueue);
    void appendMessageFooter(final StringBuilder text, final GameQueue gameQueue);
}
