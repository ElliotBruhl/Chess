public class Main {
    public static void main(String[] args) {

        Board board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"); //"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

        //System.out.println(Arrays.toString(GameManager.runGame(new MoveGenV1(), new MoveGenV2()))); //1 is win for V2, 0 is draw, -1 is win for V1
        //GameManager.runGuiGame(board, new Gui(board));
        GameManager.runGuiGame(board, new Gui(board), new MoveGenV2(), false); //modify depth manually in MoveGenV2 -> line 102 -> parameter #2 (use depth 5-6 optimally)
    }
}