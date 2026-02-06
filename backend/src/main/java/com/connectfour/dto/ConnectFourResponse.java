package com.connectfour.dto;

public class ConnectFourResponse {
    private String[][] board;
    private int column;

    public ConnectFourResponse(String[][] board, int column) {
        this.board = board;
        this.column = column;
    }

    // Getters (Jackson uses these for serialization)
    public String[][] getBoard() {
        return board;
    }

    public int getColumn() {
        return column;
    }

    // Setters (needed for deserialization)
    public void setBoard(String[][] board) {
        this.board = board;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}