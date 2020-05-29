package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.StorageValueRow;
import com.ryan_mtg.servobot.data.repositories.StorageValueRepository;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.storage.IntegerStorageValue;
import com.ryan_mtg.servobot.model.storage.StorageValue;
import org.springframework.stereotype.Component;

@Component
public class StorageValueSerializer {
    private final StorageValueRepository storageValueRepository;

    public StorageValueSerializer(final StorageValueRepository storageValueRepository) {
        this.storageValueRepository = storageValueRepository;
    }

    public StorageValue createStorageValue(final StorageValueRow storageValueRow) throws BotErrorException {
        switch (storageValueRow.getType()) {
            case IntegerStorageValue.TYPE:
                return new IntegerStorageValue(storageValueRow.getId(), storageValueRow.getUserId(),
                        storageValueRow.getName(), storageValueRow.getNumber());
        }
        throw new IllegalArgumentException("Unsupported type: " + storageValueRow.getType());
    }


    public StorageValueRow createStorageValueRow(final int botHomeId, final StorageValue storageValue) {
        StorageValueRow storageValueRow = new StorageValueRow();
        storageValueRow.setId(storageValue.getId());
        storageValueRow.setUserId(storageValue.getUserId());
        storageValueRow.setBotHomeId(botHomeId);
        storageValueRow.setType(storageValue.getType());
        storageValueRow.setName(storageValue.getName());

        switch (storageValue.getType()) {
            case IntegerStorageValue.TYPE:
                storageValueRow.setNumber((int)storageValue.getValue());
        }
        return storageValueRow;
    }

    public void save(final StorageValue storageValue, final int botHomeId) {
        StorageValueRow storageValueRow = createStorageValueRow(botHomeId, storageValue);
        storageValueRepository.save(storageValueRow);
        storageValue.setId(storageValueRow.getId());
    }
}
