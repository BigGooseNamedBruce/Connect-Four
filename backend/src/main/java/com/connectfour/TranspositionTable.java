package com.connectfour;

import java.util.Arrays;

public class TranspositionTable {

    private long[] keys;
    private byte[] values;
    private int size;

    public TranspositionTable(int size) {
        this.keys = new long[size];
        this.values = new byte[size];
        this.size = size;
        reset();
    }

    public void reset() {
        for (int i = 0; i < size; i++) {
            keys[0] = 0;
            values [0] = 0;
        }
    }

    public void put(long key, byte value) {
        int index = index(key);
        keys[index] = key;
        values[index] = value;
    }

    public byte get(long key) {
        int index = index(key);
        if (keys[index] == key) {
            return values[index];
        }
        return 0;
    }

    private int index(long key) {
        return (int) (key & (size - 1));
    }

    @Override
    public String toString() {
        return Arrays.toString(keys) + "\n" + Arrays.toString(values);
    }
}
