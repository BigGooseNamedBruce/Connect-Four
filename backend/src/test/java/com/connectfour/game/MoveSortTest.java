package com.connectfour.game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MoveSortTest {

    @Test
    void returnsMovesFromHighestToLowestScore() {
        MoveSorter sorter = new MoveSorter();
        sorter.add(5L, 49);
        sorter.add(3L, 20);
        sorter.add(1L, 40);

        assertEquals(5L, sorter.getNext()); // score 49
        assertEquals(1L, sorter.getNext()); // score 40
        assertEquals(3L, sorter.getNext()); // score 20
    }

    @Test
    void returnsZeroWhenEmpty() {
        MoveSorter sorter = new MoveSorter();
        assertEquals(0L, sorter.getNext());
    }

    @Test
    void handlesEqualScoresWithoutLosingMoves() {
        MoveSorter sorter = new MoveSorter();
        sorter.add(7L, 10);
        sorter.add(9L, 10);

        long first = sorter.getNext();
        long second = sorter.getNext();
        assertEquals(16L, first + second); // both moves are returned exactly once
        assertEquals(0L, sorter.getNext());
    }
}
