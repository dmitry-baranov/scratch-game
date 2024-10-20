package com.dmitrii.model.config;

import com.google.gson.annotations.SerializedName;

public class SymbolConfig {
    @SerializedName("reward_multiplier")
    private Double rewardMultiplier;
    private Double extra;
    private String type;
    private String impact;

    public Double getRewardMultiplier() {
        return rewardMultiplier;
    }

    public Double getExtra() {
        return extra;
    }

    public String getType() {
        return type;
    }

    public String getImpact() {
        return impact;
    }

    public void setRewardMultiplier(Double rewardMultiplier) {
        this.rewardMultiplier = rewardMultiplier;
    }

    public void setExtra(Double extra) {
        this.extra = extra;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setImpact(String impact) {
        this.impact = impact;
    }
}