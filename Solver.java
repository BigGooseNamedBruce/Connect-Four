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
        int bestScore = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        //int[] explorationOrder = {3, 2, 4, 1, 5, 0, 6};
       
        for (int col = 0; col < board.BOARD_WIDTH; col++) {
            if (!board.isColumnFull(col)) {
                board.placeDisc(col, player);
                int score = 0; //negamax(getOpponent(player));
                System.out.println(score);
                board.removeDisc(col, player);
                if (score > bestScore) {
                    bestMove = col;
                    bestScore = score;
                }
            }
        }

        return bestMove;
    }



    public int negamax(Board board, char player) {
        // Checks if the position is drawn
        if (board.checkDraw()) {
            return 0;
        }

        for (int col = 0; col < board.BOARD_WIDTH; col++) {

            if (board.placeDisc(col, player)) {
                if (board.checkWinner(player)) {
                    board.removeDisc(col, player);
                    return board.getSpacesLeft() / 2;
                }
                board.removeDisc(col, player);
            }
        }

        int bestScore = -board.BOARD_LENGTH * board.BOARD_WIDTH;

        for (int col = 0; col < board.BOARD_WIDTH; col++) {
            if (!board.isColumnFull(col)) {
                Board board2 = new Board(board);
                board2.placeDisc(col, player);
                int score = -negamax(board2, player);
                bestScore = Math.max(bestScore, score);
            }
        }

        return bestScore;
    }

    // public int negamax(char player, int alpha, int beta) {

        
    //     // Checks if the position is drawn
    //     if (board.checkDraw()) {
    //         return 0;
    //     }

    //     for (int col = 0; col < board.BOARD_WIDTH; col++) {

    //         if (board.placeDisc(col, player)) {
    //             if (board.checkWinner(player)) {
    //                 board.removeDisc(col, player);
    //                 return board.getSpacesLeft() / 2;
    //             }
    //             board.removeDisc(col, player);
    //         }
    //     }

    //     if (beta > board.getSpacesLeft() / 2) {
    //         beta = board.getSpacesLeft() / 2;
    //         if (alpha >= beta) {
    //             return beta;
    //         }
    //     }

    //     for (int col = 0; col < board.BOARD_WIDTH; col++) {
    //         if (!board.isColumnFull(col)) {
    //             board.placeDisc(col, player);
    //             int score = -negamax(getOpponent(player), -beta, -alpha);
    //             board.removeDisc(col, player);

    //             if (score >= beta) {
    //                 return score;
    //             }
    //             if (score > alpha) {
    //                 alpha = score;
    //             }
    //         }
    //     }


    //     return alpha;
    // }

    public char getOpponent(char player) {
        if (player == 'X') {
            return 'O';
        } else {
            return 'X';
        }
    }


}