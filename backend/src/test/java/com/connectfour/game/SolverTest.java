package com.connectfour.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SolverTest {

    // One shared solver: the transposition table only ever stores valid bounds, so it is safe
    // (and much faster than reloading the opening book) to reuse it across cases.
    private static final Solver solver = new Solver();

    private static Bitboard boardFrom(String moves) {
        Bitboard board = new Bitboard();
        board.load(moves);
        return board;
    }

    private static Player toMove(String moves) {
        // Red always starts; the side to move flips with every disc played.
        return (moves.length() % 2 == 0) ? Player.RED : Player.YELLOW;
    }

    // The six Pascal Pons benchmark sets, spanning opening -> endgame and easy -> hard.
    static final String[] TEST_SETS = {
        "Test_L1_R1_Begin_Easy.txt",
        "Test_L1_R2_Begin_Medium.txt",
        "Test_L1_R3_Begin_Hard.txt",
        "Test_L2_R1_Middle_Easy.txt",
        "Test_L2_R2_Middle_Medium.txt",
        "Test_L3_R1_End_Easy.txt",
    };

    // Positions sampled per file for the correctness suite - kept small so `mvn test` stays quick.
    // The full per-file timing lives in the JMH benchmark (com.connectfour.benchmark.SolverBenchmark).
    private static final int SAMPLE_PER_FILE = 20;

    /** Feeds a sample of known positions from every test set with their expected exact scores. */
    static Stream<Arguments> knownPositions() throws IOException {
        List<Arguments> cases = new ArrayList<>();
        for (String testSet : TEST_SETS) {
            List<String> lines = Files.readAllLines(Path.of("src/test/resources", testSet));
            int limit = Math.min(SAMPLE_PER_FILE, lines.size());
            for (int i = 0; i < limit; i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split(" ");
                cases.add(Arguments.of(testSet, parts[0], Integer.parseInt(parts[1])));
            }
        }
        return cases.stream();
    }

    @ParameterizedTest(name = "{0}: solve({1}) == {2}")
    @MethodSource("knownPositions")
    void solvesToExactScore(String testSet, String moves, int expectedScore) {
        Bitboard board = boardFrom(moves);
        assertEquals(expectedScore, solver.solve(board, toMove(moves)),
                "wrong score in " + testSet + " for position " + moves);
    }

    @Test
    void takesImmediateVerticalWin() {
        Bitboard board = new Bitboard();
        board.placeDisc(3, Player.RED);
        board.placeDisc(3, Player.RED);
        board.placeDisc(3, Player.RED);

        assertEquals(3, solver.findBestMove(board, Player.RED));
        assertEquals(3, solver.findBestMove(board, Player.RED, Solver.MIN_DIFFICULTY));
        assertEquals(3, solver.findBestMove(board, Player.RED, Solver.MAX_DIFFICULTY));
    }

    @Test
    void blocksOpponentsImmediateWinAtPerfectDifficulty() {
        // Yellow threatens a vertical win in column 0; perfect play must block there.
        // (Lower difficulties are intentionally allowed to miss the block so they are beatable.)
        Bitboard board = new Bitboard();
        board.placeDisc(0, Player.YELLOW);
        board.placeDisc(0, Player.YELLOW);
        board.placeDisc(0, Player.YELLOW);

        assertEquals(0, solver.findBestMove(board, Player.RED, Solver.MAX_DIFFICULTY));
    }

    @Test
    void everyDifficultyReturnsALegalColumn() {
        Bitboard board = boardFrom("1234567123");
        Player player = toMove("1234567123");
        for (int difficulty = Solver.MIN_DIFFICULTY; difficulty <= Solver.MAX_DIFFICULTY; difficulty++) {
            int move = solver.findBestMove(board, player, difficulty);
            assertTrue(move >= 0 && move < Bitboard.BOARD_WIDTH,
                    "illegal column " + move + " at difficulty " + difficulty);
        }
    }

    @Test
    void difficultyIsClampedToValidRange() {
        Bitboard board = new Bitboard();
        int low = solver.findBestMove(board, Player.RED, -100);
        int high = solver.findBestMove(board, Player.RED, 100);
        assertTrue(low >= 0 && low < Bitboard.BOARD_WIDTH);
        assertTrue(high >= 0 && high < Bitboard.BOARD_WIDTH);
    }
}
