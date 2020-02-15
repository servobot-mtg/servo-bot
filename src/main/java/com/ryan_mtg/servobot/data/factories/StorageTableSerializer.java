package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.StorageValueRow;
import com.ryan_mtg.servobot.data.repositories.StorageValueRepository;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.storage.StorageTable;
import com.ryan_mtg.servobot.model.storage.StorageValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StorageTableSerializer {
    @Autowired
    private StorageValueSerializer storageValueSerializer;

    @Autowired
    private StorageValueRepository storageValueRepository;

    public StorageTable createStorageTable(final int botHomeId) throws BotErrorException {
        Iterable<StorageValueRow> storageValueRows = storageValueRepository.findByBotHomeId(botHomeId);
        StorageTable storageTable = new StorageTable();

        for (StorageValueRow storageValueRow : storageValueRows) {
            StorageValue storageValue = storageValueSerializer.createStorageValue(storageValueRow);
            storageTable.registerValue(storageValue);
        }

        return storageTable;
    }
}
