package com.connectfour.game;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Paths;
import java.util.HashMap;
import java.nio.file.Paths;

public class OpeningBook {

    private TranspositionTable table;
    private String filepath;
    private BitBoard board;
    private HashMap<Long, Byte> map;

    public OpeningBook(TranspositionTable table, String filepath) {
        this.table = table;
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
                
                //System.out.printf("%d %d: %d\n", key, board.key(), value);
                //table.put(board.key(), (byte) value);
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

        // String fileString = "";

        // try (FileInputStream fis = new FileInputStream(filepath)) {

        //     while (fis.available() > 0) {

        //         byte[] keyBytes = new byte[4];
        //         if (fis.read(keyBytes) != 4) {
        //             break; 
        //         }

        //         ByteBuffer keyBuffer = ByteBuffer.wrap(keyBytes);
        //         keyBuffer.order(ByteOrder.BIG_ENDIAN);
        //         int key = keyBuffer.getInt();

        //         byte[] valueBytes = new byte[2];
        //         if (fis.read(valueBytes) != 2) {
        //             break;
        //         }
        //         ByteBuffer valueBuffer = ByteBuffer.wrap(valueBytes);
        //         valueBuffer.order(ByteOrder.BIG_ENDIAN);
        //         short value = valueBuffer.getShort();

        //         fileString += String.format("%d: %d\n", key, value);
        //     }
            
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        // return fileString;
        return map.toString();
    }
}

























// import java.io.BufferedWriter;
// import java.io.FileWriter;
// import java.io.IOException;
// import java.util.ArrayList;

// public class OpeningBook {

//     static int count = 0;
//     static BitBoard board = new BitBoard();
//     static Solver solver = new Solver(board);
//     static ArrayList<String> arr = new ArrayList<>();

//     public static void main(String[] args) {
//         // BitBoard board = new BitBoard();
//         // Solver solver = new Solver(board);
//         char player = 'r';
//         String pos = "7532455277545526111";
//         String filePath = "example.txt";
//         //String pos = "";

//         // for (int i = 0; i < pos.length(); i++) {
//         //     board.placeDisc(Character.getNumericValue(pos.charAt(i) - 1), player);
//         //     player = solver.getOpponent(player);
//         // }

//         player = board.load(pos, player);
//         System.out.println(solver.solve(player));

//         // try {
//         //     findPositions(0, "");
//         // } catch (InterruptedException e) {
//         //     System.out.println(e);
//         // }

        
//         //System.out.println(arr);

//         try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
//             // for (int i = 0; i < arr.size(); i++) {
//             //     board.clear();
//             //     player = 'r';

//             //     String pos = arr.get(i);
//             //     player = board.load(pos, player);
//             //     System.out.println(board);
//             //     int score = solver.solve(player);
//             //     System.out.printf("%s,%d\n", pos, score);
//             //     writer.write(String.format("%s,%d\n", pos, score));

                
//             // }
    
//             //compute(writer, 0, player, pos);
//         //     for (int i1 = 0; i1 < 7; i1++) {
//         //         if (!board.isColumnFull(i1)) {
//         //             board.placeDisc(i1, player);



//         //         } else {
//         //             continue;
//         //         }

//         //         for (int i2 = 0; i2 < 7; i2++) {
//         //             if (!board.isColumnFull(i2)) {
//         //                 board.placeDisc(i2, player);
//         //             } else {
//         //                 continue;
//         //             }

//         //             for (int i3 = 0; i3 < 7; i3++) {
//         //                 if (!board.isColumnFull(i3)) {
//         //                     board.placeDisc(i3, player);
//         //                 } else {
//         //                     continue;
//         //                 }

//         //                 for (int i4 = 0; i4 < 7; i4++) {
//         //                     if (!board.isColumnFull(i4)) {
//         //                         board.placeDisc(i4, player);
//         //                     } else {
//         //                         continue;
//         //                     }

