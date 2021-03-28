package com.github.grizzlt.botbaselib.core.memory;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesMarshallable;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.values.Values;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.util.Arrays;

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
            Class<? extends BytesMarshallable> valueInterface = (Class<? extends BytesMarshallable>)Class.forName(bytes.readUtf8());
            Object valueClass = Values.newHeapInstance(valueInterface);
            this.value = (T)valueClass;
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
        bytes.writeUtf8(((Class<T>)Arrays.stream(this.value.getClass().getGenericInterfaces()).filter(type -> type instanceof Class && BytesMarshallable.class.isAssignableFrom((Class<?>)type)).findFirst().orElseThrow(() -> new IllegalStateException("Class must implement BytesMarshallable at some point!!"))).getName());
        this.value.writeMarshallable(bytes);
    }
}
