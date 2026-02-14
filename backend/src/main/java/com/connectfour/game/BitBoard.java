package com.connectfour.game;

/**
 * This file contains all the methods related to the Connect Four board
 * 
 * @author Brayden T
 * 
 */

public class BitBoard {
    
    // Board constants
    public static final int BOARD_HEIGHT = 6;
    public static final int BOARD_WIDTH = 7;

    // Scoring constans for the solver
    public static final int MIN_SCORE = -(BOARD_WIDTH * BOARD_HEIGHT) / 2 + 3;
    public static final int MAX_SCORE = (BOARD_WIDTH * BOARD_HEIGHT + 1) / 2 - 3;

    // Bitmasks for board bitwise operations
    private static final long BOTTOM_MASK = bottomMask();
    private static final long BOARD_MASK = BOTTOM_MASK * ((1L << BOARD_HEIGHT) - 1);
    private static final long[] COLUMN_MASK = generateColumnMask();
    private static final long[] BOTTOM_MASK_COLUMN = generateBottomMaskColumn();

    // Initializing instance variables
    private long redBoard;
    private long yellowBoard;
    private long mask;
    private int spacesLeft;

    /**
     * Default constructor that will initialize an empty board
     */
    public BitBoard() {
        clear();
    }

    /**
     * Parameterized constructor that will inialize a copy of a board
     * 
     * @param bitBoard A BitBoard object that will be copied from
     */
    public BitBoard(BitBoard bitBoard) {
        this.redBoard = bitBoard.redBoard;
        this.yellowBoard = bitBoard.yellowBoard;
        this.mask = bitBoard.mask;
        this.spacesLeft = bitBoard.spacesLeft;
    }

    /**
     * This method resets the board to its default settings
     * 
     */
    public void clear() {
        redBoard = 0;
        yellowBoard = 0;
        mask = 0;
        spacesLeft = BOARD_HEIGHT * BOARD_WIDTH;
    }


    
    /**
     * Given a column and a player, this method will place a disc in that column for the given player
     * 
     * @param col An int representing the column a dics is being placed
     * @param player A char representing the player
     */
    public void placeDisc(int col, Player player) {
        // Calculates the cell the disc will be placed at for the other placeDisc() method
        placeDisc(mask + BOTTOM_MASK_COLUMN[col], player);
    }

    /**
     * Given a number representing the cell a disc will be placed and a player, 
     * this method will place a disc in that cell for the given player
     * 
     * @param move A long representing the cell the disc will be placed at
     * @param player A char representing the player
     */
    public void placeDisc(long move, Player player) {
        
        // Places the disc at the given cell
        mask |= move;
        
        // Checks which player board to update
        if (player == Player.RED) {
            redBoard = mask ^ yellowBoard;
        } else {
            yellowBoard = mask ^ redBoard;
        }

        spacesLeft--;
    }


    /**
     * Given a column, this method will remove the top disc in that column
     * 
     * @param col An int representing the column that the disc will be removed from
     */
    public void removeDisc(int col) {
        long move = ~((COLUMN_MASK[col] & mask) >> 1) & (COLUMN_MASK[col] & mask);
        removeDisc(move);
    }

    public void removeDisc(long move) {

        mask = mask ^ move;

        // Removes the piece from the player or opponent board
        if ((move & redBoard) != 0) {
            redBoard = mask ^ yellowBoard;
        } else {
            yellowBoard = mask ^ redBoard;
        }

        // Adds a space to the total amount of spots left since a piece has been removed 
        spacesLeft++;   
    }

    /**
     * This method checks if a column if full or not. It will
     * return true if it is full and false if it isn't
     * 
     * @param col An int representing the column being checked
     * @return A boolean determining if the column if full
     */
    public boolean isColumnFull(int col) {
        return (mask & topMaskColumn(col)) != 0;
    }


    /**
     * Given a player, this wrapper method will check if that player has won or not
     * 
     * @param player A char representing the player ('r' is red and 'y' is yellow)
     * @return A boolean determining if the given player has won
     */
    public boolean checkWinner(Player player) {

        // Gets the board to check the winner for
        if (player == Player.RED) {
            return checkWinner(redBoard);
        } else {
            return checkWinner(yellowBoard);
        }
    }

    /**
     * Checks if a player can win within the next move
     * 
     * @param player A char representing the player
     * @return A boolean determining if the given player can win within the next move
     */
    public boolean canWinNext(Player player) {
        return (winningPosition(player) & possible()) != 0;
    }

