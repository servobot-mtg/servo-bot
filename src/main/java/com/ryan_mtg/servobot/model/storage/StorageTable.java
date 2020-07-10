package com.ryan_mtg.servobot.model.storage;

import com.ryan_mtg.servobot.model.scope.SymbolTable;
import com.sun.istack.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StorageTable implements Iterable<StorageValue>, SymbolTable {
    private Map<StorageKey, StorageValue> storageMap = new HashMap<>();

    public boolean isEmpty() {
        return storageMap.isEmpty();
    }

    public StorageValue getStorage(final int userId, final String name) {
        return storageMap.get(new StorageKey(userId, name));
    }

    public StorageValue getStorage(final String name) {
        return storageMap.get(new StorageKey(StorageValue.GLOBAL_USER, name));
    }

    public List<StorageValue> getAllUsersStorage(final String name) {
        return storageMap.values().stream().filter(storageValue -> storageValue.getName().equals(name))
                .collect(Collectors.toList());
    }

    public Collection<StorageValue> getValues() {
        return storageMap.values();
    }

    public void registerValue(final StorageValue storageValue){
        storageMap.put(new StorageKey(storageValue), storageValue);
    }

    @NotNull
    @Override
    public Iterator<StorageValue> iterator() {
        return storageMap.values().iterator();
    }

    @Override
    public StorageValue lookup(final String name) {
        return getStorage(name);
    }

    public void removeVariable(final int storageValueId) {
        storageMap.values().stream().filter(storageValue -> storageValue.getId() == storageValueId).findFirst()
            .ifPresent(storageValue -> removeVariable(storageValue.getUserId(), storageValue.getName()));
    }

    public StorageValue removeVariable(final int userId, final String name) {
        StorageKey keyToRemove = new StorageKey(userId, name);
        return storageMap.remove(keyToRemove);
    }

    public void removeVariables(final String name) {
        Set<StorageKey> keysToRemove = new HashSet<>();
        storageMap.keySet().stream()
                .filter(key -> key.getName().equalsIgnoreCase(name)).forEach(keysToRemove::add);
        keysToRemove.forEach(key -> storageMap.remove(key));
    }

    public StorageTableEdit mergeUser(final int botHomeId, final int newUserId, final List<StorageKey> keys) {
        StorageTableEdit storageTableEdit = new StorageTableEdit();
        for(StorageKey oldKey : keys) {
            StorageValue storageValue = storageMap.get(oldKey);
            StorageKey newKey = new StorageKey(newUserId, oldKey.getName());
            storageValue.setUserId(newUserId);
            storageMap.remove(oldKey);
            storageMap.put(newKey, storageValue);
            storageTableEdit.save(botHomeId, storageValue);
        }
        return storageTableEdit;
    }

    @EqualsAndHashCode
    public static class StorageKey {
        private final int userId;
        @Getter
        private final String name;

        public StorageKey(final int userId, final String name) {
            this.userId = userId;
            this.name = name;
        }

        public StorageKey(final StorageValue storageValue) {
            this(storageValue.getUserId(), storageValue.getName());
        }
    }
}
