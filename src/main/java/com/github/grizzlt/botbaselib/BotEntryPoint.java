package com.github.grizzlt.botbaselib;

import com.github.grizzlt.botbaselib.core.BotMainClass;
import com.github.grizzlt.botbaselib.core.memory.BotMemory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import discord4j.core.GatewayDiscordClient;

import java.io.InputStream;
import java.io.InputStreamReader;

public class BotEntryPoint
{
    /**
     * The main function for the bot, an "info.json" file with "main-class" key-value pair must be included in the jar resources
     *
     * This method will first load the specified bot class, initialize the memory, login to discord using a abstract builder function {@link BotMainClass#buildClient()}
     * and call {@link BotMainClass#initBot(GatewayDiscordClient) initBot} which is where you would usually subscribe to all the events
     *
     * @param args
     */
    public static void main(String[] args)
    {
        InputStream configFileStream;
        if ((configFileStream = BotEntryPoint.class.getClassLoader().getResourceAsStream("info.json")) == null)
        {
            System.out.println("ERROR: No info.json was found in the project resources!!!\nExiting program...");
            return;
        }
        JsonElement rootElement;
        try {
            rootElement = JsonParser.parseReader(new InputStreamReader(configFileStream));
        } catch (JsonParseException exception) {
            exception.printStackTrace();
            System.out.println("ERROR: The info.json file didn't contain proper json!!!\nExiting program...");
            return;
        }
        String mainClass = "";
        try {
            mainClass = ((JsonObject)rootElement).get("main-class").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR: The info.json didn't have the correct structure to be recognized by the bot!!!\nExiting program...");
            return;
        }
        BotMainClass botMain = null;
        try
        {
            Class<?> botMainClazz = Class.forName(mainClass);
            if (BotMainClass.class.isAssignableFrom(botMainClazz)) {
                botMain = (BotMainClass)botMainClazz.newInstance();
            } else {
                System.out.println("ERROR: The specified main-class is not of type BotMainClass!!!\nExiting program...");
                return;
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e)
        {
            e.printStackTrace();
            System.out.println("ERROR: Couldn't load the specified class, it either doesn't exist or cannot be instantiated to a subclass of BotMainClass\nExiting program...");
            return;
        }

        BotMemory.initMemory(botMain);

        GatewayDiscordClient client = botMain.buildClient().block();

        botMain.initBot(client);

        client.onDisconnect().block();
    }
}
