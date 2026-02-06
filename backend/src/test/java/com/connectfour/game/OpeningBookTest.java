package com.connectfour.game;

import com.connectfour.game.OpeningBook;

public class OpeningBookTest {
    public static void main(String[] args) {
        String FILEPATH = "backend/src/main/resources/opening_book.bin";
        OpeningBook openingBook = new OpeningBook(FILEPATH);
        openingBook.load();
    }
}