    public long possibleNonLosingMoves(Player player) {

        long possibleMask = possible();
        long opponentWin = winningPosition(Player.opponent(player));
        // if (player == Player.RED) {
        //     opponentWin = winningPosition(Player.opponent(player));
        // } else {
        //     opponentWin = winningPosition(Player.opponent(player));
        // }
        long forcedMoves = possibleMask & opponentWin;
        if (forcedMoves != 0) {
            if ((forcedMoves & (forcedMoves - 1)) != 0) {
                return 0;
            } else {
                possibleMask = forcedMoves;
            }
        }

        return possibleMask & ~(opponentWin >> 1);
    }



    public int moveScore(long move, Player player) {
        if (player == Player.RED) {
            return Long.bitCount(computeWinningPosition(redBoard | move));
        } else {
            return Long.bitCount(computeWinningPosition(yellowBoard | move));
        }
    }
    

    /**
     * Generates a unique key for any Connect Four position
     * 
     * @return A long representing a unique key for a given position
     */
    public long key() {
        return redBoard + BOTTOM_MASK + mask;
    }

    /**
     * Given a move from MoveSorter, convert it to a column
     * 
     * @param move
     * @return
     */
    public int moveColumn(long move) {
        //return Long.numberOfTrailingZeros(mask ^ (move | mask)) / BOARD_WIDTH;

        return Long.numberOfTrailingZeros(move) / 7;
    }



    public Player load(String position) {
        Player player = Player.RED;
        
        for (int i = 0; i < position.length(); i++) {
            //System.out.println(s.charAt(i));
            placeDisc(Character.getNumericValue(position.charAt(i) - 1), player);
            player = Player.opponent(player);
        }
        return player;
    }



        public String[][] toArray() {

        // Initializes a string of the binary representations of the player and opponent board
        String redBoardString = boardToBinaryString(redBoard);
        String yellowBoardString = boardToBinaryString(yellowBoard);

        String[][] boardArray = new String[6][7];
        //System.out.println(Arrays.toString(boardArray));

        // Iterates vertically over the board
        for (int row = 1; row < BOARD_HEIGHT + 1; row++) {
            // Iterates horizontally
            for (int col = BOARD_WIDTH - 1; col > -1; col--) {

                if (redBoardString.charAt(row + BOARD_WIDTH * col) == '1') {
                    boardArray[row - 1][BOARD_WIDTH - 1 - col] = "red";
                } else if (yellowBoardString.charAt(row + BOARD_WIDTH * col) == '1') {
                    boardArray[row - 1][BOARD_WIDTH - 1 - col] = "yellow";
                } else {
                    boardArray[row - 1][BOARD_WIDTH - 1 - col] = null;
                }
            }
        }

        return boardArray;
    }


    
    /**
     * Returns a visual representation of the Connect Four board
     * 
     * @return A string of the Connect Four Board
     */
    @Override
    public String toString() {
        
        // Initializes a string of the binary representations of the player and opponent board
        String redBoardString = boardToBinaryString(redBoard);
        String yellowBoardString = boardToBinaryString(yellowBoard);
        String boardString = "-----------------------------\n";

        // Iterates vertically over the board
        for (int row = 1; row < BOARD_HEIGHT + 1; row++) {
            boardString += "|";
            // Iterates horizontally
            for (int col = BOARD_WIDTH - 1; col > -1; col--) {
                if (redBoardString.charAt(row + BOARD_WIDTH * col) == '1') {
                    boardString += " X |";
                } else if (yellowBoardString.charAt(row + BOARD_WIDTH * col) == '1') {
                    boardString += " O |";
                } else {
                    boardString += "   |";
                }
            }

            boardString += "\n-----------------------------\n";
        }

        return boardString;
    }




    /**
     * Converts a board position to a binary string
     * 
     * @param board A long representing a board
     * @return A binary string of the board
     */
    public static String boardToBinaryString(long board) {
        String boardString = String.format("%49s", Long.toBinaryString(board));
        boardString = boardString.replace(" ", "0");

        return boardString;
    }







    /**
     * Gets a bitmap of all the possible playable moves 
     * 
     * @return A bitmap of all the possible playable moves 
     */
    private long possible() {
        return (mask + BOTTOM_MASK) & BOARD_MASK;
    }


    private long winningPosition(Player player) {

        if (player == Player.RED) {
            return computeWinningPosition(redBoard);
        } else {
            return computeWinningPosition(yellowBoard);
        }
    } 


