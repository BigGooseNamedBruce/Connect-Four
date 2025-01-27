package com.connectfour.game;

/**
 * This file contains all the methods related to the Connect Four solver
 * 
 * @author Brayden T
 * 
 */

public class Solver {

    private BitBoard board;
    private final int TRANSPOSITION_TABLE_SIZE = (int) Math.pow(2, 23);
    private TranspositionTable table;
    private int[] moveOrder = {3, 4, 2, 5, 1, 6, 0};

    public Solver(BitBoard board) {
        this.board = board;
        this.table = new TranspositionTable(TRANSPOSITION_TABLE_SIZE);
    }


    /**
     * Given a player, this method will find the best move for that player
     * 
     * @param player An char representing which player
     * @return An int of the column that is the best move
     */
    public int findBestMove(char player) {

        int bestMove = -1;
        int score;
        int bestScore = Integer.MIN_VALUE;
        
       
        //for (int col = 0; col < board.BOARD_WIDTH; col++) {
        for (int col: moveOrder) {
            if (!board.isColumnFull(col)) {
                
                board.placeDisc(col, player);
                
                if (board.checkDraw()) {
                    score = 0;
                } else if (board.checkWinner(player)) {
                    score = (board.getSpacesLeft() + 2) / 2;
                } else {
                    score = -solve(getOpponent(player));
                }
                
                board.removeDisc(col);

                if (score > bestScore) {
                    bestMove = col;
                    bestScore = score;
                }
            }
        }
        return bestMove;

    }

    public int solve(char player) {
        if (board.canWinNext(player)) {
            return (board.getSpacesLeft() + 1) / 2;
        }

        int min = -board.getSpacesLeft() / 2;
        int max = (board.getSpacesLeft() + 1) / 2;

        while (min < max) {
            int med = min + (max - min) / 2;
            if (med <= 0 && min / 2 < med) {
                med = min / 2;
            } else if (med >= 0 && max / 2 > med) {
                med = max / 2;
            }
            int r = negamax(board, player, med, med + 1);
            if (r <= med) {
                max = r;
            } else {
                min = r;
            }
        }

        return min;

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
    private int negamax(BitBoard board, char player, int alpha, int beta) {
        // Checks if the position is drawn
        if (board.checkDraw()) {
            return 0;
        }

        // Checks if a player can win in the next move
        for (int col = 0; col < BitBoard.BOARD_WIDTH; col++) {
            if (!board.isColumnFull(col) & board.canWinNext(player)) {
                return (board.getSpacesLeft() + 1) / 2;
            }
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
            BitBoard copyBoard = new BitBoard(board);
            copyBoard.placeDisc(next, player);
            next = moves.getNext();
            
            int score = -negamax(copyBoard, getOpponent(player), -beta, -alpha);
            
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

    public void loadPosition(String position, char player) {
        for (int i = 0; i < position.length(); i++) {
            board.placeDisc(Character.getNumericValue(position.charAt(i) - 1), player);
            player = getOpponent(player);
        }
    }

    public char getOpponent(char player) {
        if (player == 'r') {
            return 'y';
        }
        return 'r';
    }
}