package com.dmitrii.model;

public class BonusSymbol extends Symbol {
    private final double rewardMultiplier;
    private final double extra;
    private final String impact;

    public BonusSymbol(String name, double rewardMultiplier, double extra, String impact) {
        super(name, "bonus");
        this.rewardMultiplier = rewardMultiplier;
        this.extra = extra;
        this.impact = impact;
    }

    @Override
    public double getRewardMultiplier() {
        return rewardMultiplier;
    }

    public double getExtra() {
        return extra;
    }

    public String getImpact() {
        return impact;
    }
}