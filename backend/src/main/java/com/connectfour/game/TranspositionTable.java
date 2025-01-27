package com.connectfour.game;

import java.util.Arrays;

/**
 * This file contains all the methods related to the transposition table.
 * The transposition table will store key-pair values of a position and its score
 * 
 * @author Brayden T
 * 
 */

public class TranspositionTable {

    // Initializes instance variables
    private long[] keys;
    private byte[] values;
    private int size;

    /**
     * Parameterized constructor that will inialize a transposition table
     * 
     * @param size An int representing the length of the transposition table
     */
    public TranspositionTable(int size) {
        this.keys = new long[size];
        this.values = new byte[size];
        this.size = size;
    }

    /**
     * Puts a key-pair value into the transposition table. If a key hashes 
     * to a spot that is already filled, the new key-pair value will replace 
     * the old key-pair value
     * 
     * @param key A long representing the position
     * @param value A byte representing the position's score
     */
    public void put(long key, byte value) {
        int index = index(key);
        keys[index] = key;
        values[index] = value;
    }

    /**
     * Gets the value associated with the given key
     * 
     * @param key A long representing the position
     * @return A byte representing the position's score
     */
    public byte get(long key) {
        int index = index(key);
        if (keys[index] == key) {
            return values[index];
        }
        return 0;
    }

    /**
     * A hash function that will return the index a key-pair value will be stored at
     * 
     * @param key A long representing a position
     * @return An int representing the position the index in key and valeue array
     */
    private int index(long key) {
        return (int) (key & (size - 1));
    }

    @Override
    public String toString() {
        return Arrays.toString(keys) + "\n" + Arrays.toString(values);
    }
}
