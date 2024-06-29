import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.*;

//buttons[i].setIcon(new ImageIcon("files\\Chess_klt45.png"));

public class Gui {
    private JFrame frame;
    private JPanel panel;
    private JButton[] buttons;
    private final HashMap<Character, String> pathMap;
    private int startClick;
    private int endClick;
    
    public Gui() {
        pathMap = new HashMap<Character, String>() {{
            put('b', "files\\Chess_bdt45.png");
            put('B', "files\\Chess_blt45.png");
            put('k', "files\\Chess_kdt45.png");
            put('K', "files\\Chess_klt45.png");
            put('n', "files\\Chess_ndt45.png");
            put('N', "files\\Chess_nlt45.png");
            put('p', "files\\Chess_pdt45.png");
            put('P', "files\\Chess_plt45.png");
            put('q', "files\\Chess_qdt45.png");
            put('Q', "files\\Chess_qlt45.png");
            put('r', "files\\Chess_rdt45.png");
            put('R', "files\\Chess_rlt45.png");
        }};

        initializeFrame();
        initializeBoard();
        frame.setVisible(true);
    }
    public void updateBoard(Board b) {
        Piece[] pos = b.getPosition();

        for (int i = 0; i < 64; i++) {
            if (pos[i] == null)
                buttons[i].setIcon(null); //removes image if no piece on that square
            else
                buttons[i].setIcon(new ImageIcon(pathMap.get(pos[i].getPieceID()))); //sets image based on piece ID
        }
    }
    private void initializeFrame() {
        frame = new JFrame("Chess Board");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 450);

        panel = new JPanel();
        panel.setLayout(new GridLayout(8, 8));
        frame.add(panel);
    }
    private void initializeBoard() {
        Color lightSquare = new Color(122, 133, 147);
        Color darkSquare = new Color(46, 54, 66);
        buttons = new JButton[64];
    
        for (int row = 7; row >= 0; row--) {
            for (int col = 0; col < 8; col++) {
                int index = row * 8 + col;
                buttons[index] = new JButton();
                buttons[index].setPreferredSize(new Dimension(45, 45));
                panel.add(buttons[index]);
    
                buttons[index].addActionListener(createButtonActionListener(index));
    
                if ((row + col) % 2 == 0)
                    buttons[index].setBackground(lightSquare);
                else
                    buttons[index].setBackground(darkSquare);
            }
        }
    }
    private ActionListener createButtonActionListener(int index) {
        return e -> System.out.println("Button clicked at index " + index);
    }
}
