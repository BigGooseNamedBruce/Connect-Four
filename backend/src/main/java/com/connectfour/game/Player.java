package com.connectfour.game;

/**
 * Enum to represent the different players in a Connect Four game,
 * which are Red and Yellow
 * 
 * @author Brayden T
 */

public enum Player {
    // Different players
    RED,
    YELLOW;

    /**
     * Gets the opponent of the given player
     * 
     * @param player The player to find the opponent of
     * @return The opposite player
     */
    public static Player opponent(Player player) {
        return (player == Player.RED) ? Player.YELLOW : Player.RED;
    }
}