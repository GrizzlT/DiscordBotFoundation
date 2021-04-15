package com.github.grizzlt.botbaselib.core.memory;

import net.openhft.chronicle.bytes.BytesMarshallable;

public interface IBytesMarshallableProvider
{
    @SuppressWarnings("rawtypes")
    BytesMarshallable generateMarshallable();
}
