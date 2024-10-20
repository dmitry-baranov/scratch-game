package com.dmitrii.model.config;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class GameConfig {
    private int columns = 3;
    private int rows = 3;
    private Map<String, SymbolConfig> symbols;
    private Probabilities probabilities;
    @SerializedName("win_combinations")
    private Map<String, WinningCombinationConfig> winCombinations;

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public Map<String, SymbolConfig> getSymbols() {
        return symbols;
    }

    public Probabilities getProbabilities() {
        return probabilities;
    }

    public Map<String, WinningCombinationConfig> getWinCombinations() {
        return winCombinations;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setSymbols(Map<String, SymbolConfig> symbols) {
        this.symbols = symbols;
    }

    public void setProbabilities(Probabilities probabilities) {
        this.probabilities = probabilities;
    }

    public void setWinCombinations(Map<String, WinningCombinationConfig> winCombinations) {
        this.winCombinations = winCombinations;
    }
}