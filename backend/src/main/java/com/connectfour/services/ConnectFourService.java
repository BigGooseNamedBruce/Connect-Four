package com.connectfour.services;

import java.util.ArrayDeque;
import java.util.Deque;

import com.connectfour.game.Bitboard;
import com.connectfour.game.Solver;
import com.connectfour.game.Player;

public class ConnectFourService {

    public static void main(String[] args) {
        Bitboard b = new Bitboard();
        b.toArray();
    }

    private Bitboard board = new Bitboard();
    private Solver solver = new Solver();
    private int bestMove = -1;

    // Columns played, most recent last, so moves can be undone in order.
    private final Deque<Integer> history = new ArrayDeque<>();

    public Bitboard startNewGame() {
        board = new Bitboard();
        solver = new Solver();
        return board;
    }

    public String[][] playerMove(int column, Player player) {
        board.placeDisc(column, player);
        return board.toArray();
    }

    public void place(int column, Player player) {
        board.placeDisc(column, player);
        history.addLast(column);
    }

    /**
     * Removes the most recently played disc.
     *
     * @return true if a disc was removed, false if there was nothing to undo
     */
    public boolean undo() {
        if (history.isEmpty()) {
            return false;
        }
        int column = history.removeLast();
        board.removeDisc(column);
        return true;
    }

    /**
     * Removes up to {@code count} of the most recently played discs.
     *
     * @param count The number of moves to take back
     * @return The number of moves actually removed
     */
    public int undo(int count) {
        int removed = 0;
        for (int i = 0; i < count && undo(); i++) {
            removed++;
        }
        return removed;
    }

    public String[][] toArray(){
        return board.toArray();
    }


    public int computerBestMove(Player player) {
        return solver.findBestMove(board, player);
    }

    public int computerBestMove(Player player, int difficulty) {
        return solver.findBestMove(board, player, difficulty);
    }


    public int getBestMove() {
        return bestMove;
    }

    public void setBestMove(int bestMove) {
        this.bestMove = bestMove;
    }

    public Bitboard getGamePosition() {
        return board;
    }

    public String[][] getGameArrayPosition() {
        return board.toArray();
    }

    public void reset() {
        board.clear();
        history.clear();
        // Start each game with a fresh solver so a previous game's transposition table cannot
        // leak stronger evaluations into a lower-difficulty game.
        solver = new Solver();
    }

    public String checkWinner() {
        if (board.checkWinner(Player.RED)) {
            return "red";
        } else if (board.checkWinner(Player.YELLOW)) {
            return "yellow";
        } else {
            return null;
        }
    }
} 

