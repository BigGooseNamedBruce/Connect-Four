package com.connectfour.game;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;

/**
 * A primative hashmap that uses the position key as the key of the hashmap and the position's
 * score as the value. This hashmap contains position key and score the all possible positions
 * of the first six or eight moves where no player can win within one move
 * 
 * @author Brayden T
 */

public class OpeningBook {
    // Binary file reader constant
    private final int BUFFER_SIZE = 1 << 16;

    // Initializing instance variables
    private String filepath;
    private int entries;
    private Long2ByteOpenHashMap map;

    /**
     * Represents opening book with different move lengths
     */
    public enum BookType {
        EIGHT_MOVES("src/main/resources/opening_book_8_moves.bin", 8, 196686),
        SIX_MOVES("src/main/resources/opening_book_6_moves.bin", 6, 19445),
        EIGHT_MOVES_DEBUG("backend/src/main/resources/opening_book_8_moves.bin", 8, 196686),
        SIX_MOVES_DEBUG("backend/src/main/resources/opening_book_6_moves.bin", 6, 19445);

        private final String path;
        private final int moveLength;
        private final int entries;

        BookType(String path, int moveLength, int entries) {
            this.path = path;
            this.moveLength = moveLength;
            this.entries = entries;
        }

        public String getPath() {
            return path;
        }

        public int getMoveLength() {
            return moveLength;
        }

        public int getEntries() {
            return entries;
        }
    }

    /**
     * Parameterized constructor that loads the appropriate opening book
     * 
     * @param book The opening book that is to be loaded
     */
    public OpeningBook(BookType book) {
        this.filepath = book.getPath();
        this.entries = book.getEntries();
        this.map = new Long2ByteOpenHashMap(book.getEntries(), 0.75f);
        
        // Loads opening book
        load();
        map.defaultReturnValue(Byte.MIN_VALUE);
    }

    /**
     * Gets the score of a position given the position's key
     * 
     * @param key The key of the position
     * @return The score of the position
     */
    public byte get(long key) {
        return map.get(key);
    }

    /**
     * Converts the opening book into a string representation
     * 
     * @return A string representation of the opening book
     */
    @Override
    public String toString() {
        return map.toString();
    }

    /**
     * Fills the hashmap with position keys and its scores from a binary file.
     * The position key is 4 bytes and the score is 1 byte
     */
    private void load() {
        // Loads the binary file
        try (DataInputStream in = new DataInputStream(
            new BufferedInputStream(new FileInputStream(filepath), BUFFER_SIZE))) {
            
            // Adds all rows in the file into the hashmap
            for (int i = 0; i < entries; i++) {
                long key = in.readLong();
                byte value = in.readByte();
                map.put(key, value);
            }
        }
        
        // File is not found
        catch (FileNotFoundException e) {
            System.out.println("Cannot find opening book: " + e.getMessage());
        } 
        // Problem with reading the binary file
        catch (IOException e) {
            System.out.println("Cannot read opening book: " + e.getMessage());
        }
    }
}