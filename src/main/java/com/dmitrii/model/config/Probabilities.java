package com.dmitrii.model.config;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Probabilities {
    @SerializedName("standard_symbols")
    private List<StandardSymbolProbability> standardSymbols;
    @SerializedName("bonus_symbols")
    private BonusSymbolProbability bonusSymbols;

    public List<StandardSymbolProbability> getStandardSymbols() {
        return standardSymbols;
    }

    public BonusSymbolProbability getBonusSymbols() {
        return bonusSymbols;
    }

    public void setStandardSymbols(List<StandardSymbolProbability> standardSymbols) {
        this.standardSymbols = standardSymbols;
    }

    public void setBonusSymbols(BonusSymbolProbability bonusSymbols) {
        this.bonusSymbols = bonusSymbols;
    }
}