import java.util.Arrays;

public class TranspositionTable {
    
    private class Entry {
        long key;
        byte value;

        public Entry() {
            key = 0;
            value = 0;
        }

        @Override
        public String toString() {
            return String.format("%d %d", key, value);
        }
        
    }

    //private Entry[] table;
    private long[] keys;
    private byte[] values;
    private int size;

    public TranspositionTable(int size) {
        //this.table = new Entry[size];
        this.keys = new long[size];
        this.values = new byte[size];
        this.size = size;
        reset();
    }

    public void reset() {
        //for (int i = 0; i < table.length; i++) {
            //table[i] = new Entry();
        //}
        for (int i = 0; i < size; i++) {
            keys[0] = 0;
            values [0] = 0;
        }
    }

    public void put(long key, byte value) {
        int index = index(key);
        keys[index] = key;
        values[index] = value;
        //table[index].key = key;
        //table[index].value = value;
    }

    public byte get(long key) {
        int index = index(key);
        if (keys[index] == key) {
            return values[index];
        }

        //if (table[index].key == key) {
            //return table[index].value;
        //}
        return 0;
    }

    private int index(long key) {
        //return (int) (key % table.length);
        return (int) (key & (size - 1));
    }

    @Override
    public String toString() {
        return Arrays.toString(keys) + "\n" + Arrays.toString(values);
    }


}
