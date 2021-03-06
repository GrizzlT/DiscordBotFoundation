package com.github.grizzlt.botbaselib.core.memory;

import com.github.grizzlt.botbaselib.core.BotMainClass;
import net.openhft.chronicle.bytes.BytesMarshallable;
import net.openhft.chronicle.map.ChronicleMap;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class BotMemory
{
    private ChronicleMap<String, Entry<? extends BytesMarshallable>> memoryMap = null;

    private static BotMemory instance = null;

    /**
     * Instantiates a new static {@link BotMemory} using the given {@link BotMainClass} instance
     * @param botMainInstance a {@link BotMainClass} instance to get e.g. a logger from...
     */
    public static void initMemory(BotMainClass botMainInstance)
    {
        if (instance != null)
        {
            BotMainClass.getLogger().warn("BotMemory is already initialized!!");
            return;
        }
        instance = new BotMemory(botMainInstance);
        BotMainClass.getLogger().trace("BotMemory successfully initialized");
    }

    /**
     * Returns the current instance of {@link BotMemory}
     * Do not call this method before {@link BotMemory#initMemory(BotMainClass)}!!!!
     * @return the current {@link BotMemory} instance
     */
    public static BotMemory inst()
    {
        return instance;
    }

    /**
     * Private constructor, the memory will be persisted to "data/bot-persistent.dat"
     * @param botMainInstance
     */
    @SuppressWarnings("unchecked")
    private BotMemory(BotMainClass botMainInstance)
    {
        try
        {
            this.memoryMap = botMainInstance.buildChronicleMap();
        } catch (IOException e)
        {
            BotMainClass.getLogger().error("Couldn't create Chronicle map!!\nexiting program...", e);
            System.exit(1);
        }
    }

    public void closeMemoryMap()
    {
        this.memoryMap.close();
    }

    /**
     * Gets the current value associated with the given key or computes one if none was supported yet
     *
     * @param key
     * @param defaultValue
     * @param <T> dataType (must be {@link BytesMarshallable}, e.g. Chronicle value interface)
     * @return the current {@link Entry} associated with the given key
     */
    @SuppressWarnings("unchecked")
    public <T extends BytesMarshallable> Entry<T> getOrDefault(String key, Entry<T> defaultValue)
    {
        Entry<T> entry = (Entry<T>)this.memoryMap.getUsing(key, defaultValue);
        if (entry == null)
        {
            this.memoryMap.put(key, defaultValue);
            return defaultValue;
        }
        return entry;
    }

    /**
     * Updates the current entry
     * @param key
     * @param entryToUpdate
     * @param <T> dataType (must be {@link BytesMarshallable}, e.g. Chronicle value interface)
     */
    public <T extends BytesMarshallable> void updateEntry(String key, Entry<T> entryToUpdate)
    {
        this.memoryMap.put(key, entryToUpdate);
    }

    /**
     * Determines whether the current key is mapped to a value or not
     * @param key
     * @return
     */
    public boolean hasKey(String key)
    {
        return this.memoryMap.containsKey(key);
    }

    /**
     * Removes the entry associated with this key
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends BytesMarshallable> Entry<T> remove(String key)
    {
        return (Entry<T>)this.memoryMap.remove(key);
    }
}
