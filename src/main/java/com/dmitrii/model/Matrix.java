package com.dmitrii.model;

public class Matrix {
    private final Symbol[][] grid;

    public Matrix(int rows, int columns) {
        grid = new Symbol[rows][columns];
    }

    public Symbol getSymbol(int row, int column) {
        return grid[row][column];
    }

    public void setSymbol(int row, int column, Symbol symbol) {
        grid[row][column] = symbol;
    }

    public Symbol[][] getGrid() {
        return grid;
    }

    public int getRows() {
        return grid.length;
    }

    public int getColumns() {
        return grid[0].length;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < getRows(); row++) {
            for (int column = 0; column < getColumns(); column++) {
                Symbol symbol = grid[row][column];
                sb.append(symbol != null ? symbol.getName() : "null");
                if (column < getColumns() - 1) {
                    sb.append("\t");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}