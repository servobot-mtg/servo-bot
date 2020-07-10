package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.StorageValueRow;
import com.ryan_mtg.servobot.data.repositories.StorageValueRepository;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.model.storage.IntegerStorageValue;
import com.ryan_mtg.servobot.model.storage.StorageValue;
import com.ryan_mtg.servobot.model.storage.StringStorageValue;
import org.springframework.stereotype.Component;

@Component
public class StorageValueSerializer {
    private final StorageValueRepository storageValueRepository;

    public StorageValueSerializer(final StorageValueRepository storageValueRepository) {
        this.storageValueRepository = storageValueRepository;
    }

    public StorageValue createStorageValue(final StorageValueRow storageValueRow) {
        switch (storageValueRow.getType()) {
            case IntegerStorageValue.TYPE:
                return SystemError.filter(() -> new IntegerStorageValue(storageValueRow.getId(),
                        storageValueRow.getUserId(), storageValueRow.getName(), storageValueRow.getNumber()));
            case StringStorageValue.TYPE:
                return SystemError.filter(() -> new StringStorageValue(storageValueRow.getId(),
                        storageValueRow.getUserId(), storageValueRow.getName(), storageValueRow.getString()));
            default:
                throw new SystemError("Unsupported type: %s", storageValueRow.getType());
        }
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
                storageValueRow.setNumber((int) storageValue.getValue());
                break;
            case StringStorageValue.TYPE:
                storageValueRow.setString((String) storageValue.getValue());
                break;
        }
        return storageValueRow;
    }

    public void save(final StorageValue storageValue, final int botHomeId) {
        StorageValueRow storageValueRow = createStorageValueRow(botHomeId, storageValue);
        storageValueRepository.save(storageValueRow);
        storageValue.setId(storageValueRow.getId());
    }
}
