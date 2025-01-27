package com.connectfour.game;

import java.util.Arrays;

public class MoveSorter {

    private int size;
    private MoveEntry[] entries;

    public MoveSorter() {
        this.size = 0;
        entries = new MoveEntry[BitBoard.BOARD_WIDTH];
        for (int i = 0; i < BitBoard.BOARD_WIDTH; i++) {
            entries[i] = new MoveEntry();
        }
    }

    public static class MoveEntry {
        long move;
        int score;

        public MoveEntry() {
            this.move = 0;
            this.score = 0;
        } 

        public MoveEntry(long move, int score) {
            this.move = move;
            this.score = score;
        } 

        public String toString() {
            return String.format("%d %d", move, score);
        }
    }

    public void add(long move, int score) {
        int pos = size++;
        for(; pos > 0 && entries[pos-1].score > score; --pos) {
            entries[pos] = entries[pos-1];
        }
        entries[pos] = new MoveEntry(move, score);
    }

    public long getNext() {
        if (size != 0) {
            return entries[--size].move;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return String.format("%s %d", Arrays.toString(entries), size);
    }
}
