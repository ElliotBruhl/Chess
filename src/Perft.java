public class Perft {

    private final Board board;
    private final int depth;

    public Perft(Board b, int d) {
        this.board = b;
        this.depth = d;
    }
    public long countPositions() {
        if (depth == 0) return 1;

        long count = 0;
        int[] starts = board.getStartIndices(board.getMove());
        long[] allMoves = board.getLegalMoves(starts);

        for (int i = 0; i < starts.length; i++) {
            long moveBitmap = allMoves[i];
            while (moveBitmap != 0) {
                int j = Long.numberOfTrailingZeros(moveBitmap);
                moveBitmap &= ~(1L << j);

                Board newBoard = new Board(this.board);
                int promotionFlag = newBoard.manageMove(starts[i], 63 - j);

                if (promotionFlag != -1)
                    count += handlePromotion(newBoard, promotionFlag);
                else
                    count += new Perft(newBoard, this.depth - 1).countPositions();
            }
        }
        return count;
    }
    private long handlePromotion(Board newBoard, int promotionFlag) {
        long count = 0;
        int[] promotionPieces = {2, 4, 6, 8}; // Rook, Knight, Bishop, Queen

        for (int piece : promotionPieces) {
            Board promotionBoard = new Board(newBoard);
            promotionBoard.managePromotion(promotionFlag, piece);
            count += new Perft(promotionBoard, this.depth - 1).countPositions();
        }

        return count;
    }
}