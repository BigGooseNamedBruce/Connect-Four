package com.connectfour;

import com.connectfour.game.*;

public class MoveSortTest {
    public static void main(String args[]) {
        MoveSorter m = new MoveSorter();
        m.add(5, 49);
        System.out.println(m);
        m.add(3, 20);
        System.out.println(m);
        m.add(1, 40);
        System.out.println(m);
        m.getNext();
        System.out.println(m);
    }
}
