package com.connectfour.game;

import com.connectfour.game.OpeningBook.BookType;

/**
 * This file contains all the methods related to the Connect Four solver
 * 
 * @author Brayden T
 * 
 */


public class Solver {

    private final int TRANSPOSITION_TABLE_SIZE = 1 << 23;
    private final BookType BOOK = BookType.EIGHT_MOVES;
    private final int[] MOVE_ORDER = generateMoveOrder();

    private TranspositionTable table;
    private OpeningBook openingBook;


    /**
     * Default constructor that will initialize the transposition table
     * and opening book
     * 
     */
    public Solver() {
        this.table = new TranspositionTable(TRANSPOSITION_TABLE_SIZE);
        this.openingBook = new OpeningBook(BOOK);
    }

    /**
     * Finds the best column to play given a board for a player
     * 
     * @param board A Bitboard of the current game
     * @param player A Player enum reprenting the current player
     * @return An int representing the column that is the best move
     */
    public int findBestMove(BitBoard board, Player player) {

        int bestMove = -1;
        int bestScore = Integer.MIN_VALUE + 1;

        if (board.canWinNext(player)) {
            for (int col = 0; col < BitBoard.BOARD_WIDTH; col++) {
                if (board.isColumnFull(col)) {
                    continue;
                }

                board.placeDisc(col, player);
                if (board.checkWinner(player)) {
                    board.removeDisc(col);
                    return col;
                }
                board.removeDisc(col);
            }
        }

        long possibleMoves = board.possibleNonLosingMoves(player);

        MoveSorter moves = new MoveSorter();
        for (int i = BitBoard.BOARD_WIDTH - 1; i >= 0; i--) {
            long move = possibleMoves & BitBoard.columnMask(MOVE_ORDER[i]);
            if (move != 0) {
                moves.add(move, board.moveScore(move, player));
            }
        }

        long next = moves.getNext();
        int count = 0;

        // Edge case when the only non-losing move is the first column
        if (next == 0) {
            return 0;
        }
        
        while (next != 0 && count < 5) {

            board.placeDisc(next, player);
            int col = board.moveColumn(next);

            int score = -solve(board, Player.opponent(player));
            board.removeDisc(next);

            if (score > bestScore) {
                bestScore = score;
                bestMove = col;
            }

            next = moves.getNext();
        }

        return bestMove;
    }



    public int solve(BitBoard board, Player player) {
        if (board.checkDraw()) {
            return 0;
        } else if (board.canWinNext(player)) {
            return (board.getSpacesLeft() + 1) / 2;
        }
        
        // Gets score from opening book
        if (BitBoard.BOARD_HEIGHT * BitBoard.BOARD_WIDTH - board.getSpacesLeft() <= BOOK.getMoveLength()) {
            long key = board.key();
            int score = openingBook.get(key);
            if (score != Byte.MIN_VALUE) {
                return openingBook.get(key);
            }
        }

        int low = -board.getSpacesLeft() / 2;
        int high = (board.getSpacesLeft() + 1) / 2;
        
        while (low < high) {
            int mid = low + (high - low) / 2;

            if (mid <= 0 && low / 2 < mid) {
                mid = low / 2;
            } else if (mid >= 0 && high / 2 > mid) {
                mid = high / 2;
            }

            int score = negamax(board, player, mid, mid + 1);

            if (score <= mid) {
                high = score;
            } else {
                low = score;
            }
        }

        return low;
    }

    /**
     * Finds the score for a given position
     * 
     * @param board A Bitboard of the current game
     * @param player A Player enum reprenting the current player
     * @param alpha An int of the lower bound score
     * @param beta An int of the higher bound score
     * @return An int representing the score of that position
     */
    private final int negamax(BitBoard board, Player player, int alpha, int beta) {

         // Checks if the position is drawn
        if (board.checkDraw()) {
            return 0;
        }
        
        long possible = board.possibleNonLosingMoves(player);
        if(possible == 0) {
            return -(board.getSpacesLeft()) / 2;
        }
            

        int min = -board.getSpacesLeft() / 2;
        if (alpha < min) {
            alpha = min;
            if(alpha >= beta) {
                return alpha;  
            }
        }

        int max = (board.getSpacesLeft() - 1) / 2;
        if (beta > max) {
            beta = max;
            if (alpha >= beta) {
                return beta;
            }
        }

        long key = board.key();
        int value = table.get(key);

        // Gets value from the transposition table
        if (value != 0) {
            if (value > BitBoard.MAX_SCORE - BitBoard.MIN_SCORE + 1) {
                min = value + 2 * BitBoard.MIN_SCORE - BitBoard.MAX_SCORE - 2;
                if (alpha < min) {
                    alpha = min;
                    if (alpha >= beta) {
                        return alpha;
                    }
                }
            } else {
                max = value + BitBoard.MIN_SCORE - 1;
                if (beta > max) {
                    beta = max;
                    if (alpha >= beta) {
                        return beta;
                    }
                }  
            }
        }

        MoveSorter moves = new MoveSorter();
        for (int i = BitBoard.BOARD_WIDTH - 1; i >= 0; i--) {
            long move = possible & BitBoard.columnMask(MOVE_ORDER[i]);
            if (move != 0) {
                moves.add(move, board.moveScore(move, player));
            }
        } 

        long next = moves.getNext();

        while(next != 0) {

            board.placeDisc(next, player);
            int score = -negamax(board, Player.opponent(player), -beta, -alpha);
            
            board.removeDisc(next);
            next = moves.getNext();

            if (score >= beta) {
                table.put(key, (byte) (score + BitBoard.MAX_SCORE - 2 * BitBoard.MIN_SCORE + 2));
                return score;
            }

            if (score > alpha) {
                alpha = score;
            }
        }

        table.put(key, (byte) (alpha - BitBoard.MIN_SCORE + 1));
        return alpha;
    }

    /**
     * Calculates the move order, starting from the centre and then gradually stretching out, 
     * for the width of the board. For a board that is 7 discs wide, the move order would be 
     * {3, 4, 2, 5, 1, 6, 0}
     */
    public static int[] generateMoveOrder() {
        int[] moveOrder = new int[BitBoard.BOARD_WIDTH];
        int midpoint = BitBoard.BOARD_WIDTH / 2;

        for (int i = 0; i < BitBoard.BOARD_WIDTH; i++) {
            if (i == 0) {
                moveOrder[i] = midpoint;
            } else if (i % 2 == 0) {
                moveOrder[i] = midpoint - (i + 1) / 2;
            } else {
                moveOrder[i] = midpoint + (i + 1) / 2;
            }
        }
        return moveOrder; 
    }
}