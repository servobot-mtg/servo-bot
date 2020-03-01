package com.ryan_mtg.servobot.data.models;

import com.ryan_mtg.servobot.utility.Validation;

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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int type;

    @Size(max = Validation.MAX_AUTHENTICATION_TOKEN_LENGTH)
    private String token;

    @Column(name = "client_id")
    @Size(max = Validation.MAX_CLIENT_ID_LENGTH)
    private String clientId;

    @Column(name = "client_secret")
    @Size(max = Validation.MAX_CLIENT_SECRET_LENGTH)
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
