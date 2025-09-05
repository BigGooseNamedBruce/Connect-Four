package com.connectfour.game;

import java.util.Arrays;

public class MoveSorter {

    private int size;
    private long[] moves;
    private int[] scores;

    public MoveSorter() {
        this.size = 0;
        this.moves = new long[BitBoard.BOARD_WIDTH];
        this.scores = new int[BitBoard.BOARD_WIDTH];
    }

    public void add(long move, int score) {
        int pos = size++;
        for(; pos > 0 && scores[pos-1] > score; --pos) {
            moves[pos] = moves[pos-1];
            scores[pos] = scores[pos-1];
        }
        moves[pos] = move;
        scores[pos] = score;
    }

    public long getNext() {
        if (size != 0) {
            return moves[--size];
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return String.format("Moves: %s\nScores: %s", Arrays.toString(moves), Arrays.toString(scores));
    }
}
