package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.RoleRow;
import com.ryan_mtg.servobot.data.models.RoleTableRow;
import com.ryan_mtg.servobot.data.repositories.RoleRepository;
import com.ryan_mtg.servobot.data.repositories.RoleTableRepository;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.roles.Role;
import com.ryan_mtg.servobot.model.roles.RoleTable;
import com.ryan_mtg.servobot.model.roles.RoleTableEdit;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Map;

@Component
public class RoleTableSerializer {
    private static final int APPEND_FLAG = 1;

    private final RoleRepository roleRepository;

    private final RoleTableRepository roleTableRepository;

    public RoleTableSerializer(final RoleRepository roleRepository, final RoleTableRepository roleTableRepository) {
        this.roleRepository = roleRepository;
        this.roleTableRepository = roleTableRepository;
    }

    public RoleTable createRoleTable(final int botHomeId, final ServiceHome serviceHome) {
        Iterable<RoleTableRow> roleTableRows = roleTableRepository.findAllByBotHomeId(botHomeId);

        RoleTable roleTable;
        if (roleTableRows.iterator().hasNext()) {
            RoleTableRow roleTableRow = roleTableRows.iterator().next();
            Message message = getMessage(serviceHome, roleTableRow.getChannelId(), roleTableRow.getMessageId());
            roleTable = new RoleTable(roleTableRow.getId(), botHomeId, message);
        } else {
            roleTable = new RoleTable(RoleTable.UNREGISTERED_ID, botHomeId, null);
        }

        Iterable<RoleRow> roleRows = roleRepository.findAllByBotHomeId(botHomeId);

        for (RoleRow roleRow : roleRows) {
            roleTable.registerRole(createRole(roleRow));
        }

        return roleTable;
    }

    @Transactional(rollbackOn = Exception.class)
    public void commit(final RoleTableEdit roleTableEdit) {
        for (Role role : roleTableEdit.getDeletedRoles()) {
            roleRepository.deleteById(role.getId());
        }

        for (Map.Entry<Role, Integer> entry : roleTableEdit.getSavedRoles().entrySet()) {
            saveRole(entry.getValue(), entry.getKey());
        }

        for (Map.Entry<RoleTable, Integer> entry : roleTableEdit.getSavedRoleTables().entrySet()) {
            saveRoleTable(entry.getValue(), entry.getKey());
        }
    }

    private Role createRole(final RoleRow roleRow) {
        boolean append = (roleRow.getFlags() & APPEND_FLAG) != 0;
        return new Role(roleRow.getId(), roleRow.getRole(), roleRow.getRoleId(), roleRow.getEmote(), append);
    }

    private void saveRole(final int contextId, final Role role) {
        RoleRow roleRow = new RoleRow();
        roleRow.setId(role.getId());
        roleRow.setBotHomeId(contextId);
        roleRow.setFlags(role.isAppendEmote() ? APPEND_FLAG : 0);
        roleRow.setRole(role.getRole());
        roleRow.setRoleId(role.getRoleId());
        roleRow.setEmote(role.getEmote());
        roleRepository.save(roleRow);
        role.setId(roleRow.getId());
    }

    private void saveRoleTable(final int contextId, final RoleTable roleTable) {
        RoleTableRow roleTableRow = new RoleTableRow();
        roleTableRow.setId(roleTable.getId());
        roleTableRow.setBotHomeId(contextId);
        Message message = roleTable.getMessage();
        roleTableRow.setChannelId(message == null ? 0 : message.getChannelId());
        roleTableRow.setMessageId(message == null ? 0 : message.getId());
        roleTableRepository.save(roleTableRow);
        roleTable.setId(roleTableRow.getId());
    }

    private Message getMessage(final ServiceHome serviceHome, final long channelId, final long messageId) {
        if (messageId != 0 && channelId != 0) {
            return serviceHome.getSavedMessage(channelId, messageId);
        }
        return null;
    }
}