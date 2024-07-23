import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import javax.swing.SwingWorker;

public class App {
    public static void main(String[] args) {

        Board board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"); //"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
        startGuiGame(board, new Gui(board));
    }
    private static void startGuiGame(Board board, Gui boardGui) {
        boardGui.updateBoard();
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() { //Gui and game logic must operate on seperate threads
                try {
                    int gameState = board.gameState();
                    int start;
                    int end;

                    while (gameState == 0) { // 0 play on, 1 draw, 2 white win, 3 black win
                        if (board.getMove()) {
                            boardGui.waitForValidInput(); //proceeds when valid start and end are inputed
                            start = boardGui.getStartClick();
                            end = boardGui.getEndClick();
                            boardGui.resetStartEnd(); //clears the Gui temp variables so new input is ready
                        }
                        else {
                            int[] move = board.getRandomMove();
                            start = move[0];
                            end = move[1];
                        }
                        if ((board.getLegalMoves(new int[]{start})[0] & (1L << (63-end))) == 0) //is move illegal
                            continue;
                        if (board.manageMove(start, end) >= 0) {
                            if (!board.getMove()) //white's move (move change already occured)
                                board.managePromotion(end%8, -1);
                            else
                                board.managePromotion(end%8, new Random().nextInt(4)*2+2); //select a random promotion piece
                        }
                        boardGui.updateBoard(); //display updated board
                        gameState = board.gameState(); //game over check for next iteration
                    }
                }
                catch (Exception e) {
                    e.printStackTrace(); //exceptions are otherwise "supressed" in the swingWorker
                }
                return null;
            }
            @Override
            protected void done() { //called when doInBackground finished (game over)
                Gui.displayGameOver(board);
            } 
        }.execute();
    }
    //for testing
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
    private static void runPerftTests(String filePath, int maxDepth) {
        int lineCounter = 1;
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines) {
                
                if (lineCounter%100 == 0) System.out.println("Parsing lines: "+lineCounter + " - " + (lineCounter+99));
                String[] parts = line.split(",");
                
                Board board = new Board(parts[0]);
                for (int i = 1; i<parts.length && i<maxDepth; i++) {
                    long expected = Long.parseLong(parts[i]);
                    long found = new Perft(board, i).countPositions();
                    if (expected != found) {
                        System.out.println("Test Failed on line "+lineCounter +": " + expected + " expected but " + found + " found");
                        System.out.println("FEN: " + parts[0]);
                        System.out.println("Depth: " + i);
                    }
                }
                lineCounter++;
            }
            System.out.println("Test Finished");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void runPerftTest(String FEN, int depth) {
        long startTime = System.nanoTime();
        long count = new Perft(new Board(FEN), depth).countPositions();
        long endTime = System.nanoTime();
        System.out.println(count + " positions at depth "+depth+" in "+((endTime-startTime)/1000000.0)+" ms");
    }
    private static void d1PerftDivide(String FEN) {
        Board board = new Board(FEN);
        int[] starts = board.getStartIndices(board.getMove());
        long[] moves = board.getLegalMoves(starts);
        for (int i = 0; i < starts.length; i++) {
            System.out.println("\nStart: " + starts[i]);
            displayBitboard(moves[i]);
        }
    }
}