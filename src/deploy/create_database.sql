CREATE DATABASE IF NOT EXISTS botdb;
USE botdb;

CREATE TABLE IF NOT EXISTS bot (id INTEGER AUTO_INCREMENT PRIMARY KEY, name VARCHAR(30));

CREATE TABLE IF NOT EXISTS service (id INTEGER AUTO_INCREMENT PRIMARY KEY, bot_id INTEGER, type INTEGER,
                                    token VARCHAR(60), client_id VARCHAR(30), client_secret VARCHAR(32));

CREATE TABLE IF NOT EXISTS home (id INTEGER AUTO_INCREMENT PRIMARY KEY, bot_id INTEGER, name VARCHAR(30),
                                 bot_name VARCHAR(30), flags INTEGER, time_zone VARCHAR(60));

CREATE TABLE IF NOT EXISTS command (id INTEGER AUTO_INCREMENT PRIMARY KEY, type INTEGER, bot_home_id INTEGER,
                                    flags INTEGER, permission INTEGER, stringParameter VARCHAR(200),
                                    stringParameter2 VARCHAR(200), longParameter BIGINT, longParameter2 BIGINT,
                                    rate_limit INTEGER);

CREATE TABLE IF NOT EXISTS reaction (id INTEGER AUTO_INCREMENT PRIMARY KEY, emote VARCHAR(30), secure BIT,
                                     filter INTEGER, filter_value INTEGER, bot_home_id INTEGER);

CREATE TABLE IF NOT EXISTS role (id INTEGER AUTO_INCREMENT PRIMARY KEY, bot_home_id INTEGER, flags INTEGER,
                                 role VARCHAR(50), role_id BIGINT, emote VARCHAR(30));

CREATE TABLE IF NOT EXISTS role_table (id INTEGER AUTO_INCREMENT PRIMARY KEY, bot_home_id INTEGER, channel_id BIGINT,
                                       message_id BIGINT);

CREATE TABLE IF NOT EXISTS reaction_pattern (id INTEGER AUTO_INCREMENT PRIMARY KEY, reaction_id INTEGER,
                                             pattern VARCHAR(30));

CREATE TABLE IF NOT EXISTS reaction_command (id INTEGER AUTO_INCREMENT PRIMARY KEY, reaction_id INTEGER,
                                             command_id INTEGER);

CREATE TABLE IF NOT EXISTS service_home (id INTEGER AUTO_INCREMENT PRIMARY KEY, bot_home_id INTEGER,
                                         service_type INTEGER, long_value BIGINT);

CREATE TABLE IF NOT EXISTS alert_generator (id INTEGER AUTO_INCREMENT PRIMARY KEY, type INTEGER,
                    bot_home_id INTEGER, time INTEGER, time_zone VARCHAR(50), alert_token VARCHAR(50));

CREATE TABLE IF NOT EXISTS user (id INTEGER AUTO_INCREMENT PRIMARY KEY, flags INTEGER, twitch_id INTEGER,
                    discord_id BIGINT, twitch_username VARCHAR(50), discord_username VARCHAR(50),
                    arena_username VARCHAR(50));

CREATE TABLE IF NOT EXISTS user_home (bot_home_id INTEGER, user_id INTEGER, state INTEGER);

CREATE TABLE IF NOT EXISTS book (id INTEGER AUTO_INCREMENT PRIMARY KEY, bot_home_id INTEGER, name VARCHAR(50));

CREATE TABLE IF NOT EXISTS statement (id INTEGER AUTO_INCREMENT PRIMARY KEY, bookId INTEGER, text VARCHAR(256));

CREATE TABLE IF NOT EXISTS suggestion (id INTEGER AUTO_INCREMENT PRIMARY KEY, count INTEGER, alias VARCHAR(30));

CREATE TABLE IF NOT EXISTS game_queue (id INTEGER AUTO_INCREMENT PRIMARY KEY, bot_home_id INTEGER, flags INTEGER,
                    game INTEGER, min_players INTEGER, max_players INTEGER, code VARCHAR(30), server VARCHAR(30),
                    proximity_server VARCHAR(50), gamer_tag_variable VARCHAR(30), state INTEGER, message_id BIGINT,
                    channel_id BIGINT, start_time BIGINT);

CREATE TABLE IF NOT EXISTS game_queue_entry (game_queue_id INTEGER, user_id INTEGER, enqueue_time BIGINT,
                    state INTEGER, note VARCHAR(200));

CREATE TABLE IF NOT EXISTS command_trigger (id INTEGER AUTO_INCREMENT PRIMARY KEY, type INTEGER, command_id INTEGER,
                                            text VARCHAR(50));

CREATE TABLE IF NOT EXISTS storage_value (id INTEGER AUTO_INCREMENT PRIMARY KEY, bot_home_id INTEGER, type INTEGER,
                                          user_id INTEGER, name VARCHAR(30), number INTEGER, string VARCHAR(200));

