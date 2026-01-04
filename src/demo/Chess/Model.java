package demo.Chess;

/*
 * Model class to manage the game state and logic.
*/

public class Model {
	// position state
    public static final int SPACE = 0;
    public static final int BLACK = 1;
	public static final int WHITE = 2;
	
	// chessboard size
	public static final int WIDTH  = 15;
    public static final int HEIGHT = 15;
    
    // direction 
    private static final int UP 	= -1;
    private static final int DOWN 	= 1;
    private static final int LEFT 	= -1;
    private static final int RIGHT	= 1;
    private static final int CENTER 	= 0;
    // {UpLeft, UpCenter, UpRight, CenterRight, DownRight, DownCenter, DownLeft, CenterLeft}
    private static final int UPLEFT       = 0;
    private static final int UPCENTER     = 1;
	private static final int UPRIGHT      = 2;
	private static final int CENTERRIGHT  = 3;
    private static final int DOWNRIGHT    = 4;
	private static final int DOWNCENTER   = 5;
	private static final int DOWNLEFT     = 6;
    private static final int CENTERLEFT   = 7;
    private static final int DIRECTIONNUM = 8;
    private static final int []CrossDirection 	= {UP, UP, UP, CENTER, DOWN, DOWN, DOWN, CENTER};
    private static final int []VerticalDirection = {LEFT, CENTER, RIGHT, RIGHT, RIGHT, CENTER, LEFT, LEFT}; 

    // game state
    public static final int ONGOING      = 0;
    public static final int BLACKWIN     = 1;
    public static final int WHITEWIN     = 2;
    public static final int DRAW         = 3;
    public static final int ABNORMALEXIT = 4;
    
    private int[][] ChessBoard = new int[WIDTH][HEIGHT];
    // BLACK starts first
    private int currentTurn = BLACK; 
    private int gameState = DRAW; 

    
    

    Model(){
    	// Initial chessboard
        for(int i = 0; i < WIDTH; i++){
            for(int j = 0; j < HEIGHT; j++){
            	ChessBoard[i][j] = SPACE;
            }
        }
        // Test setup
        // ChessBoard[7][7] = BLACK;
        // ChessBoard[7][8] = WHITE;
    }

    public int getPosition(int x, int y){
        return ChessBoard[x][y];
    }

    public void setPosition(int x, int y, int color){
        if(gameState != ONGOING) {
        	System.out.println("Game is already over. Cannot place more pieces.");
        	return;
        }
        if(ChessBoard[x][y] == BLACK || ChessBoard[x][y] == WHITE) {
        	System.out.println("Position already occupied. Choose another position.");
        	return;
        }
        if(color == BLACK || color == WHITE){
        	ChessBoard[x][y] = color;
            boolean gameOver = GameOver(x, y);
            if(gameOver) {
            	if(color == BLACK) {
            		gameState = BLACKWIN;
            	} else {
            		gameState = WHITEWIN;
            	}
            } else {
            	// Switch turn
            	if(currentTurn == BLACK) {
            		currentTurn = WHITE;
            	} else {
            		currentTurn = BLACK;
            	}
            	gameState = ONGOING;
            }
        } else {
            throw new IllegalArgumentException("Invalid color value");
        }
    }

    
    
    public boolean IsPositionInBoard(int x, int y) {
    	if(x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
    		return false;
    	}
    	return true;
    }
    
    public boolean GameOver(int changeX, int changeY) {
    	// only last chess can end the game
    	int CheckChessColor = ChessBoard[changeX][changeY];
    	for(int i = 0; i < DIRECTIONNUM; ++i) {
    		boolean overFlag = true;
    		int directX = VerticalDirection[i];
    		int directY = CrossDirection[i];
    		for(int j = 1; j <= 4; ++j) {
    			int checkX = changeX + directX * j;
    			int checkY = changeY + directY * j;
    			if(IsPositionInBoard(checkX, checkY) == false || CheckChessColor != ChessBoard[checkX][checkY]) {
    				overFlag = false;
    				break;
    			}
    		}
    		if(overFlag == true) {
    			return true;
    		}
    	}
    	return false;
    }

    public int getCurrentTurn() {
    	return currentTurn;
    }

    public int getGameState() {
    	return gameState;
    }

    public void setGameState(int state) {
    	gameState = state;
    }

    public void resetGame() {
    	// Reset chessboard
        gameState = DRAW;
        for(int i = 0; i < WIDTH; i++){
            for(int j = 0; j < HEIGHT; j++){
            	ChessBoard[i][j] = SPACE;
            }
        }
        currentTurn = BLACK;
        gameState = ONGOING;
    }

    public void applyState(int[][] boardState, int currentTurnValue, int gameStateValue) {
        if (boardState == null) {
            return;
        }
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                int value = SPACE;
                if (x < boardState.length && y < boardState[x].length) {
                    value = normalizePieceValue(boardState[x][y]);
                }
                ChessBoard[x][y] = value;
            }
        }
        if (currentTurnValue == BLACK || currentTurnValue == WHITE) {
            currentTurn = currentTurnValue;
        }
        if (gameStateValue >= ONGOING && gameStateValue <= ABNORMALEXIT) {
            gameState = gameStateValue;
        }
    }

    private int normalizePieceValue(int value) {
        if (value == BLACK || value == WHITE) {
            return value;
        }
        return SPACE;
    }

    public String serializeBoard() {
        StringBuilder string_builder_ = new StringBuilder(WIDTH * HEIGHT * 2);
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (string_builder_.length() > 0) {
                    string_builder_.append(',');
                }
                string_builder_.append(ChessBoard[x][y]);
            }
        }
        return string_builder_.toString();
    }

    public int[][] snapshotBoard() {
        int[][] copy = new int[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                copy[x][y] = ChessBoard[x][y];
            }
        }
        return copy;
    }

}
