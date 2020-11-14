package com.ryan_mtg.servobot.model.roles;

import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RoleTableEdit {
    @Getter
    private Map<RoleTable, Integer> savedRoleTables = new HashMap<>();

    @Getter
    private Map<Role, Integer> savedRoles = new HashMap<>();

    @Getter
    private Set<Role> deletedRoles = new HashSet<>();

    public void saveRole(final int contextId, final Role role) {
        savedRoles.put(role, contextId);
    }

    public void deleteRole(final Role role) {
        deletedRoles.add(role);
    }

    public void saveRoleTable(final int contextId, final RoleTable roleTable) {
        savedRoleTables.put(roleTable, contextId);
    }
}
