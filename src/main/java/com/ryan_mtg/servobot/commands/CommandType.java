package com.ryan_mtg.servobot.commands;

import lombok.Getter;

@Getter
public enum CommandType {
    ADD_COMMAND_TYPE(5, "Add Command"),
    ADD_REACTION_COMMAND_TYPE(24, "Add Reaction Command"),
    ADD_STATEMENT_COMMAND_TYPE(15, "Add Statement Command"),
    ARREST_COMMAND_TYPE(30, "Arrest Command"),
    CARD_SEARCH_COMMAND_TYPE(32, "Card Search Command"),
    DELAYED_ALERT_COMMAND_TYPE(16, "Delayed Alert Command"),
    DELETE_COMMAND_TYPE(6, "Delete Command"),
    ENTER_RAFFLE_COMMAND_TYPE(21, "Enter Raffle Command"),
    EVALUATE_EXPRESSION_COMMAND_TYPE(19, "Math Command"),
    FACTS_COMMAND_TYPE(2, "Random Statement Command"),
    GAME_QUEUE_COMMAND_TYPE(7, "Game Queue Command"),
    RAFFLE_STATUS_COMMAND_TYPE(22, "Raffle Status Command"),
    JAIL_COMMAND_TYPE(25, "Jail Command"),
    JAIL_BREAK_COMMAND_TYPE(26, "Jail Break Command"),
    JAIL_RELEASE_COMMAND_TYPE(29, "Jail Release Command"),
    JOIN_GAME_QUEUE_COMMAND_TYPE(8, "Join Game Queue Command"),
    MESSAGE_CHANNEL_COMMAND_TYPE(4, "Message Channel Command"),
    REMOVE_FROM_GAME_QUEUE_COMMAND_TYPE(10, "Remove From Game Queue Command"),
    REQUEST_PRIZE_COMMAND_TYPE(28, "Request Prize Command"),
    SCRYFALL_SEARCH_COMMAND_TYPE(31, "Scryfall Search Command"),
    SELECT_WINNER_COMMAND_TYPE(23, "Select Raffle Winner Command"),
    SET_ARENA_USERNAME_COMMAND_TYPE(11, "Set Arena Username Command"),
    SET_ROLE_COMMAND_TYPE(13, "Set Role Command"),
    SET_STATUS_COMMAND_TYPE(14, "Set Status Command"),
    SET_USERS_ROLE_COMMAND_TYPE(27, "Set User's Role Command"),
    SET_VALUE_COMMAND_TYPE(18, "Set Value Command"),
    SHOW_ARENA_USERNAMES_COMMAND_TYPE(12, "Show Arena Usernames Command"),
    SHOW_GAME_QUEUE_COMMAND_TYPE(9, "Show Game Queue Command"),
    SHOW_VALUE_COMMAND_TYPE(17, "Show Value Command"),
    START_RAFFLE_COMMAND_TYPE(20, "Start Raffle Command"),
    TEXT_COMMAND_TYPE(1, "Respond Command"),
    TIER_COMMAND_TYPE(3, "Friendship Tier Command"),
    ;

    private final int type;
    private final String name;

    CommandType(final int type, final String name) {
        this.type = type;
        this.name = name;
    }

    public static CommandType getFromValue(final int type) {
        for(CommandType commandType : values()) {
            if (commandType.getType() == type) {
                return commandType;
            }
        }
        throw new IllegalArgumentException("Unknown type:" + type);
    }

    public int getType() {
        return type;
    }
}
