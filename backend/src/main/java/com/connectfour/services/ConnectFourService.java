package com.connectfour.services;

import com.connectfour.game.BitBoard;
import com.connectfour.game.Solver;
import com.connectfour.game.Player;

public class ConnectFourService {

    public static void main(String[] args) {
        BitBoard b = new BitBoard();
        b.toArray();
    }

    private BitBoard board = new BitBoard();
    private Solver solver = new Solver();
    private int bestMove = -1;

    public BitBoard startNewGame() {
        board = new BitBoard();
        solver = new Solver();
        return board;
    }

    public String[][] playerMove(int column, Player player) {
        board.placeDisc(column, player);
        return board.toArray();
    }

    public void place(int column, Player player) {
        board.placeDisc(column, player);
    }

    public String[][] toArray(){
        return board.toArray();
    }


    public int computerBestMove(Player player) {
        return solver.findBestMove(board, player);
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
        if (board.checkWinner(Player.RED)) {
            return "red";
        } else if (board.checkWinner(Player.YELLOW)) {
            return "yellow";
        } else {
            return null;
        }
    }
} 

