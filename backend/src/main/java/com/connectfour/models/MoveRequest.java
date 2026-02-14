package com.connectfour.models;

import com.connectfour.game.Player;

public class MoveRequest {
    private String player;
    private int column;

    // Getters and setters
    public Player getPlayer() {
        if (player.equals("red")) {
            return Player.RED;
        }
        return Player.YELLOW;
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
