package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.StorageValueRow;
import com.ryan_mtg.servobot.data.repositories.StorageValueRepository;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.storage.IntegerStorageValue;
import com.ryan_mtg.servobot.model.storage.StorageValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StorageValueSerializer {
    @Autowired
    private StorageValueRepository storageValueRepository;

    public StorageValue createStorageValue(final StorageValueRow storageValueRow) throws BotErrorException {
        switch (storageValueRow.getType()) {
            case IntegerStorageValue.TYPE:
                return new IntegerStorageValue(storageValueRow.getId(), storageValueRow.getName(),
                        storageValueRow.getNumber());
        }
        throw new IllegalArgumentException("Unsupported type: " + storageValueRow.getType());
    }

    public void save(final StorageValue storageValue, final int botHomeId) {
        StorageValueRow storageValueRow = new StorageValueRow();
        storageValueRow.setId(storageValue.getId());
        storageValueRow.setBotHomeId(botHomeId);
        storageValueRow.setType(storageValue.getType());
        storageValueRow.setName(storageValue.getName());

        switch (storageValue.getType()) {
            case IntegerStorageValue.TYPE:
                storageValueRow.setNumber((int)storageValue.getValue());
        }

        storageValueRepository.save(storageValueRow);

        storageValue.setId(storageValueRow.getId());
    }
}
