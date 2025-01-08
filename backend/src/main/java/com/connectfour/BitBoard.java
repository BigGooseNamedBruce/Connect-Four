package com.connectfour;

public class BitBoard {
    
    public static final int BOARD_HEIGHT = 6;
    public static final int BOARD_WIDTH = 7;
    public final int MIN_SCORE = -(BOARD_WIDTH * BOARD_HEIGHT) / 2 + 3;
    public final int MAX_SCORE = (BOARD_WIDTH * BOARD_HEIGHT + 1) / 2 - 3;
    public final long bottomMask = bottomMask();
    public final long boardMask = bottomMask * ((1L << BOARD_HEIGHT) - 1);

    private long playerBoard;
    private long opponentBoard;
    private long mask;
    private int spacesLeft;

    public BitBoard() {
        clear();
    }

    public BitBoard(BitBoard bitBoard) {
        this.playerBoard = bitBoard.playerBoard;
        this.opponentBoard = bitBoard.opponentBoard;
        this.mask = bitBoard.mask;
        this.spacesLeft = bitBoard.spacesLeft;

    }

    public void clear() {
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

        mask |= mask + bottomMaskColumn(col);

        if (player == 1) {
            playerBoard = mask ^ opponentBoard;
        } else {
            opponentBoard = mask ^ playerBoard;
        }

        spacesLeft--;
    }

    /**
     * Removes the top piece of the column
     * 
     * @param col The int representing the column of the piece that will be removed
     */
    public void removeDisc(int col) {

        // Gets the binary representation of the piece that is going to be removed
        long removedPiece = columnMask(col) & mask;
        removedPiece = ~(removedPiece) & ((removedPiece << 1) ^ removedPiece);
        removedPiece = removedPiece >> 1;

        // Removes the piece from the mask
        mask = mask ^ removedPiece;

        // Removes the piece from the player or opponent board
        if ((removedPiece & playerBoard) != 0) {
            playerBoard = mask ^ opponentBoard;
        } else {
            opponentBoard = mask ^ playerBoard;
        }

        // Adds a space to the total amount of spots left since a piece has been removed 
        spacesLeft++;
        
    }

    private long bottomMaskColumn(int col) {
        return 1L << (col * (BOARD_HEIGHT + 1));
    }


    public boolean checkWinner(int player) {

        // Gets the board to check the winner for
        if (player == 1) {
            return checkWinner(playerBoard);
        } else {
            return checkWinner(opponentBoard);
        }
    }

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

    public boolean isWinningMove(int col, int player) {
        long board;
        if (player == 1) {
            board = playerBoard;
        } else {
            board = opponentBoard;
        }

        board |= (mask + bottomMaskColumn(col)) & columnMask(col);

        return checkWinner(board);
    }

    public boolean canWinNext(int player) {
        return (winning_position(player) & possible()) != 0;
    }

    public long possibleNonLosingMoves(int player) {

        long possibleMask = possible();
        long opponentWin = winning_position(player ^ 1);
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

    public long winning_position(int player) {

        if (player == 1) {
            return compute_winning_position(playerBoard);
        } else {
            return compute_winning_position(opponentBoard);
        }
    } 

    /**
     * Gets a bitmap of all the possible playable moves 
     * 
     * @return A bitmap of all the possible playable moves 
     */
    public long possible() {
        return (mask + bottomMask) & boardMask;
    }

    /**
     * Gets a bitmap of all the possible connect fours
     * 
     * @param board A board of either the player of the opponent
     * @return A bitmap containing all the possible connect fours
     */
    public long compute_winning_position(long board) {

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

        
        return r & (boardMask ^ mask);

    }

    public int moveScore(long move, int player) {
        if (player == 1) {
            return popcount(compute_winning_position(playerBoard | move));
        } else {
            return popcount(compute_winning_position(opponentBoard | move));
        }
    }

    public int popcount(long bits) {
        int count;
        for (count = 0; bits != 0; count++) {
            bits &= bits - 1;
        }

        return count;
    }

    public void play(long move, int player) {

        mask |= move;

        if (player == 1) {
            playerBoard = mask ^ opponentBoard;
        } else {
            opponentBoard = mask ^ playerBoard;
        }

        spacesLeft--;
    }

    public long columnMask(int col) {
        return ((1L << BOARD_HEIGHT) - 1) << (col * (BOARD_HEIGHT + 1));
    }
 
    public boolean checkDraw() {
        return spacesLeft == 0;
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

    public long key() {

        long bottom = 0;

        for (int i = 0; i < BOARD_WIDTH; i++) {
            bottom += bottomMaskColumn(i);
        }

        return playerBoard + bottom + mask;
    }

    public long bottomMask() {
        long boardMask = 0;
        for (int i = 0; i < BOARD_WIDTH; i++) {
            boardMask += 1L << (BOARD_HEIGHT + 1) * i;
        }
        return boardMask;
    }

    @Override
    public String toString() {
        
        // Initializes a string of the binary representations of the player and opponent board
        String playerBoardString = String.format("%49s", Long.toBinaryString(playerBoard)).replace(" ", "0");
        String opponentBoardString = String.format("%49s", Long.toBinaryString(opponentBoard)).replace(" ", "0");

        String boardString = "-----------------------------\n";

        // Iterates vertically over the board
        for (int row = 1; row < BOARD_HEIGHT + 1; row++) {
            boardString += "|";
            // Iterates horizontally
            for (int col = BOARD_WIDTH - 1; col > -1; col--) {
                if (playerBoardString.charAt(row + BOARD_WIDTH * col) == '1') {
                    boardString += " X |";
                } else if (opponentBoardString.charAt(row + BOARD_WIDTH * col) == '1') {
                    boardString += " O |";
                } else {
                    boardString += "   |";
                }
            }

            boardString += "\n-----------------------------\n";
        }

        return boardString;
    }

}
