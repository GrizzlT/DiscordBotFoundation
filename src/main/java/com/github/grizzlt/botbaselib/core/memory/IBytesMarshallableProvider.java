package com.github.grizzlt.botbaselib.core.memory;

import net.openhft.chronicle.wire.Marshallable;

public interface IBytesMarshallableProvider
{
    @SuppressWarnings("rawtypes")
    Marshallable generateMarshallable();
}
