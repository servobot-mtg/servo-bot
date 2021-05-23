package com.ryan_mtg.servobot.model.game_queue;

import com.ryan_mtg.servobot.commands.game_queue.GameQueueUtils;

public class AmongUsBehavior implements GameBehavior {
    @Override
    public String respondToAction(final GameQueueAction action, final boolean verbose) {
        String response = "";

        if ((action.hasEvent(GameQueueAction.Event.CODE) || action.hasEvent(GameQueueAction.Event.SERVER)
                || action.hasEvent(GameQueueAction.Event.VERSION) ) && verbose) {
            response = GameQueueUtils.combine(response,
                    getCodeMessage(action.getCode(), action.getServer(), action.getVersion()));
        }

        if (action.hasEvent(GameQueueAction.Event.PROXIMITY_SERVER) && verbose) {
            response = GameQueueUtils.combine(response,
                    GameQueueUtils.getProximityServerMessage(action.getProximityServer()));
        }

        return response;
    }

    @Override
    public void appendHelpMessage(final StringBuilder text) {
        text.append("\n");
        text.append("code: server: version: Displays the code and server.\n");
        text.append("proximity: Displays the proximity server.\n");
    }

    @Override
    public void appendModHelpMessage(final StringBuilder text) {
        text.append("\n");
        text.append("code: server: version: Sets the server, code, and/or version to the argument given.\n");
        text.append("proximity: Sets the proximity voice server to the argument given.\n");
    }

    @Override
    public void appendMessageHeader(final StringBuilder text, final GameQueue gameQueue) {
        appendCode(text, gameQueue.getCode(), gameQueue.getServer(), gameQueue.getVersion());
        if (gameQueue.getVersion() == GameQueue.Version.PROXIMITY && gameQueue.getProximityServer() != null) {
            text.append(". The proximity voice server is `").append(gameQueue.getProximityServer()).append("`.");
        }
        text.append("\n");
    }

    @Override
    public void appendMessageFooter(final StringBuilder text, final GameQueue gameQueue) {
        if (gameQueue.getCode() != null) {
            appendCode(text, gameQueue.getCode(), gameQueue.getServer(), gameQueue.getVersion());
            text.append("\n\n");
        }
    }

    private static void appendCode(final StringBuilder text, final String code, final String server,
            final GameQueue.Version version) {
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
        if (version != null) {
            switch (version) {
                case REGULAR:
                    text.append(". Use the **regular** version");
                    break;
                case BETA:
                    text.append(". Use the **Beta**");
                    break;
                case MODDED:
                    text.append(". Use the **Town of Us Mod**");
                    break;
                case PROXIMITY:
                    text.append(". Use the **Proximity mod**");
                    break;
            }
        }
    }

    public static String getCodeMessage(final GameQueue gameQueue) {
        return getCodeMessage(gameQueue.getCode(), gameQueue.getServer(), gameQueue.getVersion());
    }

    private static String getCodeMessage(final String code, final String server, final GameQueue.Version version) {
        StringBuilder text = new StringBuilder();
        if (code != null) {
            text.append("The code is ");
        } else if (server != null) {
            text.append("The server is ");
        }
        appendCode(text, code, server, version);
        text.append('.');
        return text.toString();
    }
}
