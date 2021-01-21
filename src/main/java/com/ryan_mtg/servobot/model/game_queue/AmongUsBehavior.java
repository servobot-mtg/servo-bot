package com.ryan_mtg.servobot.model.game_queue;

import com.ryan_mtg.servobot.commands.game_queue.GameQueueUtils;

public class AmongUsBehavior implements GameBehavior {
    @Override
    public String respondToAction(final GameQueueAction action, final boolean verbose) {
        String response = "";

        if ((action.hasEvent(GameQueueAction.Event.CODE) || action.hasEvent(GameQueueAction.Event.SERVER)
                || action.hasEvent(GameQueueAction.Event.ON_BETA) ) && verbose) {
            response = GameQueueUtils.combine(response,
                    getCodeMessage(action.getCode(), action.getServer(), action.getOnBeta()));
        }

        if (action.hasEvent(GameQueueAction.Event.PROXIMITY_SERVER) && verbose) {
            response = GameQueueUtils.combine(response,
                    GameQueueUtils.getProximityServerMessage(action.getProximityServer()));
        }

        return response;
    }

    @Override
    public void appendMessageHeader(final StringBuilder text, final GameQueue gameQueue) {
        appendCode(text, gameQueue.getCode(), gameQueue.getServer(), gameQueue.isOnBeta());
        if (gameQueue.getProximityServer() != null) {
            text.append(". The proximity voice server is `").append(gameQueue.getProximityServer()).append("`.");
        }
        text.append("\n\n");
    }

    @Override
    public void appendMessageFooter(final StringBuilder text, final GameQueue gameQueue) {
        if (gameQueue.getCode() != null) {
            appendCode(text, gameQueue.getCode(), gameQueue.getServer(), gameQueue.isOnBeta());
            text.append("\n\n");
        }
    }

    private static void appendCode(final StringBuilder text, final String code, final String server,
                                   final Boolean isOnBeta) {
        if (code != null) {
            text.append("🔑 **").append(code.toUpperCase()).append("**");
            if (server != null) {
                text.append(" on 🖥️ ").append(server);
            }
        } else if (server != null) {
            text.append("🖥️ ").append(server);
        } else {
            text.append("No code set");
        }
        if (isOnBeta != null) {
            if (isOnBeta) {
                text.append(". Use the **Beta**");
            } else {
                text.append(". Do **not** use the Beta");
            }
        }
    }

    public static String getCodeMessage(final GameQueue gameQueue) {
        return getCodeMessage(gameQueue.getCode(), gameQueue.getServer(), gameQueue.isOnBeta());
    }

    private static String getCodeMessage(final String code, final String server, final Boolean isOnBeta) {
        StringBuilder text = new StringBuilder();
        if (code != null) {
            text.append("The code is ");
        } else if (server != null) {
            text.append("The server is ");
        }
        appendCode(text, code, server, isOnBeta);
        text.append('.');
        return text.toString();
    }

}
