package com.ryan_mtg.servobot.data.models;

import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraft;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "chat_draft")
@Getter @Setter
public class ChatDraftRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "bot_home_id")
    private int botHomeId;

    private ChatDraft.State state;

    @Column(name = "open_command_name")
    @Size(max = Validation.MAX_NAME_LENGTH)
    private String openCommandName;

    @Column(name = "open_permission")
    private Permission openPermission;

    @Column(name = "open_flags")
    private int openFlags;

    @Column(name = "open_message")
    @Size(max = Validation.MAX_TEXT_LENGTH)
    private String openMessage;

    @Column(name = "open_command_id")
    private int openCommandId;

    @Column(name = "enter_command_name")
    @Size(max = Validation.MAX_NAME_LENGTH)
    private String enterCommandName;

    @Column(name = "enter_permission")
    private Permission enterPermission;

    @Column(name = "enter_flags")
    private int enterFlags;

    @Column(name = "enter_message")
    @Size(max = Validation.MAX_TEXT_LENGTH)
    private String enterMessage;

    @Column(name = "enter_command_id")
    private int enterCommandId;

    @Column(name = "status_command_name")
    @Size(max = Validation.MAX_NAME_LENGTH)
    private String statusCommandName;

    @Column(name = "status_permission")
    private Permission statusPermission;

    @Column(name = "status_flags")
    private int statusFlags;

    @Column(name = "status_message")
    @Size(max = Validation.MAX_TEXT_LENGTH)
    private String statusMessage;

    @Column(name = "status_command_id")
    private int statusCommandId;

    @Column(name = "begin_command_name")
    @Size(max = Validation.MAX_NAME_LENGTH)
    private String beginCommandName;

    @Column(name = "begin_permission")
    private Permission beginPermission;

    @Column(name = "begin_flags")
    private int beginFlags;

    @Column(name = "begin_message")
    @Size(max = Validation.MAX_TEXT_LENGTH)
    private String beginMessage;

    @Column(name = "begin_command_id")
    private int beginCommandId;

    @Column(name = "next_command_name")
    @Size(max = Validation.MAX_NAME_LENGTH)
    private String nextCommandName;

    @Column(name = "next_permission")
    private Permission nextPermission;

    @Column(name = "next_flags")
    private int nextFlags;

    @Column(name = "next_message")
    @Size(max = Validation.MAX_TEXT_LENGTH)
    private String nextMessage;

    @Column(name = "next_command_id")
    private int nextCommandId;

    @Column(name = "close_command_name")
    @Size(max = Validation.MAX_NAME_LENGTH)
    private String closeCommandName;

    @Column(name = "close_permission")
    private Permission closePermission;

    @Column(name = "close_flags")
    private int closeFlags;

    @Column(name = "close_message")
    @Size(max = Validation.MAX_TEXT_LENGTH)
    private String closeMessage;

    @Column(name = "close_command_id")
    private int closeCommandId;
}
