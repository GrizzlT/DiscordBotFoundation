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

    public Entry() {}

    public static <T extends BytesMarshallable> Entry<T> of(T value)
    {
        Entry<T> entry = new Entry<>();
        entry.setValue(value);
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

    @SuppressWarnings("unchecked")
    @Override
    public void readMarshallable(BytesIn bytes) throws IORuntimeException, BufferUnderflowException, IllegalStateException
    {
        try
        {
            Class<? extends BytesMarshallable> valueClass = (Class<? extends BytesMarshallable>)Class.forName(bytes.readUtf8());
            valueClass.cast(this.value).readMarshallable(bytes);
            this.value.readMarshallable(bytes);
        } catch (ClassNotFoundException e)
        {
            System.out.println("Couldn't deserialize memory entry");
            e.printStackTrace();
        }
    }

    @Override
    public void writeMarshallable(BytesOut bytes) throws IllegalStateException, BufferOverflowException, BufferUnderflowException, ArithmeticException
    {
        bytes.writeUtf8(this.value.getClass().getName());
        this.value.writeMarshallable(bytes);
    }
}
