package com.connectfour.models;

public class MoveRequest {
    private String player;
    private int column;

    // Getters and setters
    public int getPlayer() {
        if (player.equals("red")) {
            return 1;
        }
        return 0;
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
