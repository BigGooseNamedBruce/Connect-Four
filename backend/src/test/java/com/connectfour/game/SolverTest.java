package com.connectfour.game;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.List;
import java.nio.file.Path;

import com.connectfour.game.*;

public class SolverTest {

    // BitBoard board = new BitBoard();;
    // Solver solver =  new Solver(board);
    // char player;

    @Test
    void testSolver() {
        String filepath = "src/test/resources/Test_L1_R1_Begin_Easy.txt";
        BitBoard board = new BitBoard();
        Solver solver = new Solver(board);
        char player = 'r';

        try {
            Scanner scanner = new Scanner(new File(filepath));

            while (scanner.hasNextLine()) {

                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                String moveOrder = parts[0];
                int score = Integer.parseInt(parts[1]);

                for (int i = 0; i < moveOrder.length(); i++) {
                    board.placeDisc(Character.getNumericValue(moveOrder.charAt(i)) - 1, player);
                    player = solver.getOpponent(player);
                } 

                int testScore = solver.solve(board, player);
                assertEquals(score, testScore, "I fail");

                board.clear();
                player = 'r';
            }
        } catch (FileNotFoundException fileNotFoundException) {
            fail(filepath + " not found");
        }
    }


    public static void main(String[] args) throws Exception{
        long start = System.nanoTime(); 
        int count = testNegamax("backend/src/test/resources/Test_L2_R2_Middle_Medium.txt");
        
        //BitBoard board = new BitBoard();
        //Solver solver = new Solver(board);
        //board.load("444523"); //443523. 444523
        //System.out.println(solver.findBestMove('r'));
        
        long end = System.nanoTime();
        System.out.println("time: " + ((end - start) / 1000000.0) + "ms");
        System.out.println("time: " + ((end - start) / 1000000.0 / count) + "ms/position");
        System.out.println(count);
    }

    public static int testNegamax(String filename) {
        int count = 0;
        File file = new File(filename);
        BitBoard board = new BitBoard();
        Solver solver = new Solver(board);
        //char player = 'X';
        char player = 'r';


        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                count++;
                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                String moveOrder = parts[0];
                int score = Integer.parseInt(parts[1]);

                for (int i = 0; i < moveOrder.length(); i++) {
                    //System.out.println(s.charAt(i));
                    board.placeDisc(Character.getNumericValue(moveOrder.charAt(i)) - 1, player);
                    player = solver.getOpponent(player);
                }
                

                int testScore = solver.solve(board, player);
                if (testScore == score) {
                    System.out.println("True " + count);
                    ;
                } else {
                    System.out.println("False: " + testScore + " != " + score + " " + count);
                }

                board.clear();
                player = 'r';
                //break;
                if (count >= 1000) {
                    return count;
                }
            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.printf("%s not found", filename);
        }
        return count;

    }
}
