package com.github.grizzlt.botbaselib.core;

import com.github.grizzlt.botbaselib.core.memory.Entry;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.gateway.intent.IntentSet;
import net.openhft.chronicle.bytes.BytesMarshallable;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;

public abstract class BotMainClass
{
    protected Logger BOT_LOGGER_DEFAULT = null;

    public Mono<GatewayDiscordClient> buildClient()
    {
        return DiscordClientBuilder.create(System.getenv("TOKEN"))
                .build()
                .gateway()
                .setEnabledIntents(this.getEnabledIntents())
                .setEventDispatcher(this.createEventDispatcher())
                .login();
    }

    public abstract void initBot(GatewayDiscordClient client);

    public Logger getLogger()
    {
        if (this.BOT_LOGGER_DEFAULT == null)
        {
            this.BOT_LOGGER_DEFAULT = LoggerFactory.getLogger("BOT-LOGGER");
        }
        return this.BOT_LOGGER_DEFAULT;
    }

    @SuppressWarnings("unchecked")
    public ChronicleMap<String, Entry<? extends BytesMarshallable>> buildChronicleMap() throws IOException
    {
        File dataFile = new File("data/bot-persistent.dat");
        dataFile.getParentFile().mkdirs();
        return ChronicleMapBuilder.of(String.class, (Class<Entry<? extends BytesMarshallable>>)(Class<?>)Entry.class)
                .name("name-to-entry-map")
                .averageKey("ThisIsAnAverageKey")
                .averageValueSize(32)
                .entries(100000)
                .createPersistedTo(dataFile);
    }

    public EventDispatcher createEventDispatcher()
    {
        return EventDispatcher.builder()
                .eventScheduler(Schedulers.immediate())
                .build();
    }

    public abstract IntentSet getEnabledIntents();
}
