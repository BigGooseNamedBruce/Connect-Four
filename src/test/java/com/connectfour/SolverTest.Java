import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.time.Instant;
import com.connectfour.*;


public class SolverTest {

    static int count = 0;
    public static void main(String[] args) {
        long start = System.nanoTime(); 
        testNegamax("Test_L2_R1_Middle_Easy.txt");
        long end = System.nanoTime();
        System.out.println("time: " + ((end - start) / 1000000.0) + "ms");
        System.out.println("time: " + ((end - start) / 1000000.0 / count) + "ms/position");
        System.out.println(count);
    }

    public static void testNegamax(String filename) {
        File file = new File(String.format("UnitTests/%s", filename));
        BitBoard board = new BitBoard();
        Solver solver = new Solver(board);
        //char player = 'X';
        int player = 1;
        

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
                //System.out.println(board);

                int testScore = solver.solve(player);
                if (testScore == score) {
                    System.out.println("True " + count);
                } else {
                    System.out.println("False: " + testScore + " != " + score + " " + count);
                }

                board.clear();
                //break;
                if (count >= 100) {
                    return;
                }
            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.printf("%s not found", filename);
        }
        
        

    }

    

}
