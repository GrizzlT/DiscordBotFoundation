package com.github.grizzlt.botbaselib.core.memory;

import com.github.grizzlt.botbaselib.core.BotMainClass;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class Entry<T>
{
    private T value;

    public Entry(T value)
    {
        this.value = value;
    }

    public T getValue()
    {
        return this.value;
    }

    public void setValue(T newValue)
    {
        this.value = newValue;
    }

    public JsonElement toJson(Gson context)
    {
        JsonObject valueObj = new JsonObject();
        valueObj.add(TypeToken.get(this.value.getClass()).getType().getTypeName(), context.toJsonTree(this.value));
        return valueObj;
    }

    public static Entry<?> fromJson(JsonElement element, Gson context, Logger logger)
    {
        if (!element.isJsonObject() || element.getAsJsonObject().entrySet().size() != 1) return null;

        Map.Entry<String, JsonElement> outerEntry = element.getAsJsonObject().entrySet().iterator().next();
        try
        {
            String clazzName = outerEntry.getKey();
            Type clazzType = TypeToken.get(Class.forName(clazzName)).getType();
            Object value = context.fromJson(outerEntry.getValue(), clazzType);
            return new Entry<>(value);
        } catch (Exception e)
        {
            e.printStackTrace();
            logger.error("Couldn't deserialize memory entry!!", e);
        }
        return null;
    }

    public static class EntrySerializer implements Serializer<Entry<?>>
    {
        private static final Gson GSON = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .create();
        private static EntrySerializer INST = null;

        public static EntrySerializer inst(BotMainClass botMainClass)
        {
            if (INST == null) {
                INST = new EntrySerializer(botMainClass);
            }
            return INST;
        }

        private final BotMainClass botMainClass;

        public EntrySerializer(BotMainClass botMainClass)
        {
            this.botMainClass = botMainClass;
        }

        @Override
        public void serialize(@NotNull DataOutput2 out, @NotNull Entry<?> value) throws IOException
        {
            out.writeUTF(value.toJson(GSON).toString());
        }

        @Override
        public Entry<?> deserialize(@NotNull DataInput2 input, int available) throws IOException
        {
            String jsonStr = input.readUTF();
            return Entry.fromJson(JsonParser.parseString(jsonStr), GSON, this.botMainClass.getLogger());
        }
    }
}
