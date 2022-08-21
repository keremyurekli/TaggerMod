package com.keremyurekli.taggermod.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.Files.createFile;

public class ConfigManager {

    private static Path dir;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static List<BlockPos> blockPosList = new ArrayList<>();

    public static List<BlockPos> getBlockPosList() {
        return readFile().stream().map(s -> fromCustomType(s)).toList();
    }

    public static String toCustomType(BlockPos pos) {
        return pos.getX() + "/" + pos.getY() + "/" + pos.getZ();
    }
    
    public static BlockPos fromCustomType(String pos) {
        String[] split = pos.split("/");
        return new BlockPos(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }
    public static void init() {
        dir = Paths.get(MinecraftClient.getInstance().runDirectory.getPath(), "taggermod/");
        if (!dir.toFile().exists()) {
            dir.toFile().mkdirs();
        }
    }

    public static Path getDir() {
        return dir;
    }

    public static void createEmptyFile() {
        try {
            createFile(dir.resolve("list.json"));

            ArrayList<String> list = new ArrayList<>();

            FileWriter writer = new FileWriter(getDir().resolve("list.json").toFile());
            writer.write(GSON.toJson(list));
            writer.close();
        } catch (Exception ignored) {}
    }

    public static ArrayList<String> readFile(){

        ArrayList<String> list = null;
        try {
            list = GSON.fromJson(new FileReader(getDir().resolve("list.json").toFile()), ArrayList.class);
        } catch (Exception ignored) {}
        return list != null ? list : new ArrayList<>();
    }

    public static void addToFile(String pos) {
        try {
            ArrayList<String> list = readFile();
            if(!list.contains(pos)) {
                list.add(pos);
            }
            FileWriter writer = new FileWriter(getDir().resolve("list.json").toFile());
            writer.write(GSON.toJson(list));
            writer.close();
        } catch (Exception ignored) {}
    }

    public static void removeFromFile(String pos) {
        try {
            ArrayList<String> list = readFile();
            if(!list.contains(pos)) {
                return;
            }
            list.remove(pos);
            FileWriter writer = new FileWriter(getDir().resolve("list.json").toFile());
            writer.write(GSON.toJson(list));
            writer.close();
        } catch (Exception ignored) {}
    }





}
