package com.connectfour.models;

public class PlayerRequest {
    private String player;

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
}
