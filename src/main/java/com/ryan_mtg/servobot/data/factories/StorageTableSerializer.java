package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.StorageValueRow;
import com.ryan_mtg.servobot.data.repositories.StorageValueRepository;
import com.ryan_mtg.servobot.model.storage.StorageTable;
import com.ryan_mtg.servobot.model.storage.StorageTableEdit;
import com.ryan_mtg.servobot.model.storage.StorageValue;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
public class StorageTableSerializer {
    private final StorageValueSerializer storageValueSerializer;
    private final StorageValueRepository storageValueRepository;

    public StorageTableSerializer(final StorageValueSerializer storageValueSerializer,
            final StorageValueRepository storageValueRepository) {
        this.storageValueSerializer = storageValueSerializer;
        this.storageValueRepository = storageValueRepository;
    }

    public StorageTable createStorageTable(final int botHomeId) {
        Iterable<StorageValueRow> storageValueRows = storageValueRepository.findByBotHomeId(botHomeId);
        StorageTable storageTable = new StorageTable();

        for (StorageValueRow storageValueRow : storageValueRows) {
            StorageValue storageValue = storageValueSerializer.createStorageValue(storageValueRow);
            storageTable.registerValue(storageValue);
        }

        return storageTable;
    }

    @Transactional
    public void removeVariables(final String name, final int botHomeId) {
        storageValueRepository.deleteAllByBotHomeIdAndName(botHomeId, name);
    }

    @Transactional
    public void commit(final StorageTableEdit storageTableEdit) {
        List<StorageValueRow> storageValueRows = new ArrayList<>();
        storageTableEdit.getSavedStorageValues().forEach((storageValue, botHomeId) ->
            storageValueRows.add(storageValueSerializer.createStorageValueRow(botHomeId, storageValue)));
        storageValueRepository.saveAll(storageValueRows);
    }
}
