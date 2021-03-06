package com.ryan_mtg.servobot.model.editors;

import com.ryan_mtg.servobot.data.factories.RoleTableSerializer;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.roles.Role;
import com.ryan_mtg.servobot.model.roles.RoleTable;
import com.ryan_mtg.servobot.model.roles.RoleTableEdit;
import lombok.RequiredArgsConstructor;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
public class RoleTableEditor {
    private final RoleTable roleTable;
    private final RoleTableSerializer roleTableSerializer;

    public List<Role> getRoles() {
        return roleTable.getRoles();
    }

    @Transactional(rollbackOn = Exception.class)
    public Role addRole(final String roleName, final long roleId, final String emote, final boolean append) {
        Role role = new Role(Role.UNREGISTERED_ID, roleName, roleId, emote, append);
        RoleTableEdit roleTableEdit = roleTable.addRole(role);
        roleTableSerializer.commit(roleTableEdit);
        return role;
    }

    @Transactional(rollbackOn = Exception.class)
    public void deleteRole(final int roleId) {
        RoleTableEdit roleTableEdit = roleTable.deleteRole(roleId);
        roleTableSerializer.commit(roleTableEdit);
    }

    @Transactional(rollbackOn = Exception.class)
    public void setMessage(final Message message) {
        RoleTableEdit roleTableEdit = roleTable.setMessage(message);
        roleTableSerializer.commit(roleTableEdit);
    }
}