import java.util.Arrays;

public class TranspositionTable {
    
    private static class Entry {
        long key;
        byte value;
    }

    private Entry[] table;

    public TranspositionTable(int size) {
        this.table = new Entry[size];
        reset();
    }

    public void reset() {
        Arrays.fill(table, new Entry());
    }

    public void put(long key, byte value) {
        int index = index(key);
        table[index].key = key;
        table[index].value = value;
    }

    public byte get(long key) {
        int index = index(key);
        if (table[index].key == key) {
            return table[index].value;
        }
        return 0;
    }

    private int index(long key) {
        return (int) (key % table.length);
    }

    @Override
    public String toString() {
        return Arrays.toString(table);
    }


}
