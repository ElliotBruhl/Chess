import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Perft {

    private final Board board;
    private final int depth;

    private Perft(Board b, int d) {
        this.board = b;
        this.depth = d;
    }
    private long countPositions() {
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
    public static void runPerftTests(String filePath, int maxDepth) {
        int lineCounter = 1;
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines) {
                
                if (lineCounter%100 == 0) System.out.println("Parsing lines: "+lineCounter + " - " + (lineCounter+99));

                String[] splicedStr = line.split(",");
                Board board = new Board(splicedStr[0]);
                for (int i = 1; i<splicedStr.length && i<maxDepth; i++) {
                    long expected = Long.parseLong(splicedStr[i]);
                    long found = new Perft(board, i).countPositions();
                    if (expected != found) {
                        System.out.println("Test Failed on line "+lineCounter +": " + expected + " expected but " + found + " found");
                        System.out.println("FEN: " + splicedStr[0]);
                        System.out.println("Depth: " + i);
                    }
                }
                lineCounter++;
            }
            System.out.println("Test Finished");
        }
        catch (IOException e) {
            throw new Error("Error reading file");
        }
    }
    public static void runPerftTest(String FEN, int depth) {
        long startTime = System.nanoTime();
        long count = new Perft(new Board(FEN), depth).countPositions();
        long endTime = System.nanoTime();
        System.out.println(count + " positions at depth "+depth+" in "+((endTime-startTime)/1000000.0)+" ms");
    }
    public static void d1PerftDivide(String FEN) {
        Board board = new Board(FEN);
        int[] starts = board.getStartIndices(board.getMove());
        long[] moves = board.getLegalMoves(starts);
        for (int i = 0; i < starts.length; i++) {
            System.out.println("\nStart: " + starts[i]);
            displayBitboard(moves[i]);
        }
    }
    public static void displayBitboard(long bitboard) { //for debugging
        String binaryString = Long.toBinaryString(bitboard);
        binaryString = String.format("%64s", binaryString).replace(' ', '0');
        for (int rank = 7; rank >= 0; rank--) {
            for (int file = 0; file < 8; file++) {
                int index = rank * 8 + file;
                System.out.print(binaryString.charAt(index) + " ");
            }
            System.out.println();
        }
    }
}