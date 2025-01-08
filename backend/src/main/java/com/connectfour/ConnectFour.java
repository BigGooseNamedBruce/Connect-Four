package com.connectfour;

import java.util.Scanner;

public class ConnectFour {
    public static void main(String[] args){

        BitBoard board = new BitBoard();
        Solver solver = new Solver(board);
        int player = 1;
        Scanner input = new Scanner(System.in);

        while (!board.checkWinner(1) && !board.checkWinner(0) && !board.checkDraw()) {
            System.out.println(board);
            System.out.println("Enter move: ");
            int move = input.nextInt();
            input.nextLine();
            
            if (board.isColumnFull(move - 1)) {
                System.out.printf("Columns %d is full\n", move);
                continue;
            }
            board.placeDisc(move - 1, player);

            if (board.checkWinner(1)) {
                break;
            }

            player = solver.getOpponent(player);

            if (board.checkWinner(0)) {
                break;
            }
        }

        System.out.println(board);

        if (board.checkWinner(1)) {
            System.out.println("Player 1 wins");
        } else if (board.checkWinner(0)) {
            System.out.println("Player 2 wins");
        } else {
            System.out.println("Draw");
        }

        input.close();

    }
}
