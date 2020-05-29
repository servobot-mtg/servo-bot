package com.ryan_mtg.servobot.model.storage;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class StorageTableEdit {
    private Map<StorageValue, Integer> savedStorageValues = new HashMap<>();

    public void save(final int botHomeId, final StorageValue storageValue) {
        savedStorageValues.put(storageValue, botHomeId);
    }

    public void merge(final StorageTableEdit storageTableEdit) {
        savedStorageValues.putAll(storageTableEdit.savedStorageValues);
    }
}
