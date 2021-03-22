package com.github.grizzlt.botbaselib.core.memory;

import com.github.grizzlt.botbaselib.core.BotMainClass;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.util.function.Supplier;

public class BotMemory
{
    private final DB persistentDB;
    private final DB memoryDB;
    private final HTreeMap<String, Entry<?>> persistentMemoryMap;
    private final HTreeMap<String, Entry<?>> dynamicMemoryMap;

    private static BotMemory instance = null;

    public static void initMemory(BotMainClass botMainInstance)
    {
        if (instance != null)
        {
            botMainInstance.getLogger().warn("BotMemory is already initialized!!");
            return;
        }
        instance = new BotMemory(botMainInstance);
        botMainInstance.getLogger().trace("BotMemory successfully initialized");
    }

    public static BotMemory inst()
    {
        return instance;
    }

    private BotMemory(BotMainClass botMainInstance)
    {
        this.persistentDB = DBMaker.fileDB("data/bot-persistent.db")
                .closeOnJvmShutdown()
                .make();
        this.memoryDB = DBMaker.memoryDB()
                .closeOnJvmShutdown()
                .make();
        this.persistentMemoryMap = persistentDB.hashMap("bot_default_persistent_data", Serializer.STRING, Entry.EntrySerializer.inst(botMainInstance)).createOrOpen();
        this.dynamicMemoryMap = memoryDB.hashMap("bot_default_dynamic_data", Serializer.STRING, Entry.EntrySerializer.inst(botMainInstance)).createOrOpen();
    }

    @SuppressWarnings("unchecked")
    public <T> Entry<T> getOrDefault(String key, Supplier<T> defaultFactory, boolean isPersistent)
    {
        Entry<?> entry;
        if (isPersistent) {
            entry = this.persistentMemoryMap.get(key);
            if (entry == null) {
                System.out.println("Before factory get!");
                Entry<T> newEntry = new Entry<>(defaultFactory.get());
                System.out.println("After factory get!");
                this.persistentMemoryMap.put(key, newEntry);
                return newEntry;
            }
        } else {
            entry = this.dynamicMemoryMap.get(key);
            if (entry == null) {
                System.out.println("Before factory get!(non-persistent)");
                Entry<T> newEntry = new Entry<>(defaultFactory.get());
                System.out.println("After factory get! (non-persistent)");
                this.dynamicMemoryMap.put(key, newEntry);
                return newEntry;
            }
        }
        return (Entry<T>)entry;
    }

    public <T> void updateEntry(String key, Entry<T> entryToUpdate, boolean isPersistent)
    {
        if (isPersistent) {
            this.persistentMemoryMap.put(key, entryToUpdate);
        } else {
            this.dynamicMemoryMap.put(key, entryToUpdate);
        }
    }

    /**
     * Removes the entry associated with this key
     * @param key
     * @return
     */
    public void remove(String key, boolean isPersistent)
    {
        if (isPersistent) {
            this.persistentMemoryMap.remove(key);
        } else {
            this.dynamicMemoryMap.remove(key);
        }
    }
}
