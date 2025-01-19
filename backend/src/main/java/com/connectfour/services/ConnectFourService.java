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

    public BitBoard startNewGame() {
        board = new BitBoard();
        solver = new Solver(board);
        return board;
    }

    /* public Game playMove(int column) {
        // Apply logic for making a move (place a piece in the column)
        currentGame.playMove(column);
        return currentGame;
    }

    public Game getGameState() {
        return currentGame;
    }
    
    public Game() {
        this.board = BitBoard();
        this.solver = Solver(board);
    } */

    public String[][] playerMove(int column, int player) {
        board.placeDisc(column, player);
        return board.toArray();
    }

    public BitBoard computerMove(int player) {
        int bestMove = solver.findBestMove(player ^ 1);
        board.placeDisc(bestMove, player);
        return board;
    }

    public BitBoard getGamePosition() {
        return board;
    }

    public String[][] getGameArrayPosition() {
        return board.toArray();
    }

} 
