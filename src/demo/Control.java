package demo;

/*
 * Control class to manage the game flow
*/

import javax.swing.*;

public class Control {
    public void startGame() {
        // Start Game logic : NorthPanel.StartButton.ActionListener -> Model.resetGame 
        // -> ChessPanel.repaint
        System.out.println("Game started");
        Vars.model_.resetGame();
        Vars.chess_panel_.repaint();
    }
    public void restartGame() {
        // Restart Game logic : NorthPanel.RestartButton.ActionListener -> Model.resetGame 
        // -> ChessPanel.repaint
        System.out.println("Game restarted");
        Vars.model_.resetGame();
        Vars.chess_panel_.repaint();
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
        
    }
}