        /**
     * Given a board, this helper method will determine if that board contains a connect four
     * 
     * @param board A long representing a player board
     * @return A boolean determining if the given board contains a connect four
     */
    private boolean checkWinner(long board) {

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


    /**
     * Calculates a bitmap of all the possible connect fours within the next move
     * 
     * @param board A long representing the board
     * @return A bitmap of type long containing all the possible connect fours
     */
    private long computeWinningPosition(long board) {

        // Checks if a vertically move can lead to a win
        long r = (board << 1) & (board << 2) & (board << 3);


        // Checks if a horizontally move can lead to a win
        long p = (board << (BOARD_HEIGHT + 1)) & (board << 2 * (BOARD_HEIGHT + 1));

        r |= p & (board << 3 * (BOARD_HEIGHT + 1));
        r |= p & (board >> (BOARD_HEIGHT + 1));
        p >>= 3*(BOARD_HEIGHT + 1);
        r |= p & (board << (BOARD_HEIGHT + 1));
        r |= p & (board >> 3 * (BOARD_HEIGHT + 1));

        // Checks if a diagonally upwards and to the right move can lead to win
        p = (board << (BOARD_HEIGHT+2)) & (board << 2*(BOARD_HEIGHT+2));
        r |= p & (board << 3*(BOARD_HEIGHT+2));
        r |= p & (board >> (BOARD_HEIGHT+2));
        p >>= 3*(BOARD_HEIGHT+2);
        r |= p & (board << (BOARD_HEIGHT+2));
        r |= p & (board >> 3*(BOARD_HEIGHT+2));

        // Checks if a diagonally downwards and to the right move can lead to win
        p = (board << BOARD_HEIGHT) & (board << 2*BOARD_HEIGHT);
        r |= p & (board << 3*BOARD_HEIGHT);
        r |= p & (board >> BOARD_HEIGHT);
        p >>= 3*BOARD_HEIGHT;
        r |= p & (board << BOARD_HEIGHT);
        r |= p & (board >> 3*BOARD_HEIGHT);

        
        return r & (BOARD_MASK ^ mask);

    }




    /**
     * Checks if the game has become a draw
     * 
     * @return A boolean determining if the game currently a draw
     */
    public boolean checkDraw() {
        return spacesLeft == 0;
    }

    /**
     * Accessor method to get the amount of empty cells on the board
     * 
     * @return An int representing the amount of empty cells on the board
     */
    public int getSpacesLeft() {
        return spacesLeft;
    }




    
    /**
     * Generates a mask for the entire given column For example, the following 
     * would be the column mask for column 3 and for a 7x6 Connect Four board
     * 
     * 0001000
     * 0001000
     * 0001000
     * 0001000
     * 0001000
     * 0001000
     * 0001000
     * 
     * @param col An int representing the column for which the column mask needs to be found
     * @return A long representing the column mask
     */
    public static long columnMask(int col) {
        //return ((1L << BOARD_HEIGHT) - 1) << (col * (BOARD_HEIGHT + 1));
        return BitBoard.COLUMN_MASK[col];
    }

    private static long[] generateColumnMask() {
        long[] columnMask = new long[BOARD_WIDTH];
        for (int i = 0; i < columnMask.length; i++) {
            columnMask[i] = ((1L << BOARD_HEIGHT) - 1) << (i * (BOARD_HEIGHT + 1));
        }
        return columnMask;
    }

    /**
     * Generates a bottom mask for a board. For example, the following 
     * would be the bottom mask for a 7x6 Connect Four board
     * 
     * 0000000
     * 0000000
     * 0000000
     * 0000000
     * 0000000
     * 0000000
     * 1111111
     * 
     * @return A long representing the bottom mask
     */
    private static long bottomMask() {
        long boardMask = 0;
        for (int i = 0; i < BOARD_WIDTH; i++) {
            boardMask += 1L << (BOARD_HEIGHT + 1) * i;
        }
        return boardMask;
    }

    /**
     * Generates a top mask for a column. For example, the following 
     * would be the top mask for column 3 and for a 7x6 Connect Four board
     * 
     * 0001000
     * 0000000
     * 0000000
     * 0000000
     * 0000000
     * 0000000
     * 0000000
     * 
     * @param col An int representing the column for which the top mask needs to be found
     * @return A long representing the top mask for that column
     */
    private static long topMaskColumn(int col) {
        return (1L << (BOARD_HEIGHT - 1)) << (col * (BOARD_HEIGHT + 1));
    }

    /**
     * Generates a bottom mask for a column. For example, the following 
     * would be the bottom mask for column 3 and for a 7x6 Connect Four board
     * 
     * 0000000
     * 0000000
     * 0000000
     * 0000000
     * 0000000
     * 0000000
     * 0001000
     * 
     * @param col An int representing the column for which the bottom mask needs to be found
     * @return A long representing the bottom mask for that column
     */
    private static long bottomMaskColumn(int col) {
        return 1L << (col * (BOARD_HEIGHT + 1));
    }

    private static long[] generateBottomMaskColumn() {
        long[] bottomMaskColumn = new long[BOARD_WIDTH];
        for (int col = 0; col < BOARD_WIDTH; col++) {
            bottomMaskColumn[col] = 1L << (col * (BOARD_HEIGHT + 1));
        }
        return bottomMaskColumn;
    }

}
