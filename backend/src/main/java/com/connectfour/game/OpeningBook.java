package com.connectfour.game;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Paths;
import java.util.HashMap;

public class OpeningBook {

    private String filepath;
    private HashMap<Long, Byte> map;

    public OpeningBook(String filepath) {
        this.filepath = Paths.get(filepath).toString();
        this.map = new HashMap<>();
    }

    public void load() {

        BitBoard board = new BitBoard();

        try (FileInputStream fis = new FileInputStream(filepath)) {

            while (fis.available() > 0) {

                byte[] keyBytes = new byte[4];
                if (fis.read(keyBytes) != 4) {
                    break; 
                }

                ByteBuffer keyBuffer = ByteBuffer.wrap(keyBytes);
                keyBuffer.order(ByteOrder.BIG_ENDIAN);
                int key = keyBuffer.getInt();

                byte[] valueBytes = new byte[2];
                if (fis.read(valueBytes) != 2) {
                    break;
                }
                ByteBuffer valueBuffer = ByteBuffer.wrap(valueBytes);
                valueBuffer.order(ByteOrder.BIG_ENDIAN);
                short value = valueBuffer.getShort();

                board.load(String.valueOf(key));
                
                map.put(board.key(), (byte) value);

                board.clear();
            }
            
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.printf("Error: Cannot find %s\n", filepath);
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