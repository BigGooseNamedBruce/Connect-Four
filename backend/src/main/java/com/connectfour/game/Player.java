package com.connectfour.game;

public enum Player {
    RED,
    YELLOW;

    public static Player opponent(Player player) {
        return (player == Player.RED) ? Player.YELLOW : Player.RED;
    }
}