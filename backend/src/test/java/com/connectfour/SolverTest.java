package com.connectfour;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.List;
import java.nio.file.Path;

import com.connectfour.game.*;

public class SolverTest {

    BitBoard board = new BitBoard();;
    Solver solver =  new Solver(board);
    char player;

    @ParameterizedTest
    @MethodSource("testFilesProvider")
    void testMethodWithFileData(String filepath) throws Exception {
        List<String> data = loadDataFromFile(Path.of(filepath));
        board = new BitBoard();
        solver = new Solver(board);
        player = 'r';

        for (String line: data) {
            String[] parts = line.split(" ");
            String position = parts[0];
            int score = Integer.parseInt(parts[1]);
            
            player = board.load(position);
            System.out.println(line);
            assertEquals(score, solver.solve(board, player));

            board.clear();
            player = 'r';

        }
    }

    private static List<String> loadDataFromFile(Path filePath) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(filePath.toFile())) {
            //return scanner.tokens().collect(Collectors.toList());
            return scanner.useDelimiter("\n")
                          .tokens()
                          .collect(Collectors.toList());
        }
    }

    private static Stream<String> testFilesProvider() {
        return Stream.of(
            "src/test/resources/Test_L3_R1_End_Easy.txt",
            "src/test/resources/Test_L2_R1_Middle_Easy.txt",
            "src/test/resources/Test_L2_R2_Middle_Medium.txt",
            "src/test/resources/Test_L1_R1_Begin_Easy.txt",
            "src/test/resources/Test_L1_R2_Begin_Medium.txt",
            "src/test/resources/Test_L1_R3_Begin_Hard.txt"
        );
    }

    public static void main(String[] args) throws Exception{
        long start = System.nanoTime(); 
        int count = testNegamax("src/test/resources/Test_L2_R2_Middle_Medium.txt");
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
                    board.placeDisc(Character.getNumericValue(moveOrder.charAt(i) - 1), player);
                    player = solver.getOpponent(player);
                }
                

                int testScore = solver.solve(board, player);
                if (testScore == score) {
                    System.out.println("True " + count);
                } else {
                    System.out.println("False: " + testScore + " != " + score + " " + count);
                }

                board.clear();
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
