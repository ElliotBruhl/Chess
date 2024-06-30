import java.util.ArrayList;

public final class Piece {
    private char pieceID; //FEN notation for pieces
    private final boolean color; //true for white false for black
    private boolean castle = false; //true for rooks and kings that havent moved, false otherwise

    public Piece(char ID){
        pieceID = ID;
        color = Character.isUpperCase(ID);
    }
    public char getPieceID() {
        return pieceID;
    }
    public void setPieceID(char pieceID) {
        this.pieceID = pieceID;
    }
    public boolean isWhite() {
        return color;
    }
    public boolean CanCastle() {
        return castle;
    }
    public void setCanCastle(boolean c) {
        castle = c;
    }
    public static ArrayList<Integer> getLegalMoves(int index, Board b, boolean fullLegal) { //fullLegal is false when checking if a king is in check and true otherwise
        ArrayList<Integer> legalMoves = new ArrayList<>();
        Piece[] pos = b.getPosition();

        switch (pos[index].getPieceID()) {
            case 'P' -> // white pawn
                addWhitePawnMoves(legalMoves, index, pos, b.getEnPassantTarget());
            case 'p' -> // black pawn
                addBlackPawnMoves(legalMoves, index, pos, b.getEnPassantTarget());
            case 'R', 'r' -> //rooks
                addRookMoves(legalMoves, index, pos);
            case 'N', 'n' -> addKnightMoves(legalMoves, index, pos);
            case 'B', 'b' -> //knights
                addBishopMoves(legalMoves, index, pos);
            case 'Q', 'q' -> {
                addRookMoves(legalMoves, index, pos);
                addBishopMoves(legalMoves, index, pos);
            }
            case 'K', 'k' -> //bishops
                addKingMoves(legalMoves, index, pos);
            default -> throw new Error("Piece ID not found");
        }

        if (fullLegal) {
            removePsedoLegal(legalMoves, index, b);
        }
        
        return legalMoves;
    }
    private static void removePsedoLegal(ArrayList<Integer> legalMoves, int index, Board oldBoard) { //index is starting square of piece

        for (int i = 0; i < legalMoves.size(); i++) { //be careful with removing elements while iterating

            Board newBoard = new Board(oldBoard.getPosition(), oldBoard.getMove(), oldBoard.getEnPassantTarget(), oldBoard.getHalfmoves(), oldBoard.getPastMoves()); //make new board for testing move
            int targetIndex = legalMoves.get(i); //move being tested
            newBoard.movePiece(index, targetIndex); //do the move on new board
            char ID = newBoard.getPieceOnSquare(targetIndex).getPieceID();

            //special moves
            if (targetIndex == newBoard.getEnPassantTarget() && Character.toUpperCase(ID) == 'P') { //for enPassant
                if (Character.isUpperCase(ID))
                    newBoard.removePiece(targetIndex-8);
                else
                    newBoard.removePiece(targetIndex+8);
            }
            if (ID == 'K') { //for removing castle through or out of check
                if (targetIndex - index == 2) { //white kingside
                    newBoard.movePiece(6, 4); //no castling out of check
                    if (newBoard.KinCheck(4)) {
                        legalMoves.remove(i);
                        i--;
                        continue;
                    }
                    newBoard.movePiece(4,5); //no castling through check
                    if (newBoard.KinCheck(5)) {
                        legalMoves.remove(i);
                        i--;
                        continue;
                    }
                    newBoard.movePiece(5,6); //move king
                    newBoard.movePiece(7, 5); //move the rook
                }
                else if (index - targetIndex == 2) { //white queenside
                    newBoard.movePiece(2, 4); //no castling out of check
                    if (newBoard.KinCheck(4)) {
                        legalMoves.remove(i);
                        i--;
                        continue;
                    }
                    newBoard.movePiece(4,3); //no castling through check
                    if (newBoard.KinCheck(3)) {
                        legalMoves.remove(i);
                        i--;
                        continue;
                    }
                    newBoard.movePiece(3,2); //move king
                    newBoard.movePiece(0, 3); //move the rook
                }
            }
            else if (ID == 'k') {
                if (targetIndex - index == 2) { //black kingside
                    newBoard.movePiece(62, 60); //no castling out of check
                    if (newBoard.KinCheck(60)) {
                        legalMoves.remove(i);
                        i--;
                        continue;
                    }
                    newBoard.movePiece(60,61); //no castling through check
                    if (newBoard.KinCheck(61)) {
                        legalMoves.remove(i);
                        i--;
                        continue;
                    }
                    newBoard.movePiece(61,62); //move king
                    newBoard.movePiece(63, 61); //move the rook
                }
                else if (index - targetIndex == 2) { //black queenside
                    newBoard.movePiece(58, 60); //no castling out of check
                    if (newBoard.KinCheck(60)) {
                        legalMoves.remove(i);
                        i--;
                        continue;
                    }
                    newBoard.movePiece(60,59); //no castling through check
                    if (newBoard.KinCheck(59)) {
                        legalMoves.remove(i);
                        i--;
                        continue;
                    }
                    newBoard.movePiece(59,58); //move king
                    newBoard.movePiece(56, 59); //move the rook
                }
            }

            //everthing else and final castle position
            if (newBoard.KinCheck(newBoard.getKingIndex())) { 
                legalMoves.remove(i);
                i--;
            }
        }
    }
    private static void addWhitePawnMoves(ArrayList<Integer> legalMoves, int index, Piece[] pos, int ePT) {
        int row = index / 8;
        int col = index % 8;
        
        if (pos[index + 8] == null) {
            legalMoves.add(index + 8);
            if (row == 1 && pos[index + 16] == null) {
                legalMoves.add(index + 16);
            }
        }
        
        if (col != 7) { //capture right
            if (pos[index + 9] != null && !pos[index + 9].isWhite()) {
                legalMoves.add(index + 9);
            } else if (index + 9 == ePT) { // En passant right
                legalMoves.add(index + 9);
            }
        }
        
        if (col != 0) { //capture left
            if (pos[index + 7] != null && !pos[index + 7].isWhite()) {
                legalMoves.add(index + 7);
            } else if (index + 7 == ePT) { // En passant left
                legalMoves.add(index + 7);
            }
        }
    }
    private static void addBlackPawnMoves(ArrayList<Integer> legalMoves, int index, Piece[] pos, int ePT) {
        int row = index / 8;
        int col = index % 8;
        
        if (pos[index - 8] == null) {
            legalMoves.add(index - 8);
            if (row == 6 && pos[index - 16] == null) {
                legalMoves.add(index - 16);
            }
        }
        
        if (col != 0) { //capture left
            if (pos[index - 9] != null && pos[index - 9].isWhite()) {
                legalMoves.add(index - 9);
            } else if (index - 9 == ePT) { // En passant left
                legalMoves.add(index - 9);
            }
        }
        
        if (col != 7) { //capture right
            if (pos[index - 7] != null && pos[index - 7].isWhite()) {
                legalMoves.add(index - 7);
            } else if (index - 7 == ePT) { // En passant right
                legalMoves.add(index - 7);
            }
        }
    }
    private static void addRookMoves(ArrayList<Integer> legalMoves, int index, Piece[] pos) {
        int[][] directions = {{8, 1}, {-8, 1}, {1, 0}, {-1, 0}}; // up, down, right, left directions
    
        for (int[] direction : directions) {
            int step = direction[0];
            int maxSteps = direction[1] == 1 ? (7 - (index / 8)) : (index / 8);
            if (direction[0] == 1) {
                maxSteps = 7 - (index % 8);
            } else if (direction[0] == -1) {
                maxSteps = index % 8;
            }
            
            for (int i = 1; i <= maxSteps; i++) {
                int newIndex = index + i * step;
    
                if (newIndex < 0 || newIndex > 63) break;
    
                if (pos[newIndex] == null) {
                    legalMoves.add(newIndex);
                } else {
                    if (pos[newIndex].isWhite() != pos[index].isWhite()) {
                        legalMoves.add(newIndex);
                    }
                    break;
                }
            }
        }
    }
    private static void addBishopMoves(ArrayList<Integer> legalMoves, int index, Piece[] pos) {
        int[][] directions = {{9, 1}, {-7, -1}, {-9, -1}, {7, 1}}; // NE, SE, SW, NW directions
    
        for (int[] direction : directions) {
            int step = direction[0];
            int currentIndex = index + step;
    
            while (currentIndex >= 0 && currentIndex <= 63 &&
                   Math.abs((currentIndex % 8) - (index % 8)) == Math.abs((currentIndex / 8) - (index / 8))) {
                if (pos[currentIndex] == null) {
                    legalMoves.add(currentIndex);
                } else {
                    if (pos[currentIndex].isWhite() != pos[index].isWhite()) {
                        legalMoves.add(currentIndex);
                    }
                    break;
                }
                currentIndex += step;
            }
        }
    }
    private static void addKnightMoves(ArrayList<Integer> legalMoves, int index, Piece[] pos) {
        int[] nMoves = {6, -10, 15, -17, 17, -15, 10, -6}; // Offsets for knight moves
        int startVal = 0;
        int direction = 1;
    
        switch (index % 8) {
            case 0 -> startVal = 4;
            case 1 -> startVal = 2;
            case 6 -> {
                startVal = 5;
                direction = -1;
            }
            case 7 -> {
                startVal = 3;
                direction = -1;
            }
            default -> {
            }
        }
    
        for (int i = startVal; i >= 0 && i <= 7; i += direction) {
            int move = nMoves[i];
            int newIndex = index + move;
    
            if (newIndex >= 0 && newIndex <= 63 && 
                (pos[newIndex] == null || pos[newIndex].isWhite() != pos[index].isWhite())) {
                legalMoves.add(newIndex);
            }
        }
    }
    private static void addKingMoves(ArrayList<Integer> legalMoves, int index, Piece[] pos) {
        int[] kMoves = {-9, -1, 7, -8, 8, -7, 1, 9}; //offsets {-1, -1, -1, 0, 0, 1, 1, 1}
        int startVal = 0;
        int direction = 1;
        switch (index%8) {
            case 0 -> startVal = 3;
            case 7 -> {
                startVal = 4;
                direction = -1;
            }
            default -> {
            }
        }
        
        for (int i = startVal; i >= 0 && i <= 7; i += direction) {
            int move = kMoves[i];
            int newIndex = index + move;
        
            if (newIndex <= 63 && newIndex >= 0 && 
                (pos[newIndex] == null || pos[newIndex].isWhite() != pos[index].isWhite())) {
                legalMoves.add(newIndex);
            }
        }

        if (pos[index].CanCastle()) { //castling
            if (pos[index + 3] != null && pos[index + 3].CanCastle() && 
                pos[index + 1] == null && pos[index + 2] == null) {
                legalMoves.add(index + 2);
            }
            if (pos[index - 4] != null && pos[index - 4].CanCastle() && 
                pos[index - 1] == null && pos[index - 2] == null && pos[index - 3] == null) {
                legalMoves.add(index - 3);
            }
        }
    }
}