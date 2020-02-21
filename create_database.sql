CREATE DATABASE IF NOT EXISTS botdb;
USE botdb;

CREATE TABLE IF NOT EXISTS bot (id INTEGER AUTO_INCREMENT PRIMARY KEY, name VARCHAR(30));

CREATE TABLE IF NOT EXISTS service (id INTEGER AUTO_INCREMENT PRIMARY KEY, type INTEGER, token VARCHAR(60),
                                    client_id VARCHAR(30), client_secret VARCHAR(30));

CREATE TABLE IF NOT EXISTS home (id INTEGER AUTO_INCREMENT PRIMARY KEY, name VARCHAR(30), time_zone VARCHAR(60));

CREATE TABLE IF NOT EXISTS command (id INTEGER AUTO_INCREMENT PRIMARY KEY, type INTEGER, bot_home_id INTEGER,
                                    flags INTEGER, permission INTEGER, stringParameter VARCHAR(200),
                                    stringParameter2 VARCHAR(200), longParameter BIGINT);

CREATE TABLE IF NOT EXISTS reaction (id INTEGER AUTO_INCREMENT PRIMARY KEY, emote VARCHAR(30), secure BIT,
                                     filter INTEGER, filter_value INTEGER, bot_home_id INTEGER);

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

CREATE TABLE IF NOT EXISTS game_queue (id INTEGER AUTO_INCREMENT PRIMARY KEY, bot_home_id INTEGER, name VARCHAR(30),
                                       state INTEGER, next INTEGER, current_player_id INTEGER);

CREATE TABLE IF NOT EXISTS game_queue_entry (game_queue_id INTEGER, spot INTEGER, user_id INTEGER);

CREATE TABLE IF NOT EXISTS command_trigger (id INTEGER AUTO_INCREMENT PRIMARY KEY, type INTEGER, command_id INTEGER,
                                            text VARCHAR(50));

CREATE TABLE IF NOT EXISTS storage_value (id INTEGER AUTO_INCREMENT PRIMARY KEY, bot_home_id INTEGER, type INTEGER,
                                          user_id INTEGER, name VARCHAR(30), number INTEGER, string VARCHAR(200));
