public class App {
    public static void main(String[] args) {

        Board board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"); //"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

        //System.out.println(GameManager.runGame(new MoveGenV1(), new MoveGenV1()));
        //GameManager.runGuiGame(board, new Gui(board));
        GameManager.runGuiGame(board, new Gui(board), new MoveGenV1(), false);
    }
}