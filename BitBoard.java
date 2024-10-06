public class BitBoard {
    
    public final int BOARD_HEIGHT = 6;
    public final int BOARD_WIDTH = 7;

    private long playerBoard;
    private long mask;
    private long opponentBoard;
    private int spacesLeft;

    public BitBoard() {
        playerBoard = 0;
        opponentBoard = 0;
        mask = 0;
        spacesLeft = BOARD_HEIGHT * BOARD_WIDTH;
    }

    public boolean isColumnFull(int col) {
        return (mask & topMask(col)) != 0;
    }

    private long topMask(int col) {
        return (1L << (BOARD_HEIGHT - 1)) << (col * (BOARD_HEIGHT + 1));
    }
    
    /**
     * 
     * @param col
     * @param player 1 represents the player and 0 represents the opponent
     */
    public void placeDisc(int col, int player) {

        mask |= mask + bottomMask(col);

        if (player == 1) {
            playerBoard = mask ^ opponentBoard;
        } else {
            opponentBoard = mask ^ playerBoard;
        }

        spacesLeft--;
        
    }

    private long bottomMask(int col) {
        return 1L << (col * (BOARD_HEIGHT + 1));
    }

    public boolean checkWinner(int player) {

        // Gets the board to check the winner for
        long board;
        if (player == 1) {
            board = playerBoard;
        } else {
            board = opponentBoard;
        }

        // Checks for horizontal win
        long horizontalAlignment = board & (board >> (BOARD_HEIGHT + 1));
        if ((horizontalAlignment & (horizontalAlignment >> (2 * (BOARD_HEIGHT + 1)))) != 0) {
            return true;
        }

        // Checks for vertical win
        long verticalAlignment = board & (board >> 1);
        if ((verticalAlignment & (verticalAlignment >> 2)) != 0) {
            return true;
        }

         // Checks diagonally upwards and to the right if there is a connect 4
         long upwardsRightAlignment = board & (board >> (BOARD_HEIGHT + 2));
         if ((upwardsRightAlignment & (upwardsRightAlignment >> (2 * (BOARD_HEIGHT + 2)))) != 0) {
             return true;
         }

        // Checks diagonally downwards and to the right if there is a connect 4
        long downwardsRightAlignment = board & (board >> BOARD_HEIGHT);
        if ((downwardsRightAlignment & (downwardsRightAlignment >> (2 * BOARD_HEIGHT))) != 0) {
            return true;
        }

        return false;
    }

    public long getplayerBoard() {
        return playerBoard;
    }

    public long getopponentBoard() {
        return opponentBoard;
    }

    public int getSpacesLeft() {
        return spacesLeft;
    }

    @Override
    public String toString() {
        String stringBoard = Long.toBinaryString(mask);
        playerBoard = mask ^ opponentBoard;
        String p = Long.toBinaryString(playerBoard);
        String o = Long.toBinaryString(opponentBoard);
        //System.out.println(Long.toBinaryString(playerBoard));
        //System.out.println(Long.toBinaryString(opponentBoard));
        //System.out.println(stringBoard);
       //7415320.47444/*String a = "0".repeat((spacesLeft + BOARD_WIDTH) - 1) + stringBoard;
        //System.out.println(a + " " + a.length());

        //for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; i += 8) {
            //System.out.println(i + " " + (i + 7));
            //System.out.println(a.substring(i, i + 7));
        //}
        
        //for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH - 7 - 1; i += 7) {
            //System.out.println(i);
            //System.out.println(stringBoard.substring(i, i + 7));
        //}

        // char[][] board = new char[BOARD_HEIGHT][BOARD_WIDTH];
        

        stringBoard = String.format("%49s", stringBoard).replace(" ", "0");
        p = String.format("%49s", p).replace(" ", "0");
        o = String.format("%49s", o).replace(" ", "0");
        //p = String.format("%49s", p).replace(" ", "0").replace("1", "X");
        //o = String.format("%49s", o).replace(" ", "0").replace("1", "O");
        System.out.println(stringBoard);
        System.out.println(p);
        System.out.println(o);

        String s = "-----------------------------\n";
        for (int i = 0; i < BOARD_WIDTH; i++) {
            String row = "";
            s += "|";
            for (int j = BOARD_HEIGHT; j > -1; j--) {
                if (p.charAt(i + BOARD_WIDTH * j) == '1') {
                    s += " X |";
                } else if (o.charAt(i + BOARD_WIDTH * j) == '1') {
                    s += " O |";
                } else {
                    s += "   |";
                }
                System.out.print(stringBoard.charAt(i + BOARD_WIDTH * j) + " " + (i + BOARD_WIDTH * j) + "     ");
            }
            s += "\n-----------------------------\n";
            System.out.println();
            //System.out.println(row);
            //for (int col = 0; col < row.length(); i++) {
                //System.out.print(row.charAt(i));
            //}
        }

        return s;
    }

}
