package demo.Chess;

/*
 * Control class to manage the game flow
*/

import javax.swing.*;

import demo.Start;
import demo.Chess.ChessFrame;

public class ChessControl {
    public static final int SINGLE = 1;
	public static final int NET = 2;

    public ChessControl() {
        
    }
    public void startGame() {
        // Start Game logic : NorthPanel.StartButton.ActionListener -> Model.resetGame 
        // -> ChessPanel.repaint
        System.out.println("Game started");
        ChessFrame.model_.resetGame();
        ChessFrame.chess_panel_.repaint();
    }
    public void restartGame() {
        // Restart Game logic : NorthPanel.RestartButton.ActionListener -> Model.resetGame 
        // -> ChessPanel.repaint
        System.out.println("Game restarted");
        ChessFrame.model_.resetGame();
        ChessFrame.chess_panel_.repaint();
    }
    public void endGame(int winner) {
        // TODO: need more end game logic to show winner
        // End Game logic : Model.setPosition -> Model.gameState = BLACKWIN/WHITEWIN
        // -> Control.endGame
        System.out.println("Game Ended");
        if(winner==Model.BLACK){
			JOptionPane.showMessageDialog(null, "black win");
		}else if(winner==Model.WHITE){
			JOptionPane.showMessageDialog(null, "white win");
		}
    }
    public void exitGame() {
        // TODO: add exit game logic
        System.out.println("Exiting game");
        Start.chess_frame_.hideFrame();
        Start.start_frame_.showFrame();
    }
    public void putChess(int x, int y) {
        // Put chess logic : ChessPanel.mousePressed -> Control.putChess -> Model.setPosition
        if(ChessFrame.model_.getGameState() != Model.ONGOING && ChessFrame.model_.getGameState() != Model.DRAW){
            return;
        }
        System.out.println("Putting chess at: (" + x + ", " + y + ")");
        ChessFrame.model_.setPosition(x, y, ChessFrame.model_.getCurrentTurn());
        ChessFrame.chess_panel_.repaint();
        if(ChessFrame.model_.getGameState() != Model.ONGOING && ChessFrame.model_.getGameState() != Model.DRAW) {
        	ChessFrame.control_.endGame(ChessFrame.model_.getGameState());
        }
    }
}