//         //                     for (int i5 = 0; i5 < 7; i5++) {
//         //                         if (!board.isColumnFull(i5)) {
//         //                             board.placeDisc(i5, player);
//         //                         } else {
//         //                             continue;
//         //                         }

//         //                         for (int i6 = 0; i6 < 7; i6++) {
//         //                             if (!board.isColumnFull(i6)) {
//         //                                 board.placeDisc(i6, player);
//         //                             } else {
//         //                                 continue;
//         //                             }

//         //                             for (int i7 = 0; i7 < 7; i7++) {
//         //                                 if (!board.isColumnFull(i7)) {
//         //                                     board.placeDisc(i7, player);
//         //                                 } else {
//         //                                     continue;
//         //                                 }

//         //                                 for (int i8 = 0; i8 < 7; i8++) {
//         //                                     if (!board.isColumnFull(i8)) {
//         //                                         board.placeDisc(i8, player);
//         //                                     } else {
//         //                                         continue;
//         //                                     }

//         //                                     for (int i9 = 0; i9 < 7; i9++) {
//         //                                         if (!board.isColumnFull(i9)) {
//         //                                             board.placeDisc(i9, player);
//         //                                         } else {
//         //                                             continue;
//         //                                         }

//         //                                         if (count >= Math.pow(2, 23)) {
//         //                                             return;
//         //                                         }

//         //                                         int score = solver.solve(player);



//         //                                         count++;

                                                
//         //                                     }
                                            
//         //                                 }
//         //                             }
//         //                         }
//         //                     }
//         //                 }
//         //             }
//         //         }
//         //     }
            

//             // for (int i = 0; i < pos.length(); i++) {
//             //     //System.out.println(s.charAt(i));
//             //     board.placeDisc(Character.getNumericValue(pos.charAt(i) - 1), player);
//             //     player = (player == 'r') ? 'y': 'r';
//             // }

//             // int score;

//             // for (int i = 0; i < 7; i++) {
                
//             //     if (!board.isColumnFull(i)) {
//             //         board.placeDisc(i, player);
//             //         score = solver.solve(player);
//             //         board.removeDisc(i);
//             //         System.out.println(String.format("%s%d %d", pos, i, score));
//             //         writer.write(String.format("%s%d %d\n", pos, i, score));
//             //     } else {
//             //         //score = solver.solve(player);
//             //     }
                
//             //     // System.out.println(String.format("%s%d %d", pos, i, score));
//             //     // writer.write(String.format("%s%d %d\n", pos, i, score));
//             // }

//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }


//     public static void compute(BufferedWriter writer, int depth, char player, String pos) throws IOException {
//         if (depth >= 2 || count >= Math.pow(2, 23)) {
//             return;
//         }

//         for (int i = 0; i < 7; i++) {
//             if (!board.isColumnFull(i)) {
//                 String newPos = String.format("%s%d", pos, i+1);
//                 board.placeDisc(i, player);
//                 System.out.println(board);
//                 count++;
//                 char p;
//                 if (player == 'r') {
//                     p = 'y';
//                 } else {
//                     p = 'r';
//                 }
//                 int score = solver.solve(p);
//                 System.out.printf("%s %d\n", newPos, score);
//                 writer.write(String.format("%s %d\n", newPos, score));
//                 // System.out.printf("%s %d\n", pos, score);

//                 compute(writer, depth + 1, p, newPos);
        
//                 board.removeDisc(i);
//             }
//         }
//     }

//     public static void findPositions(int depth, String pos) throws InterruptedException {
//         if (depth >= 8) {
//             //System.out.println(pos);
//             return;
//         }

//         for (int i = 0; i < 7; i++) {
//             if (!board.isColumnFull(i)) {
//                 String newPos = String.format("%s%d", pos, i+1);
//                 arr.add(newPos);
//                 //Thread.sleep(500);
//                 //System.out.println(newPos);
//                 board.placeDisc(i, 'r');
//                 // System.out.printf("%s %d\n", pos, score);

//                 findPositions(depth + 1, newPos);
        
//                 board.removeDisc(i);
//             }
//         }
//     }
// }
