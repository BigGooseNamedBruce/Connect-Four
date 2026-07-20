package com.connectfour.benchmark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import com.connectfour.game.Bitboard;
import com.connectfour.game.Player;
import com.connectfour.game.Solver;

/**
 * JMH benchmark of the exact solver over each of the six Pascal Pons position sets. For every file
 * (the {@code testSet} parameter) it reports the average time to solve a single position, cycling
 * through up to {@link #MAX_POSITIONS} positions from that file - i.e. how fast the solver crunches
 * each difficulty tier.
 *
 * Run from the backend module (all six sets):
 *   mvn -Pbenchmark test-compile exec:java
 *
 * Benchmark a single set only, e.g. the hardest:
 *   mvn -Pbenchmark test-compile exec:java \
 *     -Dexec.args="com.connectfour.benchmark.SolverBenchmark -p testSet=Test_L1_R3_Begin_Hard.txt"
 *
 * The solver (and its transposition table) is created once per trial, so results reflect repeated
 * queries against a warm cache - the same way it runs during a game.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2, time = 2)
@Measurement(iterations = 3, time = 2)
@Fork(1)
@State(Scope.Thread)
public class SolverBenchmark {

    private static final int MAX_POSITIONS = 1000;

    @Param({
        "Test_L3_R1_End_Easy.txt",
        "Test_L2_R1_Middle_Easy.txt",
        "Test_L2_R2_Middle_Medium.txt",
        "Test_L1_R1_Begin_Easy.txt",
        "Test_L1_R2_Begin_Medium.txt",
        "Test_L1_R3_Begin_Hard.txt",
    })
    public String testSet;

    private String[] positions;
    private Solver solver;
    private int index;

    @Setup(Level.Trial)
    public void loadPositions() throws IOException {
        List<String> loaded = new ArrayList<>();
        for (String line : Files.readAllLines(Path.of("src/test/resources", testSet))) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (loaded.size() >= MAX_POSITIONS) {
                break;
            }
            loaded.add(trimmed.split(" ")[0]);
        }
        positions = loaded.toArray(new String[0]);
        solver = new Solver();
        index = 0;
    }

    /**
     * Solves one position per invocation, cycling through the file, so the reported average time is
     * the solver's cost per position for this set.
     *
     * @return the solved score (returned so JMH doesn't optimise the call away)
     */
    @Benchmark
    public int solvePosition() {
        String moves = positions[index];
        index = (index + 1) % positions.length;
        Bitboard board = new Bitboard();
        Player toMove = board.load(moves);
        return solver.solve(board, toMove);
    }
}
