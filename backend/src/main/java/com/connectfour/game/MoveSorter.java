package com.connectfour.game;

/**
 * A sorted array to store a move with its score. The array is stored in 
 * ascending order, with the move with the lowest score appearing first 
 * in the array
 * 
 * @author Brayden T
 */

public class MoveSorter {
    
    // Initializing instance variables
    private int size;
    private long[] moves;
    private int[] scores;
    
    /**
     * Default constuctor to initialize an array for both moves and scores
     */
    public MoveSorter() {
        this.size = 0;
        this.moves = new long[BitBoard.BOARD_WIDTH];
        this.scores = new int[BitBoard.BOARD_WIDTH];
    }

    /**
     * Inserts a move and its score into a sorted array
     * 
     * @param move The move to be inserted
     * @param score The score associated with the move to be inserted
     */
    public void add(long move, int score) {
        // Start at the length of the current array
        int idx = size++;

        // Shift elements to the right to make space for the new move
        for(; idx > 0 && scores[idx - 1] > score; idx--) {
            moves[idx] = moves[idx - 1];
            scores[idx] = scores[idx - 1];
        }

        // Insert the new move and score at the correct index
        moves[idx] = move;
        scores[idx] = score;
    }

    /**
     * Gets the move with the highest score
     * 
     * @return A bitmap of the move with the highest score
     */
    public long getNext() {
        // Finds the removes the current best move and score from the sorted array
        if (size != 0) {
            return moves[--size];
        } 

        // There is no move with a score
        return 0;
    }
    
    /**
     * Converts the sorted array into a string representation with the format 
     * [move1: score1, move2: score2, ...]
     * 
     * @return A string representation of the sorted array
     */
    @Override
    public String toString() {
        String moveSorterString = "[";

        // Adds all moves and scores to the string, except for the last
        for (int i = 0; i < size - 1; i++) {
            moveSorterString += String.format("%d: %d, ", moves[i], scores[i]);
        }
        // Adds last move and score to the string
        moveSorterString += String.format("%d: %d]", moves[size - 1], scores[size - 1]);

        return moveSorterString;
    }
}
