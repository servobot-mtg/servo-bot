CREATE DATABASE IF NOT EXISTS botdb;
USE botdb;

CREATE TABLE IF NOT EXISTS bot (id INTEGER AUTO_INCREMENT PRIMARY KEY, name VARCHAR(30), token VARCHAR(60),
                                twitch_token VARCHAR(50));

CREATE TABLE IF NOT EXISTS home (id INTEGER AUTO_INCREMENT PRIMARY KEY, name VARCHAR(30), streamer_id BIGINT);

CREATE TABLE IF NOT EXISTS command (id INTEGER AUTO_INCREMENT PRIMARY KEY, type INTEGER, bot_home_id INTEGER,
                                    stringParameter VARCHAR(200), stringParameter2 VARCHAR(200));

CREATE TABLE IF NOT EXISTS command_alias (id INTEGER AUTO_INCREMENT PRIMARY KEY, command_id INTEGER, alias VARCHAR(30));

CREATE TABLE IF NOT EXISTS command_event (id INTEGER AUTO_INCREMENT PRIMARY KEY, command_id INTEGER,
                                          event_type VARCHAR(30));

CREATE TABLE IF NOT EXISTS reaction (id INTEGER AUTO_INCREMENT PRIMARY KEY, emote VARCHAR(30), filter INTEGER,
                                     bot_home_id INTEGER);

CREATE TABLE IF NOT EXISTS reaction_pattern (id INTEGER AUTO_INCREMENT PRIMARY KEY, reaction_id INTEGER,
                                             pattern VARCHAR(30));

CREATE TABLE IF NOT EXISTS service_home (id INTEGER AUTO_INCREMENT PRIMARY KEY, bot_home_id INTEGER,
                                         service_type INTEGER, long_value BIGINT);
