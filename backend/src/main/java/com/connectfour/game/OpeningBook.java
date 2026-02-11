package com.connectfour.game;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;



public class OpeningBook {

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

    private String filepath;
    private int entries;
    private final int BUFFER_SIZE = 1 << 16;
    private Long2ByteOpenHashMap map;

    public OpeningBook(BookType book) {
        this.filepath = book.getPath();
        this.entries = book.getEntries();
        this.map = new Long2ByteOpenHashMap(book.getEntries(), 0.75f);
        map.defaultReturnValue(Byte.MIN_VALUE);
        load();
    }

    public void load() {
        try (DataInputStream in = new DataInputStream(
            new BufferedInputStream(new FileInputStream(filepath), BUFFER_SIZE))) {

            for (int i = 0; i < entries; i++) {
                long key = in.readLong();
                byte value = in.readByte();
                map.put(key, value);
            }
        }
        catch (FileNotFoundException e) {

        } catch (IOException e) {
            
        }
    }

    public boolean contains(long key) {
        return map.containsKey(key);
    }

    public byte get(long key) {
        return map.get(key);
    }

    @Override
    public String toString() {
        return map.toString();
    }
}