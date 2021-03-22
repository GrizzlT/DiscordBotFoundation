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

        GatewayDiscordClient client = botMain.buildClient().block();

        BotMemory.initMemory(botMain);
        botMain.initBot(client);

//        client.on(ReadyEvent.class)
//                .flatMap(event -> Mono.fromRunnable(() -> {
//                    User self = event.getSelf();
//                    ServerManager.GetInstance().GetLogger().info("Logged in as {}#{}", self.getUsername(), self.getDiscriminator());
//                    //System.out.println(String.format("Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));
//                    //client.updatePresence(Presence.online(Activity.listening("\n\"st!help\""))).subscribe();
//                }))
//                .onErrorResume(throwable -> Mono.fromRunnable(() -> System.out.println(throwable.getMessage())))
//                .subscribe();

        client.onDisconnect().block();
    }
}
