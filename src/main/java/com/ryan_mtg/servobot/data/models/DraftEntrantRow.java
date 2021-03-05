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
@Table(name = "draft_entrant")
@Getter @Setter
public class DraftEntrantRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "chat_draft_id")
    private int chatDraftId;

    @Column(name = "user_id")
    private int userId;
}
