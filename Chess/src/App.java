import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Board b = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"); //starting pos: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
        Scanner sc = new Scanner(System.in);
        int gameState = b.gameState();
        boolean useGui = true;
        int start;
        int end;
        Gui boardGui = null;

        if (useGui) {
            boardGui = new Gui(b);
            boardGui.updateBoard(b);
        }
        else {
            System.out.println(b);
        }

        while (gameState == 0) { //0 play on, 1 draw, 2 white win, 3 black win

            if (useGui) {
                start = 0;
                end = 0;
            }
            else {
                System.out.println("Enter start square"); //index of piece of correct color
                start = b.getStart(sc);
                System.out.println("Enter target square"); //index of null or other color piece
                end = b.getEnd(sc);
            }
            //legal move check
            if (!Piece.getLegalMoves(start, b, true).contains(end)) {
                System.out.println("Piece can't move there. Try again.");
                continue;
            }

            //pre move management (castling/enPassant/promotion/halfmoves)
            manageSpecialMoves(start, end, b, sc);

            b.movePiece(start, end); //move the piece

            //post move management
            b.addPastMove(b.generatePosHash()); //add position to repitition array
            b.nextMove(); //next color's turn
            gameState = b.gameState(); //update gamestate

            if (useGui) {
                boardGui.updateBoard(b);
            }
            else {
            System.out.println(b); //print new board
            }
        }
        //game over messages
        switch (gameState) { 
            case 1 -> System.out.println("Draw.");
            case 2 -> System.out.println("White Wins!");
            case 3 -> System.out.println("Black Wins!");
            default -> throw new Error("Bad Gamestate");
        }
    }
    private static void manageSpecialMoves(int start, int end, Board b, Scanner sc) {
        Piece sPiece = b.getPieceOnSquare(start);
        char sPieceID = sPiece.getPieceID();

        //castling
        if (sPieceID == 'K' && start == 4) { //white king in castling position
            if (end == 6)
                b.movePiece(7, 5); //kingside
            else if (end == 2)
                b.movePiece(0, 3); //queenside
        }
        else if (sPieceID == 'k' && start == 60) { //black king in castling position
            if (end == 62)
                b.movePiece(63,61); //kingside
            else if (end == 58)
                b.movePiece(56, 59); //queenside
        }
        //enPassant
        if (sPieceID == 'P' && end == b.getEnPassantTarget()) //remove the pawn being enPassanted
            b.removePiece(end-8);
        else if (sPieceID == 'p' && end == b.getEnPassantTarget())
            b.removePiece(end+8);
        b.setEnPassantTarget(-1); //manage enPassant target
        if (sPieceID == 'P' && end-start == 16)
            b.setEnPassantTarget(start+8);
        else if (sPieceID == 'p' && start-end == 16)
            b.setEnPassantTarget(start-8);
        if (sPieceID == 'K' || sPieceID == 'k' || sPieceID == 'R' || sPieceID == 'r') //manage castling rights
            sPiece.setCanCastle(false);
        //promotion
        if (sPieceID == 'P' && end >= 56)
            sPiece.setPieceID(b.getPromotionID(sc));
        else if (sPieceID == 'p' && end <= 7)
            sPiece.setPieceID(b.getPromotionID(sc));
        //halfmoves (50 move rule)
        if (sPieceID == 'P' || sPieceID == 'p' || b.getPieceOnSquare(end) != null) {
            b.setHalfmoves(1);
            b.clearPastMove();
        }
        else
            b.addHalfmove();
    }
}
