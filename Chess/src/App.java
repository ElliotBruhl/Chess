import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Board b = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"); //starting pos: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
        Scanner sc = new Scanner(System.in);
        int start; //from user
        int end; //from user
        int gameState = 0; //0 play on, 1 draw, 2 white win, 3 black win

        System.out.println(b);

        while (gameState == 0) {

            System.out.println("Enter start square");
            start = b.getStart(sc);

            System.out.println("Enter target square");
            end = b.getEnd(sc);

            Piece sPiece = b.getPieceOnSquare(start); //common references
            char sPieceID = sPiece.getPieceID();

            if (sPiece.getLegalMoves(start, b, true).contains(end)) { //todo --------- put post move management into a function ---------

                //manage moving rook in castling
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
                
                if (sPieceID == 'P' && end >= 56) //manage promotion
                    sPiece.setPieceID(b.getPromotionID(sc));
                else if (sPieceID == 'p' && end <= 7)
                    sPiece.setPieceID(b.getPromotionID(sc));
                
                if (sPieceID == 'P' || sPieceID == 'p' || b.getPieceOnSquare(end) != null) {//manages halfmoves (50 move rule)
                    b.setHalfmoves(1);
                    b.clearPastMove();
                }
                else
                    b.addHalfmove();

                b.movePiece(start, end); //move the piece

                b.addPastMove(b.generatePosHash()); //add position to repitition array

                b.nextMove(); //next color's turn

                System.out.println(b); //print board

                gameState = b.gameOver();
            }
            else {
                System.out.println("Piece can't move there. Try again.");
            }
        }
        switch (gameState) {
            case 1:
                System.out.println("Draw.");
                break;
            case 2:
                System.out.println("White Wins!");
                break;
            case 3:
                System.out.println("Black Wins!");
                break;
            default:
                throw new Error("Unknown gamestate");
        }
        sc.close();
        System.exit(0);
    }
}
