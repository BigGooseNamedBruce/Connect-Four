package com.connectfour.models;

public class MoveRequest {
    private String player;
    private int column;

    // Getters and setters
    public char getPlayer() {
        if (player.equals("red")) {
            return 'r';
        }
        return 'y';
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
