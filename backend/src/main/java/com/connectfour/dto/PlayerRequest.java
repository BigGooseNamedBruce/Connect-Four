package com.connectfour.dto;

import com.connectfour.game.Player;
import com.connectfour.game.Solver;

public class PlayerRequest {
    private String player;
    private Integer difficulty;

    // Getters and setters
    public Player getPlayer() {
        if ("red".equals(player)) {
            return Player.RED;
        }
        return Player.YELLOW;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    /**
     * @return The requested AI difficulty, defaulting to the strongest level when not supplied.
     */
    public int getDifficulty() {
        return difficulty == null ? Solver.MAX_DIFFICULTY : difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }
}
