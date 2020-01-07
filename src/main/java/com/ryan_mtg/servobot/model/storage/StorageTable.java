package com.ryan_mtg.servobot.model.storage;

import com.sun.istack.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StorageTable implements Iterable<StorageValue> {
    private Map<String, StorageValue> storageMap = new HashMap<>();

    public StorageValue getStorage(final String name) {
        return storageMap.get(name);
    }

    public Collection<StorageValue> getValues() {
        return storageMap.values();
    }

    public void registerValue(final StorageValue storageValue){
        storageMap.put(storageValue.getName(), storageValue);
    }

    @NotNull
    @Override
    public Iterator<StorageValue> iterator() {
        return storageMap.values().iterator();
    }
}
