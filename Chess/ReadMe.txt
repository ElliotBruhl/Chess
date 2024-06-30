App.java is main and handles game logic.
Board.java contains methods related to board attributes, board initialization, and game state checks. Board objects are designed so that future board states can be made with the 2nd constructor.
Piece.java contains methods related to piece attributes and legal move generation. getLegalMoves method returns an arraylist of all possible moves regardless of outcome, while the removePseudoLegal removes moves from that arraylist that leave the king in check after the move occurs.
Gui.java handles the gui using java swing. Also has event listeners to take input.

todo:
Fix random (but rare) legal move function missing moves
implement chess computer
-make a system so that future board states can be generated and traversed like a tree data structure
-make position evaluation function
-use minimax algorithm to create chess computer
