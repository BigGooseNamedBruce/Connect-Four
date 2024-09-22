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
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        //int[] explorationOrder = {3, 2, 4, 1, 5, 0, 6};
       
        for (int col = 0; col < board.BOARD_WIDTH; col++) {
            if (!board.isColumnFull(col)) {
                
                board.placeDisc(col, player);
                
                if (board.checkDraw()) {
                    return 0;
                } else if (board.checkWinner(player)) {
                    score = (board.getSpacesLeft() + 1) / 2;
                } else {
                    score = -negamax(board, getOpponent(player));
                }
                
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
            if (!board.isColumnFull(col)) {
                board.placeDisc(col, player);
                if (board.checkWinner(player)) {
                    board.removeDisc(col, player);
                    return (board.getSpacesLeft() + 1) / 2;
                }
                board.removeDisc(col, player);
                
            }
        }

        int bestScore = -board.BOARD_HEIGHT * board.BOARD_WIDTH;

        for (int col = 0; col < board.BOARD_WIDTH; col++) {
            if (!board.isColumnFull(col)) {
                Board board2 = new Board(board);
                board2.placeDisc(col, player);
                int score = -negamax(board2, getOpponent(player));
                bestScore = Math.max(bestScore, score);
            }
        }

        return bestScore;
    }

    public char getOpponent(char player) {
        if (player == 'X') {
            return 'O';
        } else {
            return 'X';
        }
    }


}