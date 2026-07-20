package com.connectfour.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.connectfour.game.Player;
import com.connectfour.game.Solver;

class ConnectFourServiceTest {

    private ConnectFourService service;

    @BeforeEach
    void setUp() {
        service = new ConnectFourService();
    }

    @Test
    void newGameIsEmpty() {
        String[][] grid = service.getGameArrayPosition();
        for (String[] row : grid) {
            for (String cell : row) {
                assertNull(cell);
            }
        }
        assertNull(service.checkWinner());
    }

    @Test
    void placeUpdatesBoard() {
        service.place(3, Player.RED);
        assertEquals("red", service.toArray()[5][3]);
    }

    @Test
    void resetClearsBoard() {
        service.place(0, Player.RED);
        service.place(1, Player.YELLOW);
        service.reset();

        for (String[] row : service.getGameArrayPosition()) {
            for (String cell : row) {
                assertNull(cell);
            }
        }
    }

    @Test
    void checkWinnerReportsWinningColour() {
        for (int i = 0; i < 4; i++) {
            service.place(0, Player.RED);
        }
        assertEquals("red", service.checkWinner());
    }

    @Test
    void computerBestMoveReturnsLegalColumn() {
        int strong = service.computerBestMove(Player.RED);
        assertTrue(strong >= 0 && strong < 7, "column out of range: " + strong);

        for (int difficulty = Solver.MIN_DIFFICULTY; difficulty <= Solver.MAX_DIFFICULTY; difficulty++) {
            int move = service.computerBestMove(Player.RED, difficulty);
            assertTrue(move >= 0 && move < 7, "column out of range at difficulty " + difficulty + ": " + move);
        }
    }

    @Test
    void computerTakesImmediateWinAtEveryDifficulty() {
        // RED has three in a column and can win by playing column 0
        service.place(0, Player.RED);
        service.place(0, Player.RED);
        service.place(0, Player.RED);

        for (int difficulty = Solver.MIN_DIFFICULTY; difficulty <= Solver.MAX_DIFFICULTY; difficulty++) {
            assertEquals(0, service.computerBestMove(Player.RED, difficulty),
                    "difficulty " + difficulty + " missed the immediate win");
        }
    }
}
