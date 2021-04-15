package com.github.grizzlt.botbaselib.core.memory;

import java.util.Map;

public class MarshallableProviderFactory
{
    private static Map<Class<? extends IBytesMarshallableProvider>, IBytesMarshallableProvider> iBytesMarshallableProviderCache;

    public static IBytesMarshallableProvider getProvider(Class<? extends IBytesMarshallableProvider> providerType) throws RuntimeException
    {
        IBytesMarshallableProvider provider = iBytesMarshallableProviderCache.computeIfAbsent(providerType, clazz -> {
            try
            {
                return providerType.newInstance();
            } catch (InstantiationException | IllegalAccessException e)
            {
                e.printStackTrace();
                return null;
            }
        });
        if (provider == null) throw new RuntimeException("No provider created for this type!!");
        return provider;
    }
}
