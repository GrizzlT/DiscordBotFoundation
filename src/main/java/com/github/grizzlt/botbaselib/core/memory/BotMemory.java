package com.github.grizzlt.botbaselib.core.memory;

import com.github.grizzlt.botbaselib.core.BotMainClass;
import net.openhft.chronicle.bytes.BytesMarshallable;
import net.openhft.chronicle.map.ChronicleMap;

import java.io.IOException;
import java.util.function.Supplier;

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
            botMainInstance.getLogger().warn("BotMemory is already initialized!!");
            return;
        }
        instance = new BotMemory(botMainInstance);
        botMainInstance.getLogger().trace("BotMemory successfully initialized");
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
            botMainInstance.getLogger().error("Couldn't create Chronicle map!!\nexiting program...", e);
            System.exit(1);
        }
    }

    /**
     * Gets the current value associated with the given key or computes one if none was supported yet
     *
     * @param key
     * @param defaultFactory
     * @param <T> dataType (must be {@link BytesMarshallable}, e.g. Chronicle value interface)
     * @return the current {@link Entry} associated with the given key
     */
    @SuppressWarnings("unchecked")
    public <T extends BytesMarshallable> Entry<T> getOrDefault(String key, Supplier<T> defaultFactory)
    {
        return (Entry<T>)this.memoryMap.computeIfAbsent(key, ignore -> Entry.of(defaultFactory.get()));
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
     * Removes the entry associated with this key
     * @param key
     * @return
     */
    public void remove(String key)
    {
        this.memoryMap.remove(key);
    }
}
