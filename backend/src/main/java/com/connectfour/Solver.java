package com.connectfour;

import javax.swing.text.Position;

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
     * This method finds the best move
     * @param board
     * @param player An char representing which player
     * @return
     */
    public int findBestMove(int player) {

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

    public int solve(int player) {
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


    private int negamax(BitBoard board, int player, int alpha, int beta) {
        // Checks if the position is drawn
        if (board.checkDraw()) {
            return 0;
        }

        for (int col = 0; col < board.BOARD_WIDTH; col++) {
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
            if (value > board.MAX_SCORE - board.MIN_SCORE + 1) {
                min = value + 2 * board.MIN_SCORE - board.MAX_SCORE - 2;
                if (alpha < min) {
                    alpha = min;
                    if (alpha >= beta) {
                        return alpha;
                    }
                }
            } else {
              max = value + board.MIN_SCORE - 1;
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
            long move = possible & board.columnMask(moveOrder[i]);
            if (move != 0) {
                moves.add(move, board.moveScore(move, player));
            }
        } 

        long next = moves.getNext();

        while(next != 0) {
            BitBoard board2 = new BitBoard(board);
            board2.play(next, player);
            next = moves.getNext();
            
            int score = -negamax(board2, getOpponent(player), -beta, -alpha);
            
            if (score >= beta) {
                table.put(key, (byte) (score + board.MAX_SCORE - 2 * board.MIN_SCORE + 2));
                return score;
            }

            if (score > alpha) {
                alpha = score;
            }
        }

        table.put(key, (byte) (alpha - board.MIN_SCORE + 1));
        return alpha;
    }

    public void loadPosition(String position, int player) {
        for (int i = 0; i < position.length(); i++) {
            board.placeDisc(Character.getNumericValue(position.charAt(i) - 1), player);
            player = getOpponent(player);
        }
    }

    public int getOpponent(int player) {
        return player ^ 1;
    }
}