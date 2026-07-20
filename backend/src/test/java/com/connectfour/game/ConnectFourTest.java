package com.connectfour.game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ConnectFourTest {

    @Test
    void loadedPositionMatchesMoveCount() {
        String moves = "3464123";
        Bitboard board = new Bitboard();
        board.load(moves);

        int nonNull = 0;
        for (String[] row : board.toArray()) {
            for (String cell : row) {
                if (cell != null) {
                    nonNull++;
                }
            }
        }

        assertEquals(moves.length(), board.getMoveCount());
        assertEquals(moves.length(), nonNull);
    }

    @Test
    void loadIsConsistentWithManualPlacement() {
        Bitboard loaded = new Bitboard();
        loaded.load("44");

        Bitboard manual = new Bitboard();
        manual.placeDisc(3, Player.RED);
        manual.placeDisc(3, Player.YELLOW);

        assertEquals(manual.key(), loaded.key());
    }
}
