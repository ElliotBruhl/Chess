import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import javax.swing.*;

public class App {

    public static void main(String[] args) {
        boolean useGui = false; // false for command line and true for gui

        Board board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        Gui boardGui = useGui ? new Gui(board) : null;

        if (useGui) {
            startGuiGame(board, boardGui);
        } else {
            startCommandLineGame(board);
        }
    }

    private static void startGuiGame(Board board, Gui boardGui) {
        boardGui.updateBoard(board);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                int gameState = board.gameState();
                while (gameState == 0) { // 0 play on, 1 draw, 2 white win, 3 black win
                    
                    boardGui.waitForValidInput(boardGui);

                    int start = boardGui.getStartClick();
                    int end = boardGui.getEndClick();
                    boardGui.resetStartEnd();

                    if (!Piece.getLegalMoves(start, board, true).contains(end)) {
                        //put gui message for illegal move
                        continue;
                    }

                    manageSpecialMoves(start, end, board, true);
                    board.movePiece(start, end);

                    board.addPastMove(board.generatePosHash());
                    board.nextMove();
                    SwingUtilities.invokeLater(() -> boardGui.updateBoard(board));

                    gameState = board.gameState();
                }
                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                    displayGameOver(board);
                } catch (InterruptedException | ExecutionException e) {}
            }
        }.execute();
    }
    private static void displayGameOver(Board board) {
        /*
        switch (board.gameState()) {
            case 1 -> {something}
            case 2 -> {something}
            case 3 -> {something}
            default -> throw new IllegalStateException("Unexpected value:");
        }
        */
    }
    private static void GuiPromotion(Piece sPiece, Board board) {
        //sPiece.setPieceID(something);
    }

    private static void startCommandLineGame(Board board) {
        Scanner scanner = new Scanner(System.in);
        int gameState = board.gameState();

        while (gameState == 0) {
            int start = board.getStart(scanner);
            int end = board.getEnd(scanner);

            if (!Piece.getLegalMoves(start, board, true).contains(end)) {
                System.out.println("Piece can't move there. Try again.");
                continue;
            }

            manageSpecialMoves(start, end, board, false);
            board.movePiece(start, end);

            board.addPastMove(board.generatePosHash());
            board.nextMove();

            System.out.println(board);
            gameState = board.gameState();
        }
        System.out.println(printGameOver(gameState));
    }
    private static String printGameOver(int gameState) {
        switch (gameState) {
            case 1 -> {return "Draw.";}
            case 2 -> {return "White Wins!";}
            case 3 -> {return "Black Wins!";}
            default -> throw new IllegalStateException("Unexpected value: " + gameState);
        }
    }
    private static void commandLinePromotion(Piece sPiece, Board board, Scanner scanner) {
        sPiece.setPieceID(board.getPromotionID(scanner));
    }

    private static void manageSpecialMoves(int start, int end, Board board, boolean useGui) {
        Piece sPiece = board.getPieceOnSquare(start);
        char sPieceID = sPiece.getPieceID();

        // Castling
        if (sPieceID == 'K' && start == 4) { // White king in castling position
            if (end == 6)
                board.movePiece(7, 5); // Kingside
            else if (end == 2)
                board.movePiece(0, 3); // Queenside
        } else if (sPieceID == 'k' && start == 60) { // Black king in castling position
            if (end == 62)
                board.movePiece(63, 61); // Kingside
            else if (end == 58)
                board.movePiece(56, 59); // Queenside
        }
        // En passant
        if (sPieceID == 'P' && end == board.getEnPassantTarget()) // Remove the pawn being en passant captured
            board.removePiece(end - 8);
        else if (sPieceID == 'p' && end == board.getEnPassantTarget())
            board.removePiece(end + 8);
        board.setEnPassantTarget(-1); // Manage en passant target
        if (sPieceID == 'P' && end - start == 16)
            board.setEnPassantTarget(start + 8);
        else if (sPieceID == 'p' && start - end == 16)
            board.setEnPassantTarget(start - 8);
        if (sPieceID == 'K' || sPieceID == 'k' || sPieceID == 'R' || sPieceID == 'r') // Manage castling rights
            sPiece.setCanCastle(false);
        // Halfmoves (50 move rule)
        if (sPieceID == 'P' || sPieceID == 'p' || board.getPieceOnSquare(end) != null) {
            board.setHalfmoves(1);
            board.clearPastMove();
        } else
            board.addHalfmove();
        if ((sPieceID == 'P' && end >= 56) || (sPieceID == 'p' && end <= 7)) {
            if (useGui) {
                GuiPromotion(sPiece, board);
            }
            else {
                commandLinePromotion(sPiece, board, new Scanner(System.in));
            }
        }
    }
}
