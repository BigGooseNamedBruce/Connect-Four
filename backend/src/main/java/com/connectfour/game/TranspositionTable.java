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

    private static final long EMPTY_KEY = Long.MIN_VALUE;

    private final int capacity;
    private final long[] keys;
    private final byte[] values;
    private final int[] prev;
    private final int[] next;

    private final int[] table;
    private final int tableSize;

    private int head = -1;
    private int tail = -1;
    private int size = 0;

    public TranspositionTable(int capacity) {
        this.capacity = capacity;
        keys = new long[capacity];
        values = new byte[capacity];
        prev = new int[capacity];
        next = new int[capacity];
        Arrays.fill(keys, EMPTY_KEY);

        tableSize = Integer.highestOneBit(capacity * 2 - 1) << 1;
        table = new int[tableSize];
        Arrays.fill(table, -1);
    }

    public byte get(long key) {
        int index = findIndex(key);
        if (index == -1) {
            return 0;
        }

        moveToFront(index);
        return values[index];
    }

    public synchronized void put(long key, byte value) {
        int index = findIndex(key);
        if (index != -1) {
            values[index] = value;
            moveToFront(index);
            return;
        }

        // Need to insert new
        if (size < capacity) {
            index = size++;
        } else {
            // Evict least recently used
            index = tail;
            removeFromHash(keys[index]);
            removeFromList(index);
        }

        keys[index] = key;
        values[index] = value;
        insertIntoHash(key, index);
        insertAtFront(index);
    }

    private int hash(long key) {
        key ^= (key >>> 33);
        key *= 0xff51afd7ed558ccdL;
        key ^= (key >>> 33);
        key *= 0xc4ceb9fe1a85ec53L;
        key ^= (key >>> 33);
        return (int) key & (tableSize - 1);
    }

    private void insertIntoHash(long key, int index) {
        int i = hash(key);
        while (table[i] != -1) {
            i = (i + 1) & (tableSize - 1);
        }
        table[i] = index;
    }

    private void removeFromHash(long key) {
        int i = hash(key);
        while (true) {
            int idx = table[i];
            if (idx == -1) {
                return;
            }

            if (keys[idx] == key) {
                table[i] = -1;

                int j = (i + 1) & (tableSize - 1);
                while (table[j] != -1) {
                    int rehashIdx = table[j];
                    table[j] = -1;
                    insertIntoHash(keys[rehashIdx], rehashIdx);
                    j = (j + 1) & (tableSize - 1);
                }

                return;
            }

            i = (i + 1) & (tableSize - 1);
        }
    }

    private int findIndex(long key) {
        int i = hash(key);
        while (true) {
            int idx = table[i];
            if (idx == -1) {
                return -1;
            }
            if (keys[idx] == key) {
                return idx;
            }
            i = (i + 1) & (tableSize - 1);
        }
    }


    private void moveToFront(int index) {
        if (index == head) {
            return;
        }
        removeFromList(index);
        insertAtFront(index);
    }

    private void insertAtFront(int index) {
        prev[index] = -1;
        next[index] = head;

        if (head != -1) {
            prev[head] = index;
        }
        head = index;

        if (tail == -1) {
            tail = index;
        }
    }

    private void removeFromList(int index) {
        int p = prev[index];
        int n = next[index];

        if (p != -1) {
            next[p] = n;
        } else {
            head = n;
        }

        if (n != -1) {
            prev[n] = p;
        } else {
            tail = p;
        }

        prev[index] = -1;
        next[index] = -1;
        
    }

    @Override
    public String toString() {
        String tableString = "";
        int curr = head;
        while (curr != -1) {
            tableString += ("[" + keys[curr] + " → " + values[curr] + "] ");
            curr = next[curr];
        }
        return tableString;
    }




}
