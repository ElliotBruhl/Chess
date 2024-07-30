import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.SwingWorker;

public abstract class GameManager {

    public static int[] runGame(Player oldBot, Player newBot) { //two bots - 

        int[] move;
        int promotionBB;
        int[] result = new int[204];
        try {
            List<String> lines = Files.readAllLines(Paths.get("files/TrainingOpeningDB.txt"));
            for (int i = 0; i < lines.size(); i++) {
                System.out.println(i + " iterations");

                Board b = new Board(lines.get(i));
                try {
                    while (b.gameState() == 0) { //game with oldBot as white

                        move = b.getMove() ? oldBot.getMove(b) : newBot.getMove(b);
    
                        if (b.manageMove(move[0], move[1])) {
                            promotionBB = b.getMove() ? newBot.getPromotionBB(b) : oldBot.getPromotionBB(b); //move switch already occured
                                b.managePromotion(move[1]%8, promotionBB);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                switch (b.gameState()) { //convert values because players switch sides
                    case 1 -> {result[i*2] = 0;}
                    case 2 -> {result[i*2] = -1;}
                    case 3 -> {result[i*2] = 1;}
                    default -> {throw new Error("Play on in SRPT update");}
                }

                b = new Board(lines.get(i));
                try {
                    while (b.gameState() == 0) { //game with newBot as white

                        move = b.getMove() ? newBot.getMove(b) : oldBot.getMove(b);
    
                        if (b.manageMove(move[0], move[1])) {
                            promotionBB = b.getMove() ? oldBot.getPromotionBB(b) : newBot.getPromotionBB(b); //move switch already occured
                            b.managePromotion(move[1]%8, promotionBB);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                switch (b.gameState()) { //convert values because players switch sides
                    case 1 -> {result[i*2+1] = 0;}
                    case 2 -> {result[i*2+1] = 1;}
                    case 3 -> {result[i*2+1] = -1;}
                    default -> {throw new Error("Play on in SRPT update");}
                }
            }
        }
        catch (IOException e) {
            throw new Error("Error reading file");
        }

        return result;
    }
    public static void runGuiGame(Board board, Gui boardGui) { //both human players
        boardGui.updateBoard();
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() { //Gui and game logic must operate on seperate threads
                int[] move;

                while (board.gameState() == 0) { // 0 play on, 1 draw, 2 white win, 3 black win

                    boardGui.waitForValidInput(); //proceeds when valid start and end are inputed
                    move = boardGui.getClicks();
                    boardGui.resetStartEnd(); //clears the Gui temp variables so new input is ready

                    if ((board.getLegalMoves(new int[]{move[0]})[0] & (1L << (63-move[1]))) == 0) //is move illegal
                        continue;

                    if (board.manageMove(move[0], move[1]))
                        board.managePromotion(move[1]%8, -1);

                    boardGui.updateBoard(); //display updated board//game over check for next iteration
                }
                return null;
            }
            @Override
            protected void done() { //called when doInBackground finished (game over)
                Gui.displayGameOver(board);
            } 
        }.execute();
    }
    public static void runGuiGame(Board board, Gui boardGui, Player computer, boolean computerTurn) { //one human player
        boardGui.updateBoard();
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() { //Gui and game logic must operate on seperate threads
                int[] move;
                int promotionBB;

                while (board.gameState() == 0) { // 0 play on, 1 draw, 2 white win, 3 black win

                    if (board.getMove() == computerTurn) {
                        move = computer.getMove(board);
                    }
                    else {
                        boardGui.waitForValidInput(); //proceeds when valid start and end are inputed
                        move = boardGui.getClicks();
                        boardGui.resetStartEnd(); //clears the Gui temp variables so new input is ready
                    }
                    
                    if ((board.getLegalMoves(new int[]{move[0]})[0] & (1L << (63-move[1]))) == 0) {//is move illegal
                        if (board.getMove() == computerTurn) {
                            System.out.println("Illegal computer move. Start: " + move[0] + " End: " + move[1]);
                        }
                        else {
                            continue;
                        }
                    }

                    if (board.manageMove(move[0], move[1])) {
                        if (board.getMove() != computerTurn)
                            promotionBB = computer.getPromotionBB(board);
                        else
                            promotionBB = -1;

                        board.managePromotion(move[1]%8, promotionBB);
                    }
                    boardGui.updateBoard(); //display updated board//game over check for next iteration
                }
                return null;
            }
            @Override
            protected void done() { //called when doInBackground finished (game over)
                Gui.displayGameOver(board);
            } 
        }.execute();
    }
}
