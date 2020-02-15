package com.ryan_mtg.servobot.data.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "service")
public class ServiceRow {
    public static final int MAX_TOKEN_SIZE = 60;
    public static final int MAX_CLIENT_ID_SIZE = 30;
    public static final int MAX_CLIENT_SECRET_SIZE = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int type;

    @Size(max = MAX_TOKEN_SIZE)
    private String token;

    @Column(name = "client_id")
    @Size(max = MAX_CLIENT_ID_SIZE)
    private String clientId;

    @Column(name = "client_secret")
    @Size(max = MAX_CLIENT_SECRET_SIZE)
    private String clientSecret;

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getToken() {
        return token;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
