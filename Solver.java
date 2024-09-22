public class Solver {

    private Board board;
    public int[] b = new int[7];

    public Solver(Board board) {
        this.board = board;
    }


    /**
     * This method finds the best move
     * @param board
     * @param player An char representing which player
     * @return
     */
    public int findBestMove(char player) {

        int bestMove = -1;
        int score;
        int bestScore = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE + 1;
        int beta = Integer.MAX_VALUE;
       
        for (int col = 0; col < board.BOARD_WIDTH; col++) {
            if (!board.isColumnFull(col)) {
                
                board.placeDisc(col, player);
                
                if (board.checkDraw()) {
                    score = 0;
                } else if (board.checkWinner(player)) {
                    score = (board.getSpacesLeft() + 2) / 2;
                } else {
                    score = -negamax(getOpponent(player), alpha, beta);
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


    private int negamax(char player, int alpha, int beta) {
        // Checks if the position is drawn
        if (board.checkDraw()) {
            return 0;
        }

        for (int col = 0; col < board.BOARD_WIDTH; col++) {
            if (!board.isColumnFull(col)) {
                board.placeDisc(col, player);
                if (board.checkWinner(player)) {
                    board.removeDisc(col, player);
                    return (board.getSpacesLeft() + 1) / 2;
                }
                board.removeDisc(col, player);
                
            }
        }

        int max = (board.getSpacesLeft() - 1) / 2;
        if (beta > max) {
            beta = max;
            if (alpha >= beta) {
                return beta;
            }
        }
       

        for (int col = 0; col < board.BOARD_WIDTH; col++) {
            if (!board.isColumnFull(col)) {
                board.placeDisc(col, player);
                int score = -negamax(getOpponent(player), -beta, -alpha);
                board.removeDisc(col, player);
                
                if (score >= beta) {
                    return score;
                }
                if (score > alpha) {
                    alpha = score;
                }
            }
        }

        return alpha;
    }

    public char getOpponent(char player) {
        if (player == 'X') {
            return 'O';
        } else {
            return 'X';
        }
    }
}