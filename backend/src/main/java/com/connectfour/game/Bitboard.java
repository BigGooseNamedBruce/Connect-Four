package com.connectfour.game;

/**
 * A bitboard-based representation of a Connect Four board.
 * 
 * Each player is represented by a 64-bit bitmap where each playable cell 
 * corresponds to a single bit.
 * 
 * Each column uses BOARD_HEIGHT + 1 bits. The extra bit per column acts as 
 * a sentinel to simplify move generation and to detect when a column is full.
 * 
 * The class contains three bitmaps:
 * - redBoard: all cells occupied by the red player
 * - yellowBoard: all cells occupied by the yellow player
 * - mask: all cells positions on the board
 * 
 * @author Brayden T
 */

public class Bitboard {
    // Board constants
    public static final int BOARD_HEIGHT = 6;
    public static final int BOARD_WIDTH = 7;

    // Scoring constans for the solver
    public static final int MIN_SCORE = -(BOARD_WIDTH * BOARD_HEIGHT) / 2 + 3;
    public static final int MAX_SCORE = (BOARD_WIDTH * BOARD_HEIGHT + 1) / 2 - 3;

    // Bitmasks for board bitwise operations
    private static final long BOTTOM_MASK = generateBottomMask();
    private static final long BOARD_MASK = BOTTOM_MASK * ((1L << BOARD_HEIGHT) - 1);
    private static final long[] COLUMN_MASK = generateColumnMask();
    private static final long[] TOP_MASK_COLUMN = generateTopMaskColumn();
    private static final long[] BOTTOM_MASK_COLUMN = generateBottomMaskColumn();

    // Initializing instance variables
    private long redBoard;
    private long yellowBoard;
    private long mask;
    private int spacesLeft;

    /**
     * Default constructor initializing an empty board
     */
    public Bitboard() {
        clear();
    }

    /**
     * Copy constructor for creating a deep copy of an existing board
     * 
     * @param bitBoard A Bitboard instance that will be copied from
     */
    public Bitboard(Bitboard bitBoard) {
        this.redBoard = bitBoard.redBoard;
        this.yellowBoard = bitBoard.yellowBoard;
        this.mask = bitBoard.mask;
        this.spacesLeft = bitBoard.spacesLeft;
    }

    /**
     * Resets the board state to an empty board
     */
    public void clear() {
        redBoard = 0;
        yellowBoard = 0;
        mask = 0;
        spacesLeft = BOARD_HEIGHT * BOARD_WIDTH;
    }

    /**
     * Places a disc in a specified column for a given player. 
     * 
     * @param col The column index [0, 6] the disc is to be placed
     * @param player The player placing the disc
     */
    public void placeDisc(int col, Player player) {
        // Calculates the bitmap for where the disc to be placed
        long move = mask + BOTTOM_MASK_COLUMN[col];
        placeDisc(move, player);
    }

    /**
     * Places a disc given a bitmap representation of the move for a given player
     * 
     * @param move A bitmap representing where the disc is to be placed
     * @param player The player placing the disc
     */
    public void placeDisc(long move, Player player) {
        // Places the disc at the given bit index
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
     * Removes the top disc in a specified column.
     * 
     * @param col The column index [0, 6] the top disc is to be removed
     */
    public void removeDisc(int col) {
        // Calculates the bitmap for where the disc to be removed
        long move = ~((COLUMN_MASK[col] & mask) >> 1) & (COLUMN_MASK[col] & mask);
        removeDisc(move);
    }

    /**
     * Removed a disc given a bitmap representation of the move
     * 
     * @param move A bitmap representing where the disc is to be removed
     */
    public void removeDisc(long move) {
        // Places the disc at the given bit index
        mask = mask ^ move;

        // Checks which player board to update
        if ((move & redBoard) != 0) {
            redBoard = mask ^ yellowBoard;
        } else {
            yellowBoard = mask ^ redBoard;
        }

        spacesLeft++;   
    }

    /**
     * Checks if the specified column has reached its maximum height
     * 
     * @param col The column index [0, 6] to be checked
     * @return True if no more discs can be placed in the specified column
     */
    public boolean isColumnFull(int col) {
        return (mask & TOP_MASK_COLUMN[col]) != 0;
    }

    /**
     * Checks if a given winner has gotten connect four
     * 
     * @param player The player to check if they've won
     * @return True if the given player has won
     */
    public boolean checkWinner(Player player) {
        if (player == Player.RED) {
            return checkWinner(redBoard);
        } else {
            return checkWinner(yellowBoard);
        }
    }

    /**
     * Checks if a given player can win on their next move
     * 
     * @param player The player to check if they can win on their next move
     * @return True if the given player can win on their next move
     */
    public boolean canWinNext(Player player) {
        return (winningPosition(player) & possible()) != 0;
    }

    /**
     * Finds the bitmap of all possible moves where the given player doesn't immediately 
     * lose on the next move
     * 
     * @param player The player to find the bitmap for
     * @return A bitmap representing all possible moves the given does not lose on their 
     *         next turn
     */
    public long possibleNonLosingMoves(Player player) {
        // Calculates a bitmap where opponent player can win on their next move
        long possibleMask = possible();
        long opponentWin = winningPosition(Player.opponent(player));
        long forcedMoves = possibleMask & opponentWin;

        // Opponent can win on their next turn
        if (forcedMoves != 0) {
            // Opponent has more than winning move that the given player cannot stop
            if ((forcedMoves & (forcedMoves - 1)) != 0) {
                return 0;
            } 
            // Blocks opponent from winning on their next turn
            else {
                possibleMask = forcedMoves;
            }
        }

        return possibleMask & ~(opponentWin >> 1);
    }

    /**
     * Computes the score of a move given its bitmap representation
     * 
     * @param move A bitmap of the move to be scored
     * @param player The player who can the move
     * @return The score of the move
     */
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
     * @return A unique key for any given position
     */
    public long key() {
        return redBoard + BOTTOM_MASK + mask;
    }

    /**
     * Converts a move from its bitmap representation to a column index
     * 
     * @param move A bitmap of the move to be converted
     * @return A column index of the move
     */
    public int moveColumn(long move) {
        return Long.numberOfTrailingZeros(move) / 7;
    }

    /**
     * Loads a position into the board given it's string representation. A string 
     * representation of a connect 4 game will have the format "22344", where each 
     * character representes the column index to be played. 
     * 
     * In the example "22344":
     * - Turn 1: Red places a disc in the second column
     * - Turn 2: Yellow places a disc in the second column
     * - Turn 3: Red places a disc in the third column
     * - ...
     * 
     * @param position A string representation of the current state of the board
     * @return The player whose turn it is after loading the position
     */
    public Player load(String position) {
        // First player is already red
        Player player = Player.RED;
        
        // Places the disc at each column while alternating players
        for (int i = 0; i < position.length(); i++) {
            int col = Character.getNumericValue(position.charAt(i)) - 1;
            placeDisc(col, player);
            player = Player.opponent(player);
        }

        return player;
    }

    /**
     * Converts the bitboard into an array representation
     * 
     * @return An array representation of the bitboard
     */
    public String[][] toArray() {
        // Initializes a string of the binary representations of the player and opponent board
        String redBoardString = boardToBinaryString(redBoard);
        String yellowBoardString = boardToBinaryString(yellowBoard);
        String[][] boardArray = new String[6][7];

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
     * Converts the bitboard into an String representation
     * 
     * @return A string representation of the bitboard
     */
    @Override
    public String toString() {
        // Initializes a string of the binary representations of the red and yellow board
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
     * Converts a bitmap to a binary string
     * 
     * @param board The bitmap to be converted
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

    /**
     * Calculates a bitmap of all the possible connect fours within the next move 
     * for a given player
     * 
     * @param player The player to find if they can within the next move
     * @return A bitmap of all possible connect fours within the next move
     */
    private long winningPosition(Player player) {
        if (player == Player.RED) {
            return computeWinningPosition(redBoard);
        } else {
            return computeWinningPosition(yellowBoard);
        }
    } 

    /**
     * Checks if the given board has a connect four
     * 
     * @param board A bitmap of the board
     * @return True if the given board has a connect four
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
     * @param board A bitmap of the board
     * @return A bitmap of all possible connect fours within the next move
     */
    private long computeWinningPosition(long board) {
        // Checks vertically if there is a possible connect four
        long winningPositions = (board << 1) & (board << 2) & (board << 3);

        // Checks horizontally if there is a possible connect four
        long twoInARow = (board << (BOARD_HEIGHT + 1)) & (board << 2 * (BOARD_HEIGHT + 1));
        winningPositions |= twoInARow & (board << 3 * (BOARD_HEIGHT + 1));
        winningPositions |= twoInARow & (board >> (BOARD_HEIGHT + 1));
        twoInARow >>= 3 * (BOARD_HEIGHT + 1);
        winningPositions |= twoInARow & (board << (BOARD_HEIGHT + 1));
        winningPositions |= twoInARow & (board >> 3 * (BOARD_HEIGHT + 1));

        // Checks diagonally upwards and to the right if there is a possible connect four
        twoInARow = (board << (BOARD_HEIGHT + 2)) & (board << 2 * (BOARD_HEIGHT + 2));
        winningPositions |= twoInARow & (board << 3 * (BOARD_HEIGHT + 2));
        winningPositions |= twoInARow & (board >> (BOARD_HEIGHT + 2));
        twoInARow >>= 3 * (BOARD_HEIGHT + 2);
        winningPositions |= twoInARow & (board << (BOARD_HEIGHT + 2));
        winningPositions |= twoInARow & (board >> 3 * (BOARD_HEIGHT + 2));

        // Checks diagonally downwards and to the right if there is a possible connect four
        twoInARow = (board << BOARD_HEIGHT) & (board << 2 * BOARD_HEIGHT);
        winningPositions |= twoInARow & (board << 3 * BOARD_HEIGHT);
        winningPositions |= twoInARow & (board >> BOARD_HEIGHT);
        twoInARow >>= 3 * BOARD_HEIGHT;
        winningPositions |= twoInARow & (board << BOARD_HEIGHT);
        winningPositions |= twoInARow & (board >> 3 * BOARD_HEIGHT);

        return winningPositions & (BOARD_MASK ^ mask);
    }

    /**
     * Checks if the game has become a draw by checking if there are no more 
     * possible moves
     * 
     * @return True of the game is a draw
     */
    public boolean checkDraw() {
        return spacesLeft == 0;
    }

    /**
     * Getter method to get the amount of empty cells that are on the board
     * 
     * @return The number of empty cells on the board
     */
    public int getSpacesLeft() {
        return spacesLeft;
    }

    /**
     * Getter method to get the amount of moves played so far
     * 
     * @return The number of moves played so far
     */
    public int getMoveCount() {
        return BOARD_HEIGHT * BOARD_WIDTH - spacesLeft;
    }

    /**
     * A heuristic evaluation of the position from the given player's perspective (positive is good
     * for that player). Used by the depth-limited difficulty search to judge non-terminal positions
     * once its look-ahead horizon is reached. It rewards having more open winning squares (threats)
     * than the opponent, with a small bonus for central control.
     *
     * @param player The player to evaluate the position for
     * @return A heuristic score; higher is better for the player
     */
    public int heuristicScore(Player player) {
        Player opponent = Player.opponent(player);
        int threatDifference = Long.bitCount(winningPosition(player))
                - Long.bitCount(winningPosition(opponent));
        int centreDifference = centreCount(player) - centreCount(opponent);
        return threatDifference * 10 + centreDifference * 2;
    }

    /**
     * Counts how many of the given player's discs are in the centre column.
     *
     * @param player The player to count discs for
     * @return The number of the player's discs in the centre column
     */
    private int centreCount(Player player) {
        long playerBoard = (player == Player.RED) ? redBoard : yellowBoard;
        return Long.bitCount(playerBoard & COLUMN_MASK[BOARD_WIDTH / 2]);
    }

    /**
     * Getter method to get the column bitmask for a given column
     * 
     * @param col The column bitmask to be found
     * @return A column bitmask of the given column
     */
    public static long getColumnMask(int col) {
        return COLUMN_MASK[col];
    }

    /**
     * Generates an array of column bitmasks for every column. For example,
     * a column bitmask for the third column of a 7x6 board would be:
     * 
     * 0001000
     * 0001000
     * 0001000
     * 0001000
     * 0001000
     * 0001000
     * 0001000
     */
    private static long[] generateColumnMask() {
        long[] columnMask = new long[BOARD_WIDTH];
        for (int i = 0; i < columnMask.length; i++) {
            columnMask[i] = ((1L << BOARD_HEIGHT) - 1) << (i * (BOARD_HEIGHT + 1));
        }
        return columnMask;
    }

    /**
     * Generates a bitmask for bottom of the board. For example, the bitmask
     * for the bottom of a 7x6 board would be:
     * 
     * 0000000
     * 0000000
     * 0000000
     * 0000000
     * 0000000
     * 0000000
     * 1111111
     */
    private static long generateBottomMask() {
        long boardMask = 0;
        for (int i = 0; i < BOARD_WIDTH; i++) {
            boardMask += 1L << (BOARD_HEIGHT + 1) * i;
        }
        return boardMask;
    }

    /**
     * Generates an array of column bitmasks of only the top row for every column.
     * For example, a column bitmask of only the top row for the third column of a 
     * 7x6 board would be:
     * 
     * 0001000
     * 0000000
     * 0000000
     * 0000000
     * 0000000
     * 0000000
     * 0000000
     */
    private static long[] generateTopMaskColumn() {
        long[] topMaskColumn = new long[BOARD_WIDTH];
        for (int col = 0; col < BOARD_WIDTH; col++) {
            topMaskColumn[col] = (1L << (BOARD_HEIGHT - 1)) << (col * (BOARD_HEIGHT + 1));
        }
        return topMaskColumn;
    }

    /**
     * Generates an array of column bitmasks of only the bottom row for every column.
     * For example, a column bitmask of only the bottom row for the third column of a 
     * 7x6 board would be:
     * 
     * 0000000
     * 0000000
     * 0000000
     * 0000000
     * 0000000
     * 0000000
     * 0001000
     */
    private static long[] generateBottomMaskColumn() {
        long[] bottomMaskColumn = new long[BOARD_WIDTH];
        for (int col = 0; col < BOARD_WIDTH; col++) {
            bottomMaskColumn[col] = 1L << (col * (BOARD_HEIGHT + 1));
        }
        return bottomMaskColumn;
    }
}
