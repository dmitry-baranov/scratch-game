package com.dmitrii.model;

public class StandardSymbol extends Symbol {
    private final double rewardMultiplier;

    public StandardSymbol(String name, double rewardMultiplier) {
        super(name, "standard");
        this.rewardMultiplier = rewardMultiplier;
    }

    @Override
    public double getRewardMultiplier() {
        return rewardMultiplier;
    }
}