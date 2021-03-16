package com.ryan_mtg.servobot.model.roles;

import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.EmoteHomeEvent;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.User;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class RoleTable {
    public static final int UNREGISTERED_ID = 0;
    private final int contextId;

    @Getter @Setter
    private int id;

    @Getter
    private Message message;

    @Getter
    private List<Role> roles = new ArrayList<>();

    public RoleTable(final int id, final int contextId, final Message message) {
        this.id = id;
        this.contextId = contextId;
        this.message = message;
    }

    public void registerRole(final Role role) {
        roles.add(role);
    }

    public void onEmoteAdded(final EmoteHomeEvent emoteHomeEvent) throws UserError {
        String emoteName = emoteHomeEvent.getEmote().getName();
        ServiceHome serviceHome = emoteHomeEvent.getServiceHome();
        User reactor = emoteHomeEvent.getSender();
        for (Role role : roles) {
            if (emoteName.equals(role.getEmote())) {
                serviceHome.setRole(reactor, role.getRole());
                if (role.isAppendEmote() && !nameEndingHasEmote(emoteName, reactor)) {
                    serviceHome.setNickName(reactor, reactor.getName() + ' ' + emoteName);
                }
            }
        }
    }

    public void onEmoteRemoved(final EmoteHomeEvent emoteHomeEvent) throws UserError {
        String emoteName = emoteHomeEvent.getEmote().getName();
        ServiceHome serviceHome = emoteHomeEvent.getServiceHome();
        for (Role role : roles) {
            if (emoteName.equals(role.getEmote())) {
                User reactor = emoteHomeEvent.getSender();
                serviceHome.clearRole(reactor, role.getRole());
            }
        }
    }

    public RoleTableEdit setMessage(final Message message) {
        this.message = message;
        RoleTableEdit roleTableEdit = new RoleTableEdit();
        roleTableEdit.saveRoleTable(contextId, this);
        return roleTableEdit;
    }

    public RoleTableEdit addRole(final Role role) {
        RoleTableEdit roleTableEdit = new RoleTableEdit();
        registerRole(role);
        roleTableEdit.saveRole(contextId, role);
        return roleTableEdit;
    }

    public RoleTableEdit deleteRole(final int roleId) {
        RoleTableEdit roleTableEdit = new RoleTableEdit();
        List<Role> rolesToDelete = new ArrayList<>();
        roles.stream().filter(role -> role.getId() == roleId).forEach(role -> {
            roleTableEdit.deleteRole(role);
            rolesToDelete.add(role);
        });
        roles.removeAll(rolesToDelete);
        return roleTableEdit;
    }

    private boolean nameEndingHasEmote(final String emoteName, final User reactor) {
        if (emoteName.length() == 2 && reactor.getName().endsWith(emoteName.substring(0, 1))) {
            return true;
        }
        return reactor.getName().endsWith(emoteName);
    }
}