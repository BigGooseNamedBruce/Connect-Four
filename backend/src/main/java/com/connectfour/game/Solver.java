package com.connectfour.game;

import com.connectfour.game.OpeningBook.BookType;

/**
 * This file contains all the methods related to the Connect Four solver
 * 
 * @author Brayden T
 * 
 */


public class Solver {

    private BitBoard board;
    private final int TRANSPOSITION_TABLE_SIZE = (int)Math.pow(2, 23) + 9;
    private final BookType BOOK = BookType.EIGHT_MOVES_DEBUG;
    public TranspositionTable table;
    private int[] moveOrder = {3, 4, 2, 5, 1, 6, 0};
    private OpeningBook openingBook;

    public Solver(BitBoard board) {
        this.board = board;
        this.table = new TranspositionTable(TRANSPOSITION_TABLE_SIZE);
        this.openingBook = new OpeningBook(BOOK);
    }

    /**
     * Given a player, this method will find the best move for that player
     * 
     * @param player An char representing which player
     * @return An int of the column that is the best move
     */
    public int findBestMove(char player) {

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
            long move = possibleMoves & BitBoard.columnMask(moveOrder[i]);
            if (move != 0) {
                moves.add(move, board.moveScore(move, player));
            }
        }

        long next = moves.getNext();

        // Edge case when the only non-losing move is the first column
        if (next == 0) {
            return 0;
        }
        
        for (int count = 0; count < 5; count++) {

            board.placeDisc(next, player);
            int col = board.moveColumn(next);

            int score = -solve(board, getOpponent(player));
            board.removeDisc(next);

            if (score > bestScore) {
                bestScore = score;
                bestMove = col;
            }

            next = moves.getNext();
        }

        return bestMove;
    }



    public int solve(BitBoard board, char player) {
        if (board.checkDraw()) {
            return 0;
        } else if (board.canWinNext(player)) {
            return (board.getSpacesLeft() + 1) / 2;
        }
        
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
     * 
     * 
     * @param board
     * @param player
     * @param alpha
     * @param beta
     * @return An int representing the score of that position
     */
    private final int negamax(BitBoard board, char player, int alpha, int beta) {

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
            long move = possible & BitBoard.columnMask(moveOrder[i]);
            if (move != 0) {
                moves.add(move, board.moveScore(move, player));
            }
        } 

        long next = moves.getNext();

        while(next != 0) {

            board.placeDisc(next, player);
            int score = -negamax(board, getOpponent(player), -beta, -alpha);
            
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


    public char getOpponent(char player) {
        if (player == 'r') {
            return 'y';
        }
        return 'r';
    }
}