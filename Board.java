import java.util.Arrays;

/**
 * This class contains all methods related to the connect 4 board
 * 
 * http://blog.gamesolver.org/solving-connect-four/01-introduction/
 * 
 */


public class Board {

    // Initilizses the constant variables for the board width and height
    public final int BOARD_HEIGHT = 6;
    public final int BOARD_WIDTH = 7;
    public int moves;

    // Initialies instance variable and the board
    private int spacesLeft = BOARD_HEIGHT * BOARD_WIDTH - 2;
    private char[][] board = new char[BOARD_HEIGHT][BOARD_WIDTH];

    /**
     * No-argument constructor to initialize an empty board
     * 
     */
    public Board() {
        clear();
        moves = 0;
    }

    /**
     * Parameterized constructor that instantiates new board by a copying the values from the 2D array 
     * from the board in the argument and assigning to the new board
     * 
     * @param board A Board object that the constuctor will use to copy the position onto a new board
     */
    public Board(Board board) {
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            this.board[row] = Arrays.copyOf(board.getBoard()[row], board.getBoard()[row].length);
        }
        this.spacesLeft = board.getSpacesLeft();
        this.moves = board.moves;
    }

    /**
     * This method will clear the board by removing all the pieces from the board and reset the amount 
     * of spaces left on the board. This method does not take in any parameters or return any values
     * 
     */
    public void clear() {
        // Resets the amount of spaces left on the board
        this.spacesLeft = BOARD_HEIGHT * BOARD_WIDTH;

        // Loops through the entire 2D array to clear all pieces
        for (char[] row: board) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                row[col] = ' ';
            }
        }
    }

    /**
     * Accesor method that returns the 2D array of the board. This method doesn't accept any
     * parameters but returns a 2D array of the board
     * 
     * @return char[][] - The 2D array of the board
     */
    public char[][] getBoard() {
        return board;
    }

    /**
     * Accessor method to get the amount of spaces left on the board. This method doesn't accept any
     * parameters but returns an int of the amount of spaces left on the board
     * 
     * @return int - The number of spaces left on the board
     */
    public int getSpacesLeft() {
        return spacesLeft;
    }

    /**
     * Mutator method that will change the amount of spaces left on the board. This method accepts an int
     * as the new number of spaces left as its parameter and will return nothing
     * 
     * @param spacesLeft The amount of spaces left on the board
     */
    public void setSpacesLeft(int spacesLeft) {
        this.spacesLeft = spacesLeft;
    }


    /**
     * This method allows the user of computer to place a move. It accepts the column and the player colour
     * as its parameters and will place a piece at that column and as the same colour as the colour. This 
     * method will return a boolean determining whether or not the piece was successfully placed there
     * 
     * @param col An int of the column the user wants to enter the piece
     * @param player A char of which player is placing a piece
     * @return boolean - Whether or not placing that piece was successful or not
     */
    public boolean placeDisc(int col, char player) {
        // Loops from the bottom row up and places the piece at that column
        for (int row = BOARD_HEIGHT - 1; row >= 0; row--) {
            if (board[row][col] == ' ' && !(board[row][col] == 'X' || board[row][col] == 'O')) {
                board[row][col] = player;
                spacesLeft--;
                moves++;
                return true;
            }
        }

        return false;
    }

    public boolean removeDisc(int col, char player) {
        // Loops from the top row down and places the piece at that column
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            if (board[row][col] == player) {
                board[row][col] = ' ';
                spacesLeft++;
                moves--;
                return true;
            }
        }

        return false;
    }

    public boolean isColumnFull(int col) {
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            if (board[row][col] == ' ') {
                return false;
            }
        }

        return true;
    }

    /**
     * This method checks if the player got connect 4. This method accpets the player colour as a parameter
     * and will check if that player won or not. The method will return true if they do and false if they don't
     * 
     * @param player char of the player
     * @return boolean - Whether or not there is connect 4
     */
    public boolean checkWinner(char player) {
        
        // Checks horizontally if there is a connect 4
        for (char[] row: board) {
            for (int col = 0; col < BOARD_WIDTH - 3; col++) {
                if (row[col] == player && row[col+1] == player && row[col+2] == player && row[col+3] == player) {
                    return true;
                }
            }
        }

        // Checks vertically if there is a conenct 4
        for (int row = BOARD_HEIGHT - 1; row > 2; row--) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col] == player && board[row-1][col] == player && board[row-2][col] == player && board[row-3][col] == player) {
                    return true;
                }
            }
        }

        // Checks diagonally upwards to the right if there is a connect 4
        for (int row = BOARD_HEIGHT - 1; row > 3; row--) {
            for (int col = 0; col < BOARD_WIDTH - 3; col++) {
                if (board[row][col] == player && board[row-1][col+1] == player && board[row-2][col+2] == player && board[row-3][col+3] == player) {
                    return true;
                }
            }
        }

        // Checks diagonally downwards to the right if there is a connect 4
        for (int row = 0; row < BOARD_HEIGHT - 3; row++) {
            for (int col = 0; col < BOARD_WIDTH - 3; col++) {
                if (board[row][col] == player && board[row+1][col+1] == player && board[row+2][col+2] == player && board[row+3][col+3] == player) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean checkDraw() {
        if (spacesLeft == 0) {
            return true;
        }
        return false;
    }


    /**
     * toString method that will output the board. This method accepts no parameters 
     * and returns a String of the board
     * 
     * @return String - a String of the board nicely formatted
     */
    public String toString(){
        // Adds the numbering system to the top of the board
        String boardString = "  ";
        for (int count=0; count < BOARD_WIDTH; count++) {
            boardString += String.format("%d   ", count + 1);
        }
        // Adds the top dash of the board to denote its the top of the board
        boardString += "\n";
        for (int count = 0; count < BOARD_WIDTH; count++) {
            boardString += "----";
        } 
        boardString += "-\n";

        // Adds the board to boardString
        for (char[] row: board) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                boardString += "| " + row[col] + " ";
            }
            boardString += "|\n";
            for (int count = 0; count < BOARD_WIDTH; count++) {
                boardString += "----";
            } 
            boardString += "-\n";
        }

        return boardString;
    }

    public char getOpponent(char player) {
        if (player == 'X') {
            return 'O';
        } else {
            return 'X';
        }
    }

}