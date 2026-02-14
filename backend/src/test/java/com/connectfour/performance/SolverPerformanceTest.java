package com.connectfour.performance;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.connectfour.game.BitBoard;
import com.connectfour.game.Solver;
import com.connectfour.game.Player;

@Tag("performance")
public class SolverPerformanceTest {
    public static void main(String[] args) throws Exception{
    //     long start = System.nanoTime(); 
    //     int count = testNegamax("backend/src/test/resources/Test_L1_R1_Begin_Easy.txt");
    //     long end = System.nanoTime();
    //     System.out.println("time: " + ((end - start) / 1000000.0) + "ms");
    //     System.out.println("time: " + ((end - start) / 1000000.0 / count) + "ms/position");
    //     System.out.println(count);
    }

    private static Logger logger = LoggerFactory.getLogger(SolverPerformanceTest.class);

    BitBoard board;
    Solver solver;
    Player player;
    long startTime;
    long endTime;

    @BeforeEach
    void setup() {
        board = new BitBoard();
        solver = new Solver();
        player = Player.RED;
    }

    @Test
    void test() {
        int a = 1;
    }

    @Test
    void testSolverEasyEnd() {
        String filename = "backend/src/test/resources/Test_L3_R1_End_Easy.txt";
        //timeSolver(filename);
    }
    @Test
    void timeSolver() {
        int count = 0;
        String filename = "backend/src/test/resources/Test_L3_R1_End_Easy.txt";
        long start = System.nanoTime(); 
       

        try {
            Scanner scanner = new Scanner(new File(filename));

            while (scanner.hasNextLine()) {
                count++;
                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                String moveOrder = parts[0];
                int score = Integer.parseInt(parts[1]);

                for (int i = 0; i < moveOrder.length(); i++) {
                    //System.out.println(s.charAt(i));
                    board.placeDisc(Character.getNumericValue(moveOrder.charAt(i)) - 1, player);
                    player = Player.opponent(player);
                }
                

                int testScore = solver.solve(board, player);
                if (testScore == score) {
                    logger.info("True " + count);
                    System.out.println("True " + count);
                } else {
                    System.out.println("False: " + testScore + " != " + score + " " + count);
                }

                board.clear();
                player = Player.RED;
                //break;
            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.printf("%s not found", filename);
        }

        long end = System.nanoTime();
        System.out.println("time: " + ((end - start) / 1000000.0) + "ms");
        System.out.println("time: " + ((end - start) / 1000000.0 / count) + "ms/position");
        System.out.println(count);
    }
}
