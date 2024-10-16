public class Solver {

    private BitBoard board;
    private TranspositionTable table;

    public Solver(BitBoard board) {
        this.board = board;
        this.table = new TranspositionTable(16777215);
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
        int alpha = Integer.MIN_VALUE + 1;
        int beta = Integer.MAX_VALUE;
        int[] moveOrder = {3, 4, 2, 5, 1, 6, 0};
       
        //for (int col = 0; col < board.BOARD_WIDTH; col++) {
        for (int col: moveOrder) {
            if (!board.isColumnFull(col)) {
                
                board.placeDisc(col, player);
                
                if (board.checkDraw()) {
                    score = 0;
                } else if (board.checkWinner(player)) {
                    score = (board.getSpacesLeft() + 2) / 2;
                } else {
                    score = -negamax(board, getOpponent(player), alpha, beta);
                }
                
                //System.out.println(score + " " + (col + 1));
                board.removeDisc(col, player);

                if (score > bestScore) {
                    bestMove = col;
                    bestScore = score;
                }
            }
        }

        return bestMove;
    }


    public int negamax(BitBoard board, int player, int alpha, int beta) {
        // Checks if the position is drawn
        if (board.checkDraw()) {
            return 0;
        }

        for (int col = 0; col < board.BOARD_WIDTH; col++) {
            if (!board.isColumnFull(col)) {
                BitBoard board2 = new BitBoard(board);
                board2.placeDisc(col, player);
                if (board2.checkWinner(player)) {
                    return (board2.getSpacesLeft() + 2) / 2;
                } 
            }
        }

        int max = (board.getSpacesLeft() - 1) / 2;

        if (table.get(board.key()) != 0) {
            max = table.get(board.key()) + board.MIN_SCORE - 1;
        }

        if (beta > max) {
            beta = max;
            if (alpha >= beta) {
                return beta;
            }
        }
       

        for (int col = 0; col < board.BOARD_WIDTH; col++) {
            if (!board.isColumnFull(col)) {
                BitBoard board2 = new BitBoard(board);
                board2.placeDisc(col, player);
                
                int score = -negamax(board2, getOpponent(player), -beta, -alpha);
                
                if (score >= beta) {
                    return score;
                }
                if (score > alpha) {
                    alpha = score;
                }
            }
        }
        //transTable.put(P.key(), alpha - Position::MIN_SCORE + 1)
        table.put(board.key(), (byte) (alpha - board.MIN_SCORE + 1));
        return alpha;
    }

    public int getOpponent(int player) {
        return player ^ 1;
    }
}