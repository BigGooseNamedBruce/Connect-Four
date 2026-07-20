package com.connectfour.game;

import java.util.Scanner;

public class ConnectFour {

    private Bitboard board;
    private Solver solver;
    private char player;

    public ConnectFour() {
        this.board = new Bitboard();
        this.solver = new Solver();
        this.player = 'r';
    }

    public void playMove(int column) {
        ;
    }

    public void computerMove() {
        ;
    }

    public boolean checkWinner() {
        return true;
    }

    public void switchPlayer() {
        if (player == 'r') {
            player = 'y';
        } else {
            player = 'r';
        }
    }


}
