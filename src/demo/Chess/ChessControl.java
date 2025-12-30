package demo.Chess;

/*
 * Control class to manage the game flow
*/

import javax.swing.*;
import java.util.*;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

import demo.Start;
import demo.Chess.ChessFrame;
import demo.StartFrame.SettingsFrame;

import static demo.Apps.ColorDefine.BLACK;
import static demo.Apps.StringEscape.*;

public class ChessControl {
    // game mode 
    public static final int SINGLE = 0;
    public static final int DUO  = 1;

    public static final String SINGLE_STR = "Single";
    public static final String DUO_STR = "Duo";

    private static int gameMode = 0;
    private static String RoomIP;

    public ChessControl() {
        // RoomIP = "127.0.0.1";
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
        if(winner == Model.BLACK){
			JOptionPane.showMessageDialog(null, "black win");
		}else if(winner == Model.WHITE){
			JOptionPane.showMessageDialog(null, "white win");
		}
        Path historyCsvPath = Paths.get("data", "history.csv");
        
        try (BufferedWriter bw = Files.newBufferedWriter(historyCsvPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE)) {
            // GameMode,RoomIP,OpponentName,MyColor,WinColor,Time
            String winnerStr;
            if(ChessFrame.model_.getGameState() == Model.BLACKWIN){
                winnerStr = "Black";
            } else {
                winnerStr = "White";
            }
            bw.write(getGameModeStr() + "," + SettingsFrame.settings_panel_.getDefaultIP() + "," + escapeCsv("") + "," + "Black" + "," + winnerStr + "," + Instant.now());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void exitGame() {
        // TODO: add exit game logic
        System.out.println("Exiting game");
        Start.chess_frame_.hideFrame();
        Start.start_frame_.showFrame();
        if(ChessFrame.model_.getGameState() != Model.BLACKWIN && ChessFrame.model_.getGameState() != Model.WHITEWIN){
            Path historyCsvPath = Paths.get("data", "history.csv");

            try (BufferedWriter bw = Files.newBufferedWriter(historyCsvPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE)) {
                // GameMode,RoomIP,OpponentName,MyColor,WinColor,Time
                
                bw.write(getGameModeStr() + "," + SettingsFrame.settings_panel_.getDefaultIP() + "," + escapeCsv("") + "," + "Black" + "," + escapeCsv("")  + "," + Instant.now());
                bw.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
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

    public int getGameMode(){
        return gameMode;
    }

    public String getGameModeStr(){
        if(gameMode == SINGLE) return SINGLE_STR;
        return DUO_STR;
    }

    public void setGameMode(int mode_){
        gameMode = mode_;
    }

    public String getRoomIP(){
        return RoomIP;
    }

    public void setRoomIP(String ip_){
        RoomIP = ip_;
    }

}
