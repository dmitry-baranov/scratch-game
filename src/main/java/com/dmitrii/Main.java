package com.dmitrii;

import com.dmitrii.model.config.GameConfig;
import com.dmitrii.model.GameResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Objects;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        String configFilePath = null;
        double betAmount = 0;

        for (int i = 0; i < args.length; i++) {
            if (Objects.equals(args[i], "--config") && i + 1 < args.length) {
                configFilePath = args[i + 1];
            } else if (Objects.equals(args[i], "--betting-amount") && i + 1 < args.length) {
                betAmount = Double.parseDouble(args[i + 1]);
            }
        }

        try {
            GameConfig config = ConfigLoader.loadConfig(configFilePath);
            GameEngine engine = new GameEngine(config, betAmount, new Random());

            engine.generateMatrix();

            GameResult result = engine.evaluateResult();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.println(gson.toJson(result));

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}