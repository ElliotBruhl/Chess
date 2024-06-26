import java.util.ArrayList;
import java.util.Scanner;

public final class Board {

    private final Piece[] position;
    private boolean move; //true for white and false for black
    private int enPassantTarget; //index of pawn than can be enPassanted
    private int halfmoves; //for 50 move rule
    private final ArrayList<String> pastMoves; //for repitition

    public Board(String FEN) { //for initial/current board state
        //default values
        this.position = new Piece[64];
        this.enPassantTarget = -1;
        this.pastMoves = new ArrayList<>();

        try {readFEN(FEN);}
        catch (Exception e) {throw new Error("Error reading FEN");}
        
        this.pastMoves.add(generatePosHash());
    }
    public Board(Piece[] pos, boolean m, int ePT, int hm, ArrayList<String> pM) { //for generating future board states
        this.position = pos.clone(); //this is a shallow copy, be careful making changes to piece attributes
        this.move = m;
        this.enPassantTarget = ePT;
        this.halfmoves = hm;
        this.pastMoves = pM;
    }
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
    
        for (int rank = 7; rank >= 0; rank--) {
            for (int file = 0; file < 8; file++) {
                int squareIndex = rank * 8 + file;
                Piece piece = this.position[squareIndex];
                if (piece != null)
                    str.append(piece.getPieceID());
                else
                    str.append("-");
                str.append(" ");
            }
            str.append("\n");
        }
        return str.toString();
    }
    public Piece[] getPosition(){
        return this.position;
    }
    public Piece getPieceOnSquare(int square) {
        return this.position[square];
    }
    public void removePiece(int square) {
        this.position[square] = null;
    }
    public void movePiece(int startIndex, int endIndex) {
        this.position[endIndex] = this.position[startIndex];
        this.position[startIndex] = null;
    }
    public boolean getMove() {
        return this.move;
    }
    public void setMove(boolean b) {
        this.move = b;
    }
    public void nextMove() {
        this.move = !this.move;
    }
    public int getEnPassantTarget() {
        return this.enPassantTarget;
    }
    public void setEnPassantTarget(int ePT) {
        this.enPassantTarget = ePT;
    }
    public int getHalfmoves() {
        return this.halfmoves;
    }
    public void setHalfmoves(int hm) {
        this.halfmoves = hm;
    }
    public void addHalfmove() {
        this.halfmoves++;
    }
    public ArrayList<String> getPastMoves() {
        return this.pastMoves;
    }
    public void addPastMove(String s) {
        this.pastMoves.add(0, s);
    }
    public void clearPastMove() {
        this.pastMoves.clear();
    }
    public int getKingIndex() {
        for (int i = 0; i < 64; i++) {
            if (this.position[i] == null)
                continue;
            if (this.move && this.position[i].getPieceID() == 'K' || !this.move && this.position[i].getPieceID() == 'k')
                return i;
        }
        throw new Error("No King Found of color");
    }
    public boolean KinCheck(int kingIndex) {
        for (int i = 0; i < 64; i++) {
            if (this.position[i] == null || (Character.isUpperCase(this.position[i].getPieceID()) == this.move))
                continue;
            if (this.position[i].getLegalMoves(i, this, false).contains(kingIndex)) {
                return true;
            }
        }
        return false;
    }
    public int gameOver() { //0 play on, 1 draw, 2 white win, 3 black win
        int pieceCount = 0; //stalemate if <4 and
        boolean sufMat = false; //no queen/rook/pawn

        boolean hasMove = false; //false until legal move for current color found

        int posCount = 0;

        for (int i = 0; i < 64; i++) { //gather most info in one loop through
            Piece piece = this.position[i];
            if (piece != null) {
                pieceCount++;
                char ID = piece.getPieceID();
                if (!hasMove && Character.isUpperCase(ID) == this.move)
                    hasMove = !piece.getLegalMoves(i, this, true).isEmpty();
                if (!sufMat && (ID == 'P' || ID == 'Q' || ID == 'R' || ID == 'p' || ID == 'q' || ID == 'r'))
                    sufMat = true;
            }
        }
        if (!hasMove) { //stalemate and checkmate
            if (KinCheck(getKingIndex())) { //is king in check
                if (this.move) {
                    System.out.println("Checkmate! Black Wins!"); //remove after debug------------------
                    return 3;
                }
                else {
                    System.out.println("Checkmate! White Wins!"); //remove after debug-----------------
                    return 2;
                }
            }
            System.out.println("Draw by stalemate."); //remove after debug-------------------------
            return 1;
        }
        if (this.halfmoves >= 100) { //50 move rule
            System.out.println("Draw by 50 move rule."); //remove after debug--------------------
            return 1;
        }
        if (pieceCount < 4 && !sufMat) { //insufficient material
            System.out.println("Draw by insufficient material"); //remove after debug-------------
            return 1;
        }
        for (String s : this.pastMoves) { //repitition
            if (s.equals(pastMoves.get(0))) {
                posCount++;
                if (posCount >= 3) {
                    System.out.println("Draw by repitition."); //remove after debug-----------------
                    return 1;
                }
            }
        }
    return 0;
    }
    public int getStart(Scanner sc) {
        int start;
        try {
            start = Integer.parseInt(sc.nextLine());
            while ((start > 63 || start < 0) || (this.position[start] == null)) {
                System.out.println("Enter a number [0,63] that has a piece on it");
                start = Integer.parseInt(sc.nextLine());
            }
            if (this.position[start].isWhite() != this.move) {
                System.out.println("You must select one of your pieces");
                start = getStart(sc);
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid integer.");
            start = getStart(sc);
        }
        return start;
    }
    public int getEnd(Scanner sc) {
        int end;
        try {
            end = Integer.parseInt(sc.nextLine());
            while (end > 63 || end < 0) {
                System.out.println("Enter a number [0,63]");
                end = Integer.parseInt(sc.nextLine());
            }
            if (this.position[end] != null && this.position[end].isWhite() == this.move) {
                System.out.println("You can't take your own piece.");
                end = getEnd(sc);
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid integer.");
            end = getEnd(sc);
        }
        return end;
    }
    public char getPromotionID(Scanner sc) {
        char newID;
        try {
            newID = Character.toUpperCase(sc.next().charAt(0));
            while (!(newID == 'R' || newID == 'N' || newID == 'B' || newID == 'Q')) {
                System.out.println("You must promote to R for rook, N for knight, B bishop, or Q for queen");
                newID = Character.toUpperCase(sc.next().charAt(0));
            }
        } catch (Exception e) {
            System.out.println("Please enter a valid character.");
            newID = getPromotionID(sc);
        }
        return move ? Character.toUpperCase(newID) : Character.toLowerCase(newID);
    }
    public String generatePosHash() {
        StringBuilder posHash = new StringBuilder();
        for (Piece p : this.position) {
            if (p == null)
                posHash.append('-');
            else
                posHash.append(p.getPieceID());
        }
        return posHash.toString();
    }
    private void readFEN(String FEN) { //assumes FEN is valid
        String[] splitFEN = FEN.split(" "); //splits fen into the 6 pieces

        //-----------------------------handles position part of FEN-----------------------------
        int current_square = 0;
        int file = 0;

        for (int i = 0; i < splitFEN[0].length(); i++) {
            char c = splitFEN[0].charAt(i);

            if (Character.isDigit(c)) { // If it's a digit, it represents empty squares
                int emptySquares = Character.getNumericValue(c);
                current_square += emptySquares;
                file += emptySquares;

            } else if (c == '/') { // If '/' move to the next rank
                current_square += (8-file);
                file = 0;

            } else { // There's a piece there
                this.position[(7 - current_square / 8) * 8 + current_square % 8] = new Piece(c);
                current_square++;
                file++;
            } 
        }
        //-----------------------------handles move(w/b) part of FEN-----------------------------
        char c = splitFEN[1].charAt(0);

        if (c == 'w') {
            setMove(true);
        }
        else {
            setMove(false);
        }
        //-----------------------------handles castling rights part of FEN-----------------------------
        if (splitFEN[2].contains("Q")) {
            this.position[0].setCanCastle(true);
            this.position[4].setCanCastle(true);
        }
        if (splitFEN[2].contains("K")) {
            this.position[7].setCanCastle(true);
            this.position[4].setCanCastle(true);
        }
        if (splitFEN[2].contains("q")) {
            this.position[56].setCanCastle(true);
            this.position[60].setCanCastle(true);
        }
        if (splitFEN[2].contains("k")) {
            this.position[63].setCanCastle(true);
            this.position[60].setCanCastle(true);
        }
        //-----------------------------handles enPassant target part of FEN-----------------------------
        if (splitFEN[3].length() == 2) {
            setEnPassantTarget(((splitFEN[3].charAt(1) - '1') * 8) + (splitFEN[3].charAt(0) - 'a')); //converts fram algebraic notation to index
        }
        //-----------------------------handles halfmove part of FEN-----------------------------
        setHalfmoves(Integer.parseInt(splitFEN[4]));
    }
}
