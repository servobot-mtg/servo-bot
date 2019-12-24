package com.ryan_mtg.servobot;

import com.ryan_mtg.servobot.commands.CommandAlert;
import com.ryan_mtg.servobot.commands.CommandAlias;
import com.ryan_mtg.servobot.commands.CommandEvent;
import com.ryan_mtg.servobot.commands.Trigger;
import com.ryan_mtg.servobot.data.models.CommandAlertRow;
import com.ryan_mtg.servobot.data.models.CommandAliasRow;
import com.ryan_mtg.servobot.data.models.CommandEventRow;
import com.ryan_mtg.servobot.data.models.TriggerRow;
import com.ryan_mtg.servobot.data.repositories.CommandAlertRepository;
import com.ryan_mtg.servobot.data.repositories.CommandAliasRepository;
import com.ryan_mtg.servobot.data.repositories.CommandEventRepository;
import com.ryan_mtg.servobot.data.repositories.TriggerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdminTask implements Runnable {
    @Autowired
    private CommandAliasRepository commandAliasRepository;

    @Autowired
    private CommandEventRepository commandEventRepository;

    @Autowired
    private CommandAlertRepository commandAlertRepository;

    @Autowired
    private TriggerRepository triggerRepository;

    @Override
    public void run() {
        for(CommandAliasRow commandAliasRow : commandAliasRepository.findAll()) {
            TriggerRow triggerRow = new TriggerRow(Trigger.UNREGISTERED_ID, CommandAlias.TYPE,
                    commandAliasRow.getCommandId(), commandAliasRow.getAlias());
            triggerRepository.save(triggerRow);
        }

        for(CommandEventRow commandEventRow : commandEventRepository.findAll()) {
            TriggerRow triggerRow = new TriggerRow(Trigger.UNREGISTERED_ID, CommandEvent.TYPE,
                    commandEventRow.getCommandId(), commandEventRow.getEventType().toString());
            triggerRepository.save(triggerRow);
        }

        for(CommandAlertRow commandAlertRow : commandAlertRepository.findAll()) {
            TriggerRow triggerRow = new TriggerRow(Trigger.UNREGISTERED_ID, CommandAlert.TYPE,
                    commandAlertRow.getCommandId(), commandAlertRow.getAlertToken());
            triggerRepository.save(triggerRow);
        }
    }
}
