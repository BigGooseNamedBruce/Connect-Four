package com.connectfour.game;

import java.util.Scanner;

public class Play {
    public static void main(String[] args){

        BitBoard board = new BitBoard();
        Solver solver = new Solver(board);
        char player = 'r';
        Scanner input = new Scanner(System.in);

        while (!board.checkWinner('r') && !board.checkWinner('y') && !board.checkDraw()) {
            System.out.println(board);
            System.out.println("Enter move: ");
            int move = input.nextInt();
            input.nextLine();
            
            if (board.isColumnFull(move - 1)) {
                System.out.printf("Columns %d is full\n", move);
                continue;
            }
            if (player == 'r') {
                board.placeDisc(move - 1, player);
            }
            

            if (board.checkWinner('r')) {
                break;
            }

            player = solver.getOpponent(player);

            int bestMove = solver.findBestMove(player);

            if (player == 'y') {
                board.placeDisc(bestMove, player);
            }

            if (board.checkWinner('y')) {
                break;
            }

            player = solver.getOpponent(player);
        }

        System.out.println(board);

        if (board.checkWinner('r')) {
            System.out.println("Player 1 wins");
        } else if (board.checkWinner('y')) {
            System.out.println("Player 2 wins");
        } else {
            System.out.println("Draw");
        }

        input.close();

    }
}
