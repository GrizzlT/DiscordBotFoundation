package com.github.grizzlt.botbaselib.core.memory;

import com.github.grizzlt.botbaselib.core.BotMainClass;

import java.util.HashMap;
import java.util.Map;

public class MarshallableProviderFactory
{
    private static final Map<Class<? extends IBytesMarshallableProvider>, IBytesMarshallableProvider> iBytesMarshallableProviderCache = new HashMap<>();

    public static IBytesMarshallableProvider getProvider(Class<? extends IBytesMarshallableProvider> providerType) throws RuntimeException
    {
        IBytesMarshallableProvider provider = iBytesMarshallableProviderCache.computeIfAbsent(providerType, clazz -> {
            try
            {
                return providerType.newInstance();
            } catch (InstantiationException | IllegalAccessException e)
            {
                BotMainClass.getLogger().error("Couldn't instantiate provider class", e);
                return null;
            }
        });
        if (provider == null) throw new RuntimeException("No provider created for this type!!");
        return provider;
    }
}
