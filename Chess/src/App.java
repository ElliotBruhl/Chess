import java.util.Scanner;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class App {

    public static void main(String[] args) {

        boolean useGui = true; // false for command line and true for gui

        Board board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"); //starting FEN position

        if (useGui)
            startGuiGame(board, new Gui(board));
        else
            startCommandLineGame(board);
    }

    private static void startGuiGame(Board board, Gui boardGui) {

        boardGui.updateBoard(board);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() { //Gui and game logic must operate on seperate threads

                int gameState = board.gameState();

                while (gameState == 0) { // 0 play on, 1 draw, 2 white win, 3 black win
                    
                    boardGui.waitForValidInput(boardGui); //proceeds when valid start and end are inputed
                    int start = boardGui.getStartClick();
                    int end = boardGui.getEndClick();
                    boardGui.resetStartEnd(); //clears the Gui temp variables so new input is ready

                    if (!Piece.getLegalMoves(start, board, true).contains(end)) { //legal move check
                        continue;
                    }
                    board.manageSpecialMoves(start, end, true, boardGui); //for enPassant, castling, promotion, 50 move rule
                    board.movePiece(start, end); //change position array
                    board.addPastMove(board.generatePosHash()); //for 3 fold repitition
                    board.nextMove(); //other player's turn
                    SwingUtilities.invokeLater(() -> boardGui.updateBoard(board)); //display updated board
                    gameState = board.gameState(); //game over check for next iteration
                }
                return null;
            }
            @Override
            protected void done() { //called when doInBackground finished (game over)
                Gui.displayGameOver(board);
            } 
        }.execute();
    }

    private static void startCommandLineGame(Board board) {
        Scanner scanner = new Scanner(System.in);
        int gameState = board.gameState();

        while (gameState == 0) {
            int start = board.getStart(scanner);
            int end = board.getEnd(scanner);

            if (!Piece.getLegalMoves(start, board, true).contains(end)) { //legal move check
                System.out.println("Piece can't move there. Try again.");
                continue;
            }

            board.manageSpecialMoves(start, end, false, null); //for enPassant, castling, promotion, 50 move rule
            board.movePiece(start, end); //change position array
            board.addPastMove(board.generatePosHash()); //for 3 fold repitition
            board.nextMove(); //other player's turn
            System.out.println(board); //display updated board
            gameState = board.gameState(); //game over check for next iteration
        }
        switch (gameState) { //display game over message
            case 1 -> System.out.println("Draw");
            case 2 -> System.out.println("White Wins!");
            case 3 -> System.out.println("Black Wins!");
            default -> throw new IllegalStateException("Unexpected value: " + gameState);
        }
    }
}
