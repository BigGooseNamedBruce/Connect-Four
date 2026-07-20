package com.connectfour.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BitboardTest {

    @Test
    void newBoardIsEmpty() {
        Bitboard board = new Bitboard();
        assertEquals(Bitboard.BOARD_WIDTH * Bitboard.BOARD_HEIGHT, board.getSpacesLeft());
        assertEquals(0, board.getMoveCount());
        assertFalse(board.checkDraw());
        assertFalse(board.checkWinner(Player.RED));
        assertFalse(board.checkWinner(Player.YELLOW));

        String[][] grid = board.toArray();
        assertEquals(6, grid.length);
        assertEquals(7, grid[0].length);
        for (String[] row : grid) {
            for (String cell : row) {
                assertNull(cell);
            }
        }
    }

    @Test
    void placeDiscLandsAtBottomOfColumn() {
        Bitboard board = new Bitboard();
        board.placeDisc(3, Player.RED);

        String[][] grid = board.toArray();
        assertEquals("red", grid[5][3]);
        assertEquals(41, board.getSpacesLeft());
        assertEquals(1, board.getMoveCount());
    }

    @Test
    void discsStackUpwards() {
        Bitboard board = new Bitboard();
        board.placeDisc(0, Player.RED);
        board.placeDisc(0, Player.YELLOW);
        board.placeDisc(0, Player.RED);

        String[][] grid = board.toArray();
        assertEquals("red", grid[5][0]);
        assertEquals("yellow", grid[4][0]);
        assertEquals("red", grid[3][0]);
    }

    @Test
    void columnBecomesFull() {
        Bitboard board = new Bitboard();
        assertFalse(board.isColumnFull(2));
        for (int i = 0; i < Bitboard.BOARD_HEIGHT; i++) {
            board.placeDisc(2, Player.RED);
        }
        assertTrue(board.isColumnFull(2));
    }

    @Test
    void removeDiscRestoresState() {
        Bitboard board = new Bitboard();
        long emptyKey = board.key();

        board.placeDisc(4, Player.YELLOW);
        assertEquals("yellow", board.toArray()[5][4]);

        board.removeDisc(4);
        assertNull(board.toArray()[5][4]);
        assertEquals(Bitboard.BOARD_WIDTH * Bitboard.BOARD_HEIGHT, board.getSpacesLeft());
        assertEquals(emptyKey, board.key());
    }

    @Test
    void detectsVerticalWin() {
        Bitboard board = new Bitboard();
        for (int i = 0; i < 4; i++) {
            board.placeDisc(1, Player.RED);
        }
        assertTrue(board.checkWinner(Player.RED));
        assertFalse(board.checkWinner(Player.YELLOW));
    }

    @Test
    void detectsHorizontalWin() {
        Bitboard board = new Bitboard();
        for (int col = 0; col < 4; col++) {
            board.placeDisc(col, Player.RED);
        }
        assertTrue(board.checkWinner(Player.RED));
    }

    @Test
    void detectsDiagonalWin() {
        Bitboard board = new Bitboard();
        // Build an upward-right diagonal of RED discs at (5,0),(4,1),(3,2),(2,3)
        board.placeDisc(0, Player.RED);

        board.placeDisc(1, Player.YELLOW);
        board.placeDisc(1, Player.RED);

        board.placeDisc(2, Player.YELLOW);
        board.placeDisc(2, Player.YELLOW);
        board.placeDisc(2, Player.RED);

        board.placeDisc(3, Player.YELLOW);
        board.placeDisc(3, Player.YELLOW);
        board.placeDisc(3, Player.YELLOW);
        board.placeDisc(3, Player.RED);

        assertTrue(board.checkWinner(Player.RED));
    }

    @Test
    void loadAlternatesPlayersAndReportsNextToMove() {
        Bitboard board = new Bitboard();
        Player next = board.load("4444"); // R, Y, R, Y in column 4 (index 3)

        String[][] grid = board.toArray();
        assertEquals("red", grid[5][3]);
        assertEquals("yellow", grid[4][3]);
        assertEquals("red", grid[3][3]);
        assertEquals("yellow", grid[2][3]);
        assertEquals(4, board.getMoveCount());
        assertEquals(Player.RED, next); // after 4 moves it is red's turn again
    }

    @Test
    void canWinNextDetectsThreat() {
        Bitboard board = new Bitboard();
        board.placeDisc(0, Player.RED);
        board.placeDisc(0, Player.RED);
        board.placeDisc(0, Player.RED);
        assertTrue(board.canWinNext(Player.RED));
        assertFalse(board.canWinNext(Player.YELLOW));
    }
}
