package com.github.grizzlt.botbaselib.core.memory;

import com.github.grizzlt.botbaselib.core.BotMainClass;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

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
    public <T> Entry<T> getOrDefault(String key, T defaultValue, boolean isPersistent)
    {
        if (isPersistent) {
            Entry<?> entry = this.persistentMemoryMap.get(key);
            if (entry == null) {
                Entry<T> newEntry = new Entry<>(defaultValue);
                this.persistentMemoryMap.put(key, newEntry);
                return newEntry;
            }
            return (Entry<T>)entry;
        } else {
            Entry<?> entry = this.dynamicMemoryMap.get(key);
            if (entry == null) {
                Entry<T> newEntry = new Entry<>(defaultValue);
                this.dynamicMemoryMap.put(key, newEntry);
                return newEntry;
            }
            return (Entry<T>)entry;
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
