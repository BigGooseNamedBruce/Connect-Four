package com.connectfour.models;

import com.connectfour.game.Player;

public class PlayerRequest {
    private String player;

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
}
