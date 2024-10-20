package com.dmitrii;

import com.dmitrii.model.config.GameConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;

public class ConfigLoader {
    public static GameConfig loadConfig(String filePath) throws Exception {
        Gson gson = new GsonBuilder().create();
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, GameConfig.class);
        }
    }
}