package com.connectfour.services;

import com.connectfour.game.BitBoard;
import com.connectfour.game.Solver;

public class ConnectFourService {

    public static void main(String[] args) {
        BitBoard b = new BitBoard();
        b.toArray();
    }

    private BitBoard board = new BitBoard();
    private Solver solver = new Solver(board);
    private boolean status = false;
    private int bestMove = -1;

    public BitBoard startNewGame() {
        board = new BitBoard();
        solver = new Solver(board);
        return board;
    }

    public String[][] playerMove(int column, char player) {
        board.placeDisc(column, player);
        return board.toArray();
    }

    public int computerBestMove(char player) {
        return solver.findBestMove(player);
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getBestMove() {
        return bestMove;
    }

    public void setBestMove(int bestMove) {
        this.bestMove = bestMove;
    }

    public BitBoard getGamePosition() {
        return board;
    }

    public String[][] getGameArrayPosition() {
        return board.toArray();
    }

    public void reset() {
        board.clear();
    }

    public String checkWinner() {
        if (board.checkWinner('r')) {
            return "red";
        } else if (board.checkWinner('y')) {
            return "yellow";
        } else {
            return null;
        }
    }
} 