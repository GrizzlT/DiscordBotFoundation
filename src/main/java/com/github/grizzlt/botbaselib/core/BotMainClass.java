package com.github.grizzlt.botbaselib.core;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public abstract class BotMainClass
{
    protected Logger BOT_LOGGER_DEFAULT = null;

    public Mono<GatewayDiscordClient> buildClient()
    {
        return DiscordClientBuilder.create(System.getenv("TOKEN"))
                .build()
                .gateway()
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

    public EventDispatcher createEventDispatcher()
    {
        return EventDispatcher.builder()
                .eventScheduler(Schedulers.immediate())
                .build();
    }
}