CREATE TABLE IF NOT EXISTS giveaway (id INTEGER AUTO_INCREMENT PRIMARY KEY, bot_home_id INTEGER, name VARCHAR(30),
        flags INTEGER, state INTEGER, request_prize_command_name VARCHAR(30), prize_request_limit INTEGER,
        prize_request_user_limit INTEGER, request_prize_command_id INTEGER, prize_requests INTEGER,
        raffle_flags INTEGER, start_raffle_command_name VARCHAR(30), start_raffle_flags INTEGER,
        start_raffle_permission INTEGER, start_raffle_message VARCHAR(200), start_raffle_command_id INTEGER,
        enter_raffle_command_name VARCHAR(30), enter_raffle_permission INTEGER, enter_raffle_flags INTEGER,
        enter_raffle_message VARCHAR(200), raffle_status_command_name VARCHAR(30), raffle_status_permission INTEGER,
        raffle_status_flags INTEGER, raffle_status_message VARCHAR(200), select_winner_command_name VARCHAR(30),
        select_winner_permission INTEGER, select_winner_flags INTEGER, select_winner_message VARCHAR(200),
        raffle_duration INTEGER, raffle_winner_count INTEGER, raffle_winner_response VARCHAR(200),
        discord_channel VARCHAR(200));

CREATE TABLE IF NOT EXISTS emote_link (id INTEGER AUTO_INCREMENT PRIMARY KEY, bot_home_id INTEGER,
        twitch_emote_name VARCHAR(30), discord_emote_name VARCHAR(30));

CREATE TABLE IF NOT EXISTS prize (id INTEGER AUTO_INCREMENT PRIMARY KEY, giveaway_id INTEGER, reward VARCHAR(200),
        description VARCHAR(200), status INTEGER, winner_id INTEGER);

CREATE TABLE IF NOT EXISTS logged_message (id INTEGER AUTO_INCREMENT PRIMARY KEY, user_id INTEGER, message TEXT,
        service_type INTEGER, direction INTEGER, sent_time BIGINT);

CREATE TABLE IF NOT EXISTS video_timestamp (id INTEGER AUTO_INCREMENT PRIMARY KEY, time BIGINT, channel VARCHAR(50),
                                            user VARCHAR(50), link VARCHAR(200), note VARCHAR(200), offset VARCHAR(30));

CREATE TABLE IF NOT EXISTS chat_draft (id INTEGER AUTO_INCREMENT PRIMARY KEY, bot_home_id INTEGER, state INTEGER,
        pack INTEGER, pick INTEGER, open_command_name VARCHAR(30), open_permission INTEGER, open_flags INTEGER,
        open_message VARCHAR(200), open_command_id INTEGER, enter_command_name VARCHAR(30), enter_permission INTEGER,
        enter_flags INTEGER, enter_message VARCHAR(200), enter_command_id INTEGER, status_command_name VARCHAR(30),
        status_permission INTEGER, status_flags INTEGER, status_message VARCHAR(200), status_command_id INTEGER,
        begin_command_name VARCHAR(30), begin_permission INTEGER, begin_flags INTEGER, begin_message VARCHAR(200),
        begin_command_id INTEGER, next_command_name VARCHAR(30), next_permission INTEGER, next_flags INTEGER,
        next_message VARCHAR(200), next_command_id INTEGER, close_command_name VARCHAR(30), close_permission INTEGER,
        close_flags INTEGER, close_message VARCHAR(200), close_command_id INTEGER);

CREATE TABLE IF NOT EXISTS draft_entrant (id INTEGER AUTO_INCREMENT PRIMARY KEY, chat_draft_id INTEGER,
                                          user_id INTEGER);

CREATE TABLE IF NOT EXISTS chat_draft_pick (id INTEGER AUTO_INCREMENT PRIMARY KEY, chat_draft_id INTEGER, pack INTEGER,
                                          pick INTEGER, picker_id INTEGER);

CREATE TABLE IF NOT EXISTS schedule (id INTEGER AUTO_INCREMENT PRIMARY KEY, bot_home_id INTEGER,
                                          default_announcement VARCHAR(200));

CREATE TABLE IF NOT EXISTS weekly_stream (id INTEGER AUTO_INCREMENT PRIMARY KEY, schedule_id BIGINT, name VARCHAR(30),
                                          announcement VARCHAR(200), enabled BIT, day INTEGER, time INTEGER);

CREATE TABLE IF NOT EXISTS session (primary_id CHAR(36) NOT NULL, session_id CHAR(36) NOT NULL,
        creation_time BIGINT NOT NULL, last_access_time BIGINT NOT NULL, max_inactive_interval INT NOT NULL,
        expiry_time BIGINT NOT NULL, principal_name VARCHAR(100),
        CONSTRAINT session_pk PRIMARY KEY (primary_id));

CREATE TABLE session_ATTRIBUTES (session_primary_id CHAR(36) NOT NULL, attribute_name VARCHAR(200) NOT NULL,
                                 attribute_bytes BLOB NOT NULL, CONSTRAINT session_attributes_pk
                                     PRIMARY KEY (session_primary_id, attribute_name), CONSTRAINT session_attributes_fk
                                     FOREIGN KEY (session_primary_id) REFERENCES session(primary_id) ON DELETE CASCADE);

CREATE UNIQUE INDEX spring_session_ix1 ON session (session_id);
CREATE INDEX spring_session_ix2 ON session (expiry_time);
CREATE INDEX spring_session_ix3 ON session (principal_name);