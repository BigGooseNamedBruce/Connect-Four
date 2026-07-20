package com.connectfour.game;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.connectfour.game.OpeningBook.BookType;

class OpeningBookTest {

    private static final OpeningBook book = new OpeningBook(BookType.EIGHT_MOVES);
    private static final Solver solver = new Solver();

    @Test
    void unknownKeyReturnsSentinel() {
        // A raw value that is not a valid position key must fall back to the default.
        assertTrue(book.get(1234567890123L) == Byte.MIN_VALUE);
    }

    @ParameterizedTest(name = "book score matches solver for \"{0}\"")
    @ValueSource(strings = {"44", "33", "4433", "3344"})
    void bookScoreMatchesSolverWhenPresent(String moves) {
        Bitboard board = new Bitboard();
        Player toMove = board.load(moves);

        byte bookScore = book.get(board.key());
        // Only assert when the position is actually stored in the book.
        assumeTrue(bookScore != Byte.MIN_VALUE, "position not in opening book");

        assertTrue(bookScore >= Bitboard.MIN_SCORE - 1 && bookScore <= Bitboard.MAX_SCORE + 1,
                "book score out of expected range: " + bookScore);
        assertTrue(bookScore == solver.solve(board, toMove),
                "book score disagrees with solver for " + moves);
    }
}
