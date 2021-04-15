package com.github.grizzlt.botbaselib.core.memory;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesMarshallable;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.core.io.IORuntimeException;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

public class Entry<T extends BytesMarshallable> implements BytesMarshallable
{
    private T value;
    private Class<? extends IBytesMarshallableProvider> serializerProvider;

    public Entry() {}

    public static <T extends BytesMarshallable> Entry<T> of(T value, Class<? extends IBytesMarshallableProvider> serializerProvider)
    {
        Entry<T> entry = new Entry<>();
        entry.setValue(value);
        entry.setSerializerProvider(serializerProvider);
        return entry;
    }

    public T getValue()
    {
        return this.value;
    }

    public void setValue(T newValue)
    {
        this.value = newValue;
    }

    private void setSerializerProvider(Class<? extends IBytesMarshallableProvider> serializerProvider)
    {
        this.serializerProvider = serializerProvider;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readMarshallable(BytesIn bytes) throws IORuntimeException, BufferUnderflowException, IllegalStateException
    {
        try
        {
            Class<? extends IBytesMarshallableProvider> providerClass = (Class<? extends IBytesMarshallableProvider>)Class.forName(bytes.readUtf8());
            this.serializerProvider = providerClass;
            this.value = (T)MarshallableProviderFactory.getProvider(providerClass).generateMarshallable();
            this.value.readMarshallable(bytes);
        } catch (ClassNotFoundException e)
        {
            System.out.println("Couldn't deserialize memory entry");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void writeMarshallable(BytesOut bytes) throws IllegalStateException, BufferOverflowException, BufferUnderflowException, ArithmeticException
    {
        bytes.writeUtf8(this.serializerProvider.getName());
        this.value.writeMarshallable(bytes);
    }
}
