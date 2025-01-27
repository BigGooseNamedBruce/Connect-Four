package com.connectfour;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.time.Instant;

import com.connectfour.game.*;

public class SolverTest {

    BitBoard board;
    Solver solver;
    static int count = 0;
    char player;


    @BeforeEach
    void setUp() {
        board = new BitBoard();
        solver = new Solver(board);
        count = 0;
        player = 'r';
    }

    @Test
    void endEasyTest() {

        System.out.println("Testing Test_L3_R1_End_Easy.txt");

        File file = new File("src/test/resources/Test_L3_R1_End_Easy.txt");

        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                count++;
                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                String moveOrder = parts[0];
                int score = Integer.parseInt(parts[1]);

                for (int i = 0; i < moveOrder.length(); i++) {
                    board.placeDisc(Character.getNumericValue(moveOrder.charAt(i) - 1), player);
                    player = solver.getOpponent(player);
                }

                int testScore = solver.solve(player);

                if (testScore == score) {
                    System.out.println("True " + count);
                } else {
                    System.out.println("False: " + testScore + " != " + score + " " + count);
                }
                
                assertEquals(score, testScore, "dasdad");

                board.clear();

            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.printf("Test_L3_R1_End_Easy.txt not found");
        }
    }

    @Test
    void MiddleEasyTest() {

        System.out.println("Testing Test_L2_R1_Middle_Easy.txt");

        File file = new File("src/test/resources/Test_L2_R1_Middle_Easy.txt");

        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                count++;
                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                String moveOrder = parts[0];
                int score = Integer.parseInt(parts[1]);

                for (int i = 0; i < moveOrder.length(); i++) {
                    board.placeDisc(Character.getNumericValue(moveOrder.charAt(i) - 1), player);
                    player = solver.getOpponent(player);
                }

                int testScore = solver.solve(player);

                if (testScore == score) {
                    System.out.println("True " + count);
                } else {
                    System.out.println("False: " + testScore + " != " + score + " " + count);
                }
                
                assertEquals(score, testScore, "dasdad");

                board.clear();

            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.printf("Test_L3_R1_End_Easy.txt not found");
        }
    }


    //@Test
    void MiddleMediunTest() {

        System.out.println("Test_L2_R2_Middle_Medium.txt");

        File file = new File("src/test/resources/Test_L2_R2_Middle_Medium.txt");

        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                count++;
                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                String moveOrder = parts[0];
                int score = Integer.parseInt(parts[1]);

                for (int i = 0; i < moveOrder.length(); i++) {
                    board.placeDisc(Character.getNumericValue(moveOrder.charAt(i) - 1), player);
                    player = solver.getOpponent(player);
                }

                int testScore = solver.solve(player);

                if (testScore == score) {
                    System.out.println("True " + count);
                } else {
                    System.out.println("False: " + testScore + " != " + score + " " + count);
                }
                
                assertEquals(score, testScore, "dasdad");

                board.clear();

            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.printf("Test_L3_R1_End_Easy.txt not found");
        }
    }

    @Test
    void BeginEasyTest() {

        System.out.println("Test_L1_R3_Begin_Easy.txt");

        File file = new File("src/test/resources/Test_L1_R3_Begin_Easy.txt");

        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                count++;
                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                String moveOrder = parts[0];
                int score = Integer.parseInt(parts[1]);

                for (int i = 0; i < moveOrder.length(); i++) {
                    board.placeDisc(Character.getNumericValue(moveOrder.charAt(i) - 1), player);
                    player = solver.getOpponent(player);
                }

                int testScore = solver.solve(player);

                if (testScore == score) {
                    System.out.println("True " + count);
                } else {
                    System.out.println("False: " + testScore + " != " + score + " " + count);
                }
                
                assertEquals(score, testScore, "dasdad");

                board.clear();

            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.printf("Test_L3_R1_End_Easy.txt not found");
        }
    }

    public static void main(String[] args) {
        long start = System.nanoTime(); 
        testNegamax("backend/src/test/resources/Test_L1_R1_Begin_Easy.txt");
        long end = System.nanoTime();
        System.out.println("time: " + ((end - start) / 1000000.0) + "ms");
        System.out.println("time: " + ((end - start) / 1000000.0 / count) + "ms/position");
        System.out.println(count);
    }

    public static void testNegamax(String filename) {
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
                    board.placeDisc(Character.getNumericValue(moveOrder.charAt(i) - 1), player);
                    player = solver.getOpponent(player);
                }
                

                int testScore = solver.solve(player);
                if (testScore == score) {
                    System.out.println("True " + count);
                } else {
                    System.out.println("False: " + testScore + " != " + score + " " + count);
                }

                board.clear();
                //break;
                if (count >= 1000) {
                    return;
                }
            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.printf("%s not found", filename);
        }
        

    }

    

}
