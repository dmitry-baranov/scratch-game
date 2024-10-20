package com.dmitrii;

import com.dmitrii.model.BonusSymbol;
import com.dmitrii.model.config.GameConfig;
import com.dmitrii.model.StandardSymbol;
import com.dmitrii.model.Symbol;
import com.dmitrii.model.*;
import com.dmitrii.model.config.StandardSymbolProbability;
import com.dmitrii.model.config.SymbolConfig;
import com.dmitrii.model.config.WinningCombinationConfig;

import java.util.*;

public class GameEngine {
    private final GameConfig config;
    private final double betAmount;
    private final Random random;

    private Matrix matrix;
    private Map<String, StandardSymbol> standardSymbols;
    private Map<String, BonusSymbol> bonusSymbols;

    public GameEngine(GameConfig config, double betAmount, Random random) {
        this.config = config;
        this.betAmount = betAmount;
        this.random = random;
        initializeSymbols();
    }

    /**
     * Generates a matrix of symbols based on the configuration probabilities.
     * <p>
     * The generated matrix is stored in the instance variable {@code matrix} for further processing.
     * </p>
     */
    public void generateMatrix() {
        int rows = config.getRows();
        int columns = config.getColumns();
        matrix = new Matrix(rows, columns);

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                Symbol symbol = generateSymbol(row, column);
                matrix.setSymbol(row, column, symbol);
            }
        }
    }

    /**
     * Evaluates the generated matrix to calculate the total reward based on the game rules.
     *
     * @return a {@link GameResult} object containing the evaluation results, including the total reward, the generated matrix,
     * the applied winning combinations per symbol, and the applied bonus symbol.
     */
    public GameResult evaluateResult() {
        GameResult result = new GameResult();
        result.setMatrix(matrix);

        Map<String, Integer> symbolCounts = countStandardSymbols();
        Map<String, List<String>> appliedWinningCombinations = new HashMap<>();

        double totalReward = calculateTotalReward(symbolCounts, appliedWinningCombinations);

        boolean hasWinningCombination = !appliedWinningCombinations.isEmpty();

        totalReward = applyBonusSymbolsIfNeeded(totalReward, hasWinningCombination, result);

        result.setReward(totalReward);
        result.setAppliedWinningCombinations(appliedWinningCombinations);

        return result;
    }

    private void initializeSymbols() {
        standardSymbols = new HashMap<>();
        bonusSymbols = new HashMap<>();

        for (Map.Entry<String, SymbolConfig> entry : config.getSymbols().entrySet()) {
            String name = entry.getKey();
            SymbolConfig symbolConfig = entry.getValue();

            if (Objects.equals(symbolConfig.getType(), "standard")) {
                StandardSymbol symbol = new StandardSymbol(name, symbolConfig.getRewardMultiplier());
                standardSymbols.put(name, symbol);
            } else if (Objects.equals(symbolConfig.getType(), "bonus")) {
                BonusSymbol symbol = new BonusSymbol(
                        name,
                        symbolConfig.getRewardMultiplier() != null ? symbolConfig.getRewardMultiplier() : 0,
                        symbolConfig.getExtra() != null ? symbolConfig.getExtra() : 0,
                        symbolConfig.getImpact()
                );
                bonusSymbols.put(name, symbol);
            } else {
                throw new IllegalArgumentException("Unknown symbol type: " + symbolConfig.getType());
            }
        }
    }

    private Symbol generateSymbol(int row, int column) {
        Map<String, Integer> standardProbabilities = getStandardSymbolProbabilities(row, column);
        Map<String, Integer> bonusProbabilities = config.getProbabilities().getBonusSymbols().getSymbols();

        Map<String, Integer> combinedProbabilities = new HashMap<>();
        combinedProbabilities.putAll(standardProbabilities);
        combinedProbabilities.putAll(bonusProbabilities);

        Map<String, Symbol> combinedSymbols = new HashMap<>();
        combinedSymbols.putAll(standardSymbols);
        combinedSymbols.putAll(bonusSymbols);

        return getRandomSymbol(combinedProbabilities, combinedSymbols);
    }

    private Symbol getRandomSymbol(Map<String, Integer> probabilities, Map<String, ? extends Symbol> symbols) {
        int totalProbability = probabilities.values().stream().mapToInt(Integer::intValue).sum();
        int randomValue = random.nextInt(totalProbability) + 1;

        int cumulative = 0;
        for (Map.Entry<String, Integer> entry : probabilities.entrySet()) {
            cumulative += entry.getValue();
            if (randomValue <= cumulative) {
                return symbols.get(entry.getKey());
            }
        }
        return symbols.values().iterator().next();
    }

    private Map<String, Integer> getStandardSymbolProbabilities(int row, int column) {
        for (StandardSymbolProbability prob : config.getProbabilities().getStandardSymbols()) {
            if (prob.getRow() == row && prob.getColumn() == column) {
                return prob.getSymbols();
            }
        }
        return config.getProbabilities().getStandardSymbols().get(0).getSymbols();
    }

    private Map<String, Integer> countStandardSymbols() {
        Map<String, Integer> counts = new HashMap<>();
        for (int row = 0; row < matrix.getRows(); row++) {
            for (int column = 0; column < matrix.getColumns(); column++) {
                Symbol symbol = matrix.getSymbol(row, column);
                if (Objects.equals(symbol.getType(), "standard")) {
                    counts.put(symbol.getName(), counts.getOrDefault(symbol.getName(), 0) + 1);
                }
            }
        }
        return counts;
    }

    private double calculateTotalReward(Map<String, Integer> symbolCounts, Map<String, List<String>> appliedWinningCombinations) {
        double maxReward = 0;
        String maxRewardSymbolName = null;
        List<String> maxRewardCombinations = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : symbolCounts.entrySet()) {
            String symbolName = entry.getKey();
            int count = entry.getValue();
            List<String> symbolWinningCombinations = new ArrayList<>();
            double symbolReward = calculateSymbolReward(symbolName, count, symbolWinningCombinations);
            if (symbolReward > maxReward) {
                maxReward = symbolReward;
                maxRewardSymbolName = symbolName;
                maxRewardCombinations = symbolWinningCombinations;
            }
        }

        if (maxRewardSymbolName != null) {
            appliedWinningCombinations.put(maxRewardSymbolName, maxRewardCombinations);
        }

        return maxReward;
    }

    private double calculateSymbolReward(String symbolName, int count, List<String> symbolWinningCombinations) {
        StandardSymbol symbol = standardSymbols.get(symbolName);
        double baseReward = betAmount * symbol.getRewardMultiplier();

        CombinationResult bestCombinationResult = getBestCombinationForSymbol(symbolName, count);

        if (bestCombinationResult != null) {
            symbolWinningCombinations.add(bestCombinationResult.getCombinationName());
            return baseReward * bestCombinationResult.getTotalMultiplier();
        } else {
            return 0;
        }
    }

    private CombinationResult getBestCombinationForSymbol(String symbolName, int count) {
        CombinationResult bestSameSymbolsCombination = getBestSameSymbolsCombination(count);
        CombinationResult bestLinearSymbolsCombination = getBestLinearSymbolsCombination(symbolName);

        CombinationResult bestCombination = null;

        if (bestSameSymbolsCombination != null && bestLinearSymbolsCombination != null) {
            bestCombination = (bestSameSymbolsCombination.getTotalMultiplier() >= bestLinearSymbolsCombination.getTotalMultiplier())
                    ? bestSameSymbolsCombination : bestLinearSymbolsCombination;
        } else if (bestSameSymbolsCombination != null) {
            bestCombination = bestSameSymbolsCombination;
        } else if (bestLinearSymbolsCombination != null) {
            bestCombination = bestLinearSymbolsCombination;
        }

        return bestCombination;
    }

    private CombinationResult getBestSameSymbolsCombination(int count) {
        double maxMultiplier = 0;
        String bestCombinationName = null;

        for (Map.Entry<String, WinningCombinationConfig> winEntry : config.getWinCombinations().entrySet()) {
            WinningCombinationConfig winConfig = winEntry.getValue();

            if (Objects.equals(winConfig.getWhen(), "same_symbols") && count >= winConfig.getCount()) {
                double multiplier = winConfig.getRewardMultiplier();
                if (multiplier > maxMultiplier) {
                    maxMultiplier = multiplier;
                    bestCombinationName = winEntry.getKey();
                }
            }
        }

        if (bestCombinationName != null) {
            return new CombinationResult(bestCombinationName, maxMultiplier);
        } else {
            return null;
        }
    }

    private CombinationResult getBestLinearSymbolsCombination(String symbolName) {
        double maxMultiplier = 0;
        String bestCombinationName = null;

        for (Map.Entry<String, WinningCombinationConfig> winEntry : config.getWinCombinations().entrySet()) {
            WinningCombinationConfig winConfig = winEntry.getValue();

            if (Objects.equals(winConfig.getWhen(), "linear_symbols")) {
                if (isLinearCombinationMatched(symbolName, winConfig.getCoveredAreas())) {
                    double multiplier = winConfig.getRewardMultiplier();
                    if (multiplier > maxMultiplier) {
                        maxMultiplier = multiplier;
                        bestCombinationName = winEntry.getKey();
                    }
                }
            }
        }

        if (bestCombinationName != null) {
            return new CombinationResult(bestCombinationName, maxMultiplier);
        } else {
            return null;
        }
    }

    private boolean isLinearCombinationMatched(String symbolName, List<List<String>> coveredAreas) {
        for (List<String> area : coveredAreas) {
            if (isAreaMatched(symbolName, area)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAreaMatched(String symbolName, List<String> area) {
        for (String position : area) {
            String[] parts = position.split(":");
            int row = Integer.parseInt(parts[0]);
            int column = Integer.parseInt(parts[1]);

            Symbol matrixSymbol = matrix.getSymbol(row, column);
            if (!Objects.equals(symbolName, matrixSymbol.getName())) {
                return false;
            }
        }
        return true;
    }

    private double applyBonusSymbolsIfNeeded(double totalReward, boolean hasWinningCombination, GameResult result) {
        if (!hasWinningCombination || totalReward == 0) {
            result.setAppliedBonusSymbol(null);
            return 0;
        }

        List<BonusSymbol> bonusSymbolsList = findBonusSymbols();
        BonusSymbol bonusSymbol = selectBonusSymbol(bonusSymbolsList, totalReward);

        if (bonusSymbol != null) {
            result.setAppliedBonusSymbol(bonusSymbol.getName());
            return applyBonusEffect(totalReward, bonusSymbol);
        }

        result.setAppliedBonusSymbol(null);
        return totalReward;
    }

    private BonusSymbol selectBonusSymbol(List<BonusSymbol> bonusSymbols, double totalReward) {
        BonusSymbol selectedSymbol = null;
        double maxImpact = 0;

        for (BonusSymbol bonusSymbol : bonusSymbols) {
            double impactValue = calculateBonusImpact(bonusSymbol, totalReward);
            if (impactValue > maxImpact) {
                maxImpact = impactValue;
                selectedSymbol = bonusSymbol;
            }
        }

        return selectedSymbol;
    }

    private double applyBonusEffect(double totalReward, BonusSymbol bonusSymbol) {
        switch (bonusSymbol.getImpact()) {
            case "multiply_reward":
                return totalReward * bonusSymbol.getRewardMultiplier();
            case "extra_bonus":
                return totalReward + bonusSymbol.getExtra();
            case "miss":
            default:
                return totalReward;
        }
    }

    private List<BonusSymbol> findBonusSymbols() {
        List<BonusSymbol> bonusSymbolsList = new ArrayList<>();
        for (int row = 0; row < matrix.getRows(); row++) {
            for (int column = 0; column < matrix.getColumns(); column++) {
                Symbol symbol = matrix.getSymbol(row, column);
                if (Objects.equals(symbol.getType(), "bonus")) {
                    bonusSymbolsList.add((BonusSymbol) symbol);
                }
            }
        }
        return bonusSymbolsList;
    }

    private double calculateBonusImpact(BonusSymbol bonusSymbol, double totalReward) {
        switch (bonusSymbol.getImpact()) {
            case "multiply_reward":
                return totalReward * (bonusSymbol.getRewardMultiplier() - 1);
            case "extra_bonus":
                return bonusSymbol.getExtra();
            case "miss":
            default:
                return 0;
        }
    }

    private static class CombinationResult {
        private final String combinationName;
        private final double totalMultiplier;

        public CombinationResult(String combinationName, double totalMultiplier) {
            this.combinationName = combinationName;
            this.totalMultiplier = totalMultiplier;
        }

        public String getCombinationName() {
            return combinationName;
        }

        public double getTotalMultiplier() {
            return totalMultiplier;
        }
    }
}