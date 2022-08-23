package com.keremyurekli.taggermod.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.keremyurekli.taggermod.client.TaggermodClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

import static java.nio.file.Files.createFile;


/**
 *
 config manager will be used later
 */
public class ConfigManager {




    private static Path dir;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();




    private static final String PLAYER_KEY = "player_outline_color";
    private static final String HOSTILE_KEY = "hostile_mob_outline_color";
    private static final String NEUTRAL_KEY = "neutral_mob_outline_color";
    private static final String OVERWORLD_KEY = "overworld_block_outline_color";
    private static final String NETHER_KEY = "nether_block_outline_color";
    private static final String END_KEY = "end_block_outline_color";



    public static Color PLAYER_COLOR;
    public static Color HOSTILE_COLOR;
    public static Color NEUTRAL_COLOR;
    public static Color OVERWORLD_COLOR;
    public static Color NETHER_COLOR;
    public static Color END_COLOR;



    private static Color hexToRGB(String HEX){
        Color temp = Color.decode(HEX);
        return temp;
    }


    private static HashMap<String, String> defaults(){
        HashMap<String, String> templist = new HashMap();
        templist.put("player_outline_color", "#00FFFF"); //aqua
        templist.put("hostile_mob_outline_color", "#FF0000"); //red
        templist.put("neutral_mob_outline_color", "#FFFF00"); //yellow
        templist.put("overworld_block_outline_color", "#00FF00"); //green
        templist.put("nether_block_outline_color", "#FF5F1F"); //neon-like orange
        templist.put("end_block_outline_color", "#FF00FF");  //magenta

        return templist;
    }

    public static void init() {
        dir = Paths.get(MinecraftClient.getInstance().runDirectory.getPath(), "config/");
        if (!dir.toFile().exists()) {
            dir.toFile().mkdirs();
        }

    }

    public static Path getDir() {
        return dir;
    }



    public static void createEmptyFile() {
        if(dir.resolve("taggermod.json").toFile().exists()){
            return;
        }
        Path path = null;
        try {
            Map<String, String> settingMap = defaults();
            String temp = GSON.toJson(settingMap);

            path = dir.resolve("taggermod.json");

            FileWriter writer = new FileWriter(dir.resolve(path.toString()).toFile());
            writer.write(temp);
            writer.close();
        } catch (Exception e) {
            System.out.println("Error Clearing/Creating File: " + path.toString());
        }
    }

    public static void read() throws FileNotFoundException {

        Map<String, String> list = null;

        list = GSON.fromJson(new FileReader(getDir().resolve("taggermod.json").toFile()), Map.class);


        PLAYER_COLOR = hexToRGB(list.get(PLAYER_KEY));
        HOSTILE_COLOR = hexToRGB(list.get(HOSTILE_KEY));
        NEUTRAL_COLOR = hexToRGB(list.get(NEUTRAL_KEY));
        OVERWORLD_COLOR = hexToRGB(list.get(OVERWORLD_KEY));
        NETHER_COLOR = hexToRGB(list.get(NETHER_KEY));
        END_COLOR = hexToRGB(list.get(END_KEY));


    }






}

