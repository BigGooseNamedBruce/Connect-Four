package com.connectfour.game;

import java.util.Arrays;
import java.util.Scanner;
import com.connectfour.game.*;

public class ConnectFourTest {
    public static void main(String args[]) throws InterruptedException {
        //Board board = new Board();
        
        BitBoard board = new BitBoard();
        

        Solver solver = new Solver(board);
        System.out.println(board);
        char player = 'r';
        //board.placeDisc(3, 'X');
        //65323776614155213553376652122427
        String s = "3464123621337153667644637227";
        //String s = "";
        for (int i = 0; i < s.length(); i++) {
            //System.out.println(s.charAt(i));
            board.placeDisc(Character.getNumericValue(s.charAt(i) - 1), player);
            player = solver.getOpponent(player);
        }
        

 

        System.out.println(board);
        String[][] a = board.toArray();
        for (String[] r: a) {
            System.out.println(Arrays.toString(r));
        }

        
        //solver.solve(1);

        //Temp.printb(board.compute_winning_position(board.possibleNonLosingMoves(0)));

        
        
        // System.out.println(b);
        // b.placeDisc(6);
        // b.placeDisc(6);

        /* System.out.println(~1);
        
        b.placeDisc(0, 1);
        b.placeDisc(1, 0);
        b.placeDisc(2, 0);
        b.placeDisc(3, 0);
        b.placeDisc(2, 0);
        b.placeDisc(3, 0);
        b.placeDisc(3, 0);
        b.placeDisc(1, 1);
        b.placeDisc(2, 1);
        System.out.println(b);
        //System.out.println(b.checkWinner(1));
        b.removeDisc(3, 0);
        System.out.println(b);
        b.removeDisc(3, 0);
        b.removeDisc(2, 1);
        b.removeDisc(2, 0);
        System.out.println(b);

        System.out.println(b.key()); */

        //System.out.println(b.checkWinner(1));

        //System.out.println(board + " " + player);
        //System.out.println(solver.solve(player));
        //board.placeDisc(4, 1);
        //board.placeDisc(4, 1);
        //System.out.println(board);
        //System.out.println(board.canWinNext(0));
        //System.out.println(board.checkWinner(1));
        //board.removeDisc(2, player);
        //board.removeDisc(2, player);
        //board.removeDisc(2, player);
        //board.removeDisc(2, player);
        //board.removeDisc(2, player);
        //board.removeDisc(2, player);
        //board.removeDisc(2, player);
        //System.out.println(board);
        //System.out.println(board.checkWinner('O'));
        //int a = solver.negamax(board, player);
        //System.out.println("score: " + a);
        //System.out.println(solver.negamax(player));
        //Solver s = new Solver(board);
        // int a = 0;
        // while (!board.checkWinner('X') && !board.checkWinner('O') && !board.checkDraw()) {
        //     System.out.println("Best move: " + (solver.findBestMove('X') + 1));
        //     Scanner input = new Scanner(System.in);
        //     System.out.println(board);
        //     System.out.print("Enter: ");
        //     int move = input.nextInt();
        //     input.nextLine();




        //     board.placeDisc(move - 1, 'X');
        //     a = solver.findBestMove('O');
        //     board.placeDisc(a, 'O');
        //     System.out.println();
        //     System.out.println("Opponent Best move: " + (a + 1));
        //     System.out.println();
        // }
        // System.out.println(board);
        
        // if (board.checkWinner('X')) {
        //     System.out.println("X WON");
        // } else if (board.checkWinner('O')) {
        //     System.out.println("O WON");
        // } else {
        //     System.out.println("DRAW");
        // }

    }
}