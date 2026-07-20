package com.connectfour.game;

import java.util.Random;

import com.connectfour.game.OpeningBook.BookType;

/**
 * This file contains all the methods related to the Connect Four solver
 * 
 * @author Brayden T
 * 
 */


public class Solver {

    // Difficulty bounds exposed to the API layer (inclusive)
    public static final int MIN_DIFFICULTY = 1;
    public static final int MAX_DIFFICULTY = 5;

    // Score returned for a win in the depth-limited difficulty search; far larger than any
    // heuristic value so wins/losses always dominate.
    private static final int WIN_SCORE = 1_000_000;

    private final int TRANSPOSITION_TABLE_SIZE = 1 << 23;
    private final BookType BOOK = BookType.EIGHT_MOVES;
    private final int[] MOVE_ORDER = generateMoveOrder();
    private final Random random = new Random();

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
    public int findBestMove(Bitboard board, Player player) {

        int bestMove = -1;
        int bestScore = Integer.MIN_VALUE + 1;

        if (board.canWinNext(player)) {
            for (int col = 0; col < Bitboard.BOARD_WIDTH; col++) {
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
        for (int i = Bitboard.BOARD_WIDTH - 1; i >= 0; i--) {
            long move = possibleMoves & Bitboard.getColumnMask(MOVE_ORDER[i]);
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

    /**
     * Finds the best column to play at a given difficulty. Difficulty controls how far ahead the AI
     * looks and how often it blunders, so the strength scales in a way a human can feel:
     *
     * <ul>
     *   <li>{@link #MAX_DIFFICULTY}: perfect play (opening book + exact solver) - effectively
     *       unbeatable.</li>
     *   <li>Lower levels: no opening book, a shallow depth-limited search, and a chance to play a
     *       random move instead. Shorter look-ahead means the AI can be out-planned, and the blunder
     *       chance means it will sometimes miss blocks entirely.</li>
     * </ul>
     *
     * An obvious immediate win is always taken, at every difficulty.
     *
     * @param board A Bitboard of the current game
     * @param player The player to move
     * @param difficulty A value in [{@link #MIN_DIFFICULTY}, {@link #MAX_DIFFICULTY}]
     * @return The column index of the chosen move
     */
    public int findBestMove(Bitboard board, Player player, int difficulty) {
        difficulty = Math.max(MIN_DIFFICULTY, Math.min(MAX_DIFFICULTY, difficulty));

        // An obvious immediate win is always taken, at every difficulty.
        int winningColumn = findWinningColumn(board, player);
        if (winningColumn != -1) {
            return winningColumn;
        }

        // Top difficulty plays perfectly: opening book + exact iterative deepening.
        if (difficulty >= MAX_DIFFICULTY) {
            return findBestMove(board, player);
        }

        // Lower difficulties sometimes just blunder (more often the easier the level).
        if (random.nextDouble() < blunderChance(difficulty)) {
            return randomLegalColumn(board);
        }

        // Otherwise, a depth-limited look-ahead with a heuristic evaluation (no opening book),
        // so a shallow depth genuinely limits how well the AI plays.
        int lookahead = searchDepth(difficulty);
        int bestMove = -1;
        int bestScore = Integer.MIN_VALUE;

        for (int i = 0; i < Bitboard.BOARD_WIDTH; i++) {
            int col = MOVE_ORDER[i];
            if (board.isColumnFull(col)) {
                continue;
            }
            board.placeDisc(col, player);
            int score = -negamaxDepth(board, Player.opponent(player), lookahead, -WIN_SCORE, WIN_SCORE);
            board.removeDisc(col);
            if (score > bestScore) {
                bestScore = score;
                bestMove = col;
            }
        }

        if (bestMove == -1) {
            return randomLegalColumn(board);
        }
        return bestMove;
    }

    /**
     * A depth-limited negamax search that uses a heuristic evaluation at its horizon. Unlike the
     * exact search it does NOT use the opening book or transposition table, so the depth limit truly
     * caps how far ahead the AI can plan (which is what makes lower difficulties beatable).
     *
     * @param board The current position
     * @param player The player to move
     * @param depth Remaining plies of look-ahead
     * @param alpha Alpha bound
     * @param beta Beta bound
     * @return The negamax score from {@code player}'s perspective
     */
    private int negamaxDepth(Bitboard board, Player player, int depth, int alpha, int beta) {
        if (board.checkDraw()) {
            return 0;
        }
        if (depth <= 0) {
            return board.heuristicScore(player);
        }
        // The side to move can win immediately - best possible outcome (prefer sooner wins).
        if (board.canWinNext(player)) {
            return WIN_SCORE - board.getMoveCount();
        }

        int best = Integer.MIN_VALUE;
        for (int i = 0; i < Bitboard.BOARD_WIDTH; i++) {
            int col = MOVE_ORDER[i];
            if (board.isColumnFull(col)) {
                continue;
            }
            board.placeDisc(col, player);
            int score = -negamaxDepth(board, Player.opponent(player), depth - 1, -beta, -alpha);
            board.removeDisc(col);
            if (score > best) {
                best = score;
            }
            if (best > alpha) {
                alpha = best;
            }
            if (alpha >= beta) {
                break;
            }
        }
        return best;
    }

    /**
     * Returns the column of an immediate winning move for the player, or -1 if there is none.
     *
     * @param board The current position
     * @param player The player to move
     * @return A winning column, or -1
     */
    private int findWinningColumn(Bitboard board, Player player) {
        if (!board.canWinNext(player)) {
            return -1;
        }
        for (int col = 0; col < Bitboard.BOARD_WIDTH; col++) {
            if (board.isColumnFull(col)) {
                continue;
            }
            board.placeDisc(col, player);
            boolean win = board.checkWinner(player);
            board.removeDisc(col);
            if (win) {
                return col;
            }
        }
        return -1;
    }

    /**
     * Picks a uniformly random playable column.
     *
     * @param board The current position
     * @return A non-full column index (0 if the board is full)
     */
    private int randomLegalColumn(Bitboard board) {
        int[] legal = new int[Bitboard.BOARD_WIDTH];
        int count = 0;
        for (int col = 0; col < Bitboard.BOARD_WIDTH; col++) {
            if (!board.isColumnFull(col)) {
                legal[count] = col;
                count++;
            }
        }
        if (count == 0) {
            return 0;
        }
        return legal[random.nextInt(count)];
    }

    /**
     * Plies of look-ahead for a given (below-max) difficulty. Higher means stronger play. Tune
     * these to shift how challenging each level feels.
     *
     * @param difficulty A value in [{@link #MIN_DIFFICULTY}, {@link #MAX_DIFFICULTY} - 1]
     * @return The number of look-ahead plies
     */
    private int searchDepth(int difficulty) {
        if (difficulty == 1) {
            return 0;
        } else if (difficulty == 2) {
            return 2;
        } else if (difficulty == 3) {
            return 4;
        } else {
            return 8;
        }
    }

    /**
     * Probability of playing a random move instead of searching, for a given (below-max)
     * difficulty. Higher means the AI blunders more often (easier). Tune to taste.
     *
     * @param difficulty A value in [{@link #MIN_DIFFICULTY}, {@link #MAX_DIFFICULTY} - 1]
     * @return A probability in [0, 1]
     */
    private double blunderChance(int difficulty) {
        if (difficulty == 1) {
            return 0.45;
        } else if (difficulty == 2) {
            return 0.25;
        } else if (difficulty == 3) {
            return 0.10;
        } else {
            return 0.0;
        }
    }



    public int solve(Bitboard board, Player player) {
        if (board.checkDraw()) {
            return 0;
        } else if (board.canWinNext(player)) {
            return (board.getSpacesLeft() + 1) / 2;
        }
        
        // Gets score from opening book
        if (Bitboard.BOARD_HEIGHT * Bitboard.BOARD_WIDTH - board.getSpacesLeft() <= BOOK.getMoveLength()) {
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
    private final int negamax(Bitboard board, Player player, int alpha, int beta) {

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
            if (value > Bitboard.MAX_SCORE - Bitboard.MIN_SCORE + 1) {
                min = value + 2 * Bitboard.MIN_SCORE - Bitboard.MAX_SCORE - 2;
                if (alpha < min) {
                    alpha = min;
                    if (alpha >= beta) {
                        return alpha;
                    }
                }
            } else {
                max = value + Bitboard.MIN_SCORE - 1;
                if (beta > max) {
                    beta = max;
                    if (alpha >= beta) {
                        return beta;
                    }
                }  
            }
        }

        MoveSorter moves = new MoveSorter();
        for (int i = Bitboard.BOARD_WIDTH - 1; i >= 0; i--) {
            long move = possible & Bitboard.getColumnMask(MOVE_ORDER[i]);
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
                table.put(key, (byte) (score + Bitboard.MAX_SCORE - 2 * Bitboard.MIN_SCORE + 2));
                return score;
            }

            if (score > alpha) {
                alpha = score;
            }
        }

        table.put(key, (byte) (alpha - Bitboard.MIN_SCORE + 1));
        return alpha;
    }

    /**
     * Calculates the move order, starting from the centre and then gradually stretching out, 
     * for the width of the board. For a board that is 7 discs wide, the move order would be 
     * {3, 4, 2, 5, 1, 6, 0}
     */
    public static int[] generateMoveOrder() {
        int[] moveOrder = new int[Bitboard.BOARD_WIDTH];
        int midpoint = Bitboard.BOARD_WIDTH / 2;

        for (int i = 0; i < Bitboard.BOARD_WIDTH; i++) {
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