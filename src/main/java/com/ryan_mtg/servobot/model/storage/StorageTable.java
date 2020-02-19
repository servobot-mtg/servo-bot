package com.ryan_mtg.servobot.model.storage;

import com.ryan_mtg.servobot.model.scope.SymbolTable;
import com.sun.istack.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class StorageTable implements Iterable<StorageValue>, SymbolTable {
    private Map<StorageKey, StorageValue> storageMap = new HashMap<>();

    public StorageValue getStorage(final int userId, final String name) {
        return storageMap.get(new StorageKey(userId, name));
    }

    public StorageValue getStorage(final String name) {
        return storageMap.get(new StorageKey(StorageValue.GLOBAL_USER, name));
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

    public void removeVariables(final String name) {
        Set<StorageKey> keysToRemove = new HashSet<>();
        storageMap.keySet().stream()
                .filter(key -> key.getName().equalsIgnoreCase(name)).forEach(key -> keysToRemove.add(key));
        keysToRemove.forEach(key -> storageMap.remove(key));
    }

    private class StorageKey {
        private final int userId;
        private final String name;

        public StorageKey(final int userId, final String name) {
            this.userId = userId;
            this.name = name;
        }

        public StorageKey(final StorageValue storageValue) {
            this(storageValue.getUserId(), storageValue.getName());
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o){
                return true;
            }
            if (o == null || getClass() != o.getClass()){
                return false;
            }
            StorageKey that = (StorageKey) o;
            return userId == that.userId && name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, name);
        }
    }
}
