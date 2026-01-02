package demo.Chess;

/*
 * Control class to manage the game flow
*/

import javax.swing.*;

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
import demo.NetManage.Connection;
import demo.NetManage.Net;
import demo.NetManage.Protocol;
import demo.NetManage.Message;

import static demo.Apps.StringEscape.*;

public class ChessControl {
    // game mode 
    public static final int SINGLE = 0;
    public static final int DUO  = 1;

    public static final String SINGLE_STR = "Single";
    public static final String DUO_STR = "Duo";

    private static int gameMode = 0;
    private static String RoomIP = "";
    private static String OpponentName = "";

    // multiplayer session state
    private Net multiplayerNet;
    private Connection multiplayerConnection;
    private Net.MessageListener multiplayerListener;
    private boolean multiplayerActive = false;
    private boolean isHostSide = false;
    private int localColor = Model.BLACK;
    private int remoteColor = Model.WHITE;
    private String localPlayerId = "HOST";
    private String remotePlayerId = "GUEST";

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
        if(winner == Model.BLACK){
			JOptionPane.showMessageDialog(null, "black win");
		}else if(winner == Model.WHITE){
			JOptionPane.showMessageDialog(null, "white win");
		}
        writeHistoryRecord();
        notifyRemoteGameEnd();
    }
    public void exitGame() {
        // TODO: add exit game logic
        System.out.println("Exiting game");
        Start.chess_frame_.hideFrame();
        Start.start_frame_.showFrame();
        endMultiplayerSession(true);
        if(ChessFrame.model_.getGameState() != Model.BLACKWIN && ChessFrame.model_.getGameState() != Model.WHITEWIN){
            Path historyCsvPath = Paths.get("data", "history.csv");

            try (BufferedWriter bw = Files.newBufferedWriter(historyCsvPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE)) {
                // GameMode,RoomIP,OpponentName,MyColor,WinColor,Time
                
                bw.write(getGameModeStr() + "," + SettingsFrame.settings_panel_.getDefaultIP() + "," + escapeCsv("") + "," + "Black" + "," + escapeCsv("Unfinished")  + "," + Instant.now());
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
        if (isMultiplayerTurnBlocked()) {
            JOptionPane.showMessageDialog(null, "Waiting for opponent's move.");
            return;
        }
        System.out.println("Putting chess at: (" + x + ", " + y + ")");
        ChessFrame.model_.setPosition(x, y, ChessFrame.model_.getCurrentTurn());
        ChessFrame.chess_panel_.repaint();
        sendMoveIfNeeded(x, y);
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
        RoomIP = ip_ == null ? "" : ip_;
    }

    public String getOpponentName() {
        return OpponentName;
    }

    public void setOpponentName(String name) {
        OpponentName = name == null ? "" : name;
    }

    public synchronized void startMultiplayerSession(Net net, Connection connection, boolean hostSide, int assignedColor,
                                                     String localId, String remoteId) {
        endMultiplayerSession(false);
        if (net == null || connection == null) {
            return;
        }
        multiplayerNet = net;
        multiplayerConnection = connection;
        isHostSide = hostSide;
        localColor = assignedColor;
        remoteColor = (assignedColor == Model.BLACK) ? Model.WHITE : Model.BLACK;
        localPlayerId = (localId == null || localId.isEmpty()) ? (hostSide ? "HOST" : "GUEST") : localId;
        remotePlayerId = (remoteId == null || remoteId.isEmpty()) ? (hostSide ? "GUEST" : "HOST") : remoteId;
        multiplayerListener = new MultiplayerMessageListener();
        multiplayerNet.setMessageListener(multiplayerListener);
        multiplayerActive = true;
    }

    public synchronized void endMultiplayerSession(boolean notifyRemote) {
        if (!multiplayerActive) {
            return;
        }
        if (notifyRemote) {
            notifyRemoteLeave();
        }
        if (multiplayerNet != null) {
            multiplayerNet.setMessageListener(null);
        }
        if (multiplayerConnection != null) {
            multiplayerConnection.closeConnect();
        }
        multiplayerActive = false;
        multiplayerListener = null;
        multiplayerConnection = null;
        multiplayerNet = null;
    }

    private void notifyRemoteGameEnd() {
        if (multiplayerActive && multiplayerNet != null && multiplayerConnection != null) {
            multiplayerNet.send(multiplayerConnection, Protocol.buildLeave(localPlayerId));
        }
    }

    private void notifyRemoteLeave() {
        if (multiplayerNet != null && multiplayerConnection != null) {
            multiplayerNet.send(multiplayerConnection, Protocol.buildLeave(localPlayerId));
        }
    }

    private boolean isMultiplayerTurnBlocked() {
        if (!multiplayerActive || gameMode != DUO) {
            return false;
        }
        return ChessFrame.model_.getCurrentTurn() != localColor;
    }

    private void sendMoveIfNeeded(int x, int y) {
        if (!multiplayerActive || multiplayerNet == null || multiplayerConnection == null || gameMode != DUO) {
            return;
        }
        multiplayerNet.send(multiplayerConnection, Protocol.buildMove(x, y, localPlayerId));
        sendSyncIfNeeded();
    }

    private void sendSyncIfNeeded() {
        if (!multiplayerActive || multiplayerNet == null || multiplayerConnection == null || gameMode != DUO) {
            return;
        }
        String boardStr = ChessFrame.model_.serializeBoard();
        multiplayerNet.send(multiplayerConnection,
                Protocol.buildSync(boardStr,
                        ChessFrame.model_.getCurrentTurn(),
                        ChessFrame.model_.getGameState()));
    }

    private void handleIncomingMove(int x, int y) {
        if (!multiplayerActive || gameMode != DUO) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            if (ChessFrame.model_.getGameState() != Model.ONGOING && ChessFrame.model_.getGameState() != Model.DRAW) {
                return;
            }
            try {
                ChessFrame.model_.setPosition(x, y, remoteColor);
                ChessFrame.chess_panel_.repaint();
                if (ChessFrame.model_.getGameState() != Model.ONGOING && ChessFrame.model_.getGameState() != Model.DRAW) {
                    endGame(ChessFrame.model_.getGameState());
                }
            } catch (Exception ignored) {}
        });
    }

    private void handleSync(Message parsed) {
        if (!multiplayerActive || parsed == null) {
            return;
        }
        String boardStr = parsed.get(Protocol.K_BOARD);
        int currentTurnValue = parseInt(parsed.get(Protocol.K_CURRENT_TURN));
        int gameStateValue = parseInt(parsed.get(Protocol.K_GAME_STATE));
        int[][] snapshot = parseBoardSnapshot(boardStr);
        if (snapshot == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            ChessFrame.model_.applyState(snapshot, currentTurnValue, gameStateValue);
            ChessFrame.chess_panel_.repaint();
            if (ChessFrame.model_.getGameState() != Model.ONGOING && ChessFrame.model_.getGameState() != Model.DRAW) {
                endGame(ChessFrame.model_.getGameState());
            }
        });
    }

    private void handleOpponentLeave(String reason) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null,
                    reason == null ? "Opponent disconnected." : reason,
                    "Connection Closed",
                    JOptionPane.WARNING_MESSAGE);
            endMultiplayerSession(false);
        });
    }

    private void writeHistoryRecord() {
        Path historyCsvPath = Paths.get("data", "history.csv");

        try (BufferedWriter bw = Files.newBufferedWriter(historyCsvPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE)) {
            String winnerStr;
            if(ChessFrame.model_.getGameState() == Model.BLACKWIN){
                winnerStr = "Black";
            } else {
                winnerStr = "White";
            }
             String ipToWrite = (RoomIP == null || RoomIP.isEmpty()) ? SettingsFrame.settings_panel_.getDefaultIP() : RoomIP;
            String opponent = (OpponentName == null) ? "" : OpponentName;
            bw.write(getGameModeStr() + "," + ipToWrite + "," + escapeCsv(opponent) + "," +
                    (localColor == Model.BLACK ? "Black" : "White") + "," + winnerStr + "," + Instant.now());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class MultiplayerMessageListener implements Net.MessageListener {
        @Override
        public void onConnected(Connection conn) {
            
        }

        @Override
        public void onMessage(Connection conn, String message) {
            Message parsed = Protocol.parse(message);
            String type = parsed.getType();
            if (Protocol.T_MOVE.equals(type)) {
                int px = parseInt(parsed.get(Protocol.K_X));
                int py = parseInt(parsed.get(Protocol.K_Y));
                if (px >= 0 && py >= 0) {
                    handleIncomingMove(px, py);
                }
            } else if (Protocol.T_SYNC.equals(type)) {
                handleSync(parsed);
            } else if (Protocol.T_LEAVE.equals(type)) {
                handleOpponentLeave("Opponent left the game.");
            }
        }

        @Override
        public void onDisconnected(Connection conn) {
            if (multiplayerActive) {
                handleOpponentLeave("Connection lost.");
            }
        }

        @Override
        public void onError(Connection conn, Exception ex) {
            if (multiplayerActive) {
                handleOpponentLeave(ex == null ? "Network error" : ex.getMessage());
            }
        }
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void syncBoardState() {
        sendSyncIfNeeded();
    }

    private int[][] parseBoardSnapshot(String boardStr) {
        if (boardStr == null || boardStr.isEmpty()) {
            return null;
        }
        String[] tokens = boardStr.split(",");
        int expected = Model.WIDTH * Model.HEIGHT;
        if (tokens.length < expected) {
            return null;
        }
        int[][] snapshot = new int[Model.WIDTH][Model.HEIGHT];
        int idx = 0;
        for (int y = 0; y < Model.HEIGHT; y++) {
            for (int x = 0; x < Model.WIDTH; x++) {
                snapshot[x][y] = sanitizePiece(tokens[idx++]);
                if (idx >= tokens.length) {
                    break;
                }
            }
        }
        return snapshot;
    }

    private int sanitizePiece(String token) {
        int value = parseInt(token);
        if (value != Model.BLACK && value != Model.WHITE) {
            return Model.SPACE;
        }
        return value;
    }

}
