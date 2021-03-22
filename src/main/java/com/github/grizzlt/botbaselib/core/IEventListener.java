package com.github.grizzlt.botbaselib.core;

import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.Event;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.lang.reflect.ParameterizedType;

public abstract class IEventListener<T extends Event>
{
    private final Class<T> parameterClass = (Class<T>)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    private final BotMainClass botMainClass;

    public IEventListener(BotMainClass botMainClass)
    {
        this.botMainClass = botMainClass;
    }

    public abstract Publisher<Boolean> canExecute(T event);

    public abstract Publisher<Object> execute(T event);

    public Publisher<Object> handleError(Throwable throwable, Object obj)
    {
        botMainClass.getLogger().error("Event handling failed!", throwable);
        return Mono.empty();
    }

    public void subscribeOn(EventDispatcher eventDispatcher)
    {
        eventDispatcher.on(this.parameterClass)
                .filterWhen(this::canExecute)
                .flatMap(this::execute)
                .onErrorContinue(this::handleError)
                .subscribe();
    }
}
