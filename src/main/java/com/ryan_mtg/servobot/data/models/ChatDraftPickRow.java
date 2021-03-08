package com.ryan_mtg.servobot.data.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "chat_draft_pick")
@Getter @Setter
public class ChatDraftPickRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "chat_draft_id")
    private int chatDraftId;

    private int pack;

    private int pick;

    @Column(name = "picker_id")
    private int pickerId;
}
