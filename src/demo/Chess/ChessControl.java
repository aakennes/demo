package demo.Chess;

/*
 * Control class to manage the game flow
*/

import javax.swing.*;
import java.io.*;
import java.nio.file.*;
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
    private volatile boolean restartRequestPending = false;

    // undo snapshot state
    private int[][] localUndoBoardSnapshot;
    private int localUndoTurnSnapshot = Model.BLACK;
    private int localUndoGameStateSnapshot = Model.ONGOING;
    private boolean localUndoAvailable = false;

    private int[][] remoteUndoBoardSnapshot;
    private int remoteUndoTurnSnapshot = Model.BLACK;
    private int remoteUndoGameStateSnapshot = Model.ONGOING;
    private boolean remoteUndoAvailable = false;

    private volatile boolean undoRequestPending = false;

    public ChessControl() {
        
    }
    public void restartGame() {
        // Restart Game logic : NorthPanel.RestartButton.ActionListener -> Model.resetGame 
        // -> ChessPanel.repaint
        System.out.println("Game restarted");
        ChessFrame.model_.resetGame();
        ChessFrame.chess_panel_.repaint();
        clearAllUndoSnapshots();
        refreshNorthPanel();
    }

    private void performRestartWithSync() {
        restartGame();
        if (multiplayerActive && gameMode == DUO) {
            sendSyncIfNeeded();
        }
    }
    public void requestRestart() {
        if (gameMode != DUO || !multiplayerActive || multiplayerNet == null || multiplayerConnection == null) {
            restartGame();
            return;
        }
        if (restartRequestPending) {
            JOptionPane.showMessageDialog(null, "Waiting for opponent to respond to the previous restart request.");
            return;
        }
        restartRequestPending = true;
        multiplayerNet.send(multiplayerConnection, Protocol.buildRestartRequest(localPlayerId));
        JOptionPane.showMessageDialog(null, "Restart request sent. Awaiting opponent confirmation.");
    }
    public void endGame(int winner) {
        // End Game logic : Model.setPosition -> Model.gameState = BLACKWIN/WHITEWIN
        // -> Control.endGame
        System.out.println("Game Ended");
        if(winner == Model.BLACK){
			JOptionPane.showMessageDialog(null, "black win");
		}else if(winner == Model.WHITE){
			JOptionPane.showMessageDialog(null, "white win");
		}
        writeHistoryRecord();
        // Keep the link alive so restart/exit buttons decide what happens next.
        refreshNorthPanel();
    }
    public void exitGame() {
        System.out.println("Exiting game");
        Start.chess_frame_.hideFrame();
        Start.start_frame_.showFrame();
        endMultiplayerSession(true);
        clearAllUndoSnapshots();
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
        boolean boardAcceptsMove = ChessFrame.model_.getGameState() == Model.ONGOING
                || ChessFrame.model_.getGameState() == Model.DRAW;
        if (boardAcceptsMove && ChessFrame.model_.getPosition(x, y) == Model.SPACE) {
            captureLocalUndoSnapshot();
        }
        ChessFrame.model_.setPosition(x, y, ChessFrame.model_.getCurrentTurn());
        ChessFrame.chess_panel_.repaint();
        sendMoveIfNeeded(x, y);
        if(ChessFrame.model_.getGameState() != Model.ONGOING && ChessFrame.model_.getGameState() != Model.DRAW) {
        	ChessFrame.control_.endGame(ChessFrame.model_.getGameState());
        }
        refreshNorthPanel();
    }

    public void undoLastMove() {
        if (!localUndoAvailable || localUndoBoardSnapshot == null || ChessFrame.model_ == null) {
            JOptionPane.showMessageDialog(null, "No move available to undo.");
            return;
        }

        boolean requiresApproval = gameMode == DUO
                && multiplayerActive
                && multiplayerNet != null
                && multiplayerConnection != null;

        if (requiresApproval) {
            if (undoRequestPending) {
                JOptionPane.showMessageDialog(null, "Waiting for opponent to respond to the previous undo request.");
                return;
            }
            undoRequestPending = true;
            multiplayerNet.send(multiplayerConnection, Protocol.buildUndoRequest(localPlayerId));
            JOptionPane.showMessageDialog(null, "Undo request sent. Awaiting opponent confirmation.");
            return;
        }

        applyLocalUndoSnapshot();
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
        if (mode_ == SINGLE) {
            localColor = Model.BLACK;
            remoteColor = Model.WHITE;
            localPlayerId = fetchConfiguredUserName();
            remotePlayerId = "";
            multiplayerActive = false;
            restartRequestPending = false;
        }
        clearAllUndoSnapshots();
        refreshNorthPanel();
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
        refreshNorthPanel();
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
        String tempName1 = hostSide ? "HOST" : "GUEST";
        String tempName2 = hostSide ? "GUEST" : "HOST";
        localPlayerId = (localId == null || localId.isEmpty()) ? tempName1 : localId;
        remotePlayerId = (remoteId == null || remoteId.isEmpty()) ? tempName2 : remoteId;
        multiplayerListener = new MultiplayerMessageListener();
        multiplayerNet.setMessageListener(multiplayerListener);
        multiplayerActive = true;
        restartRequestPending = false;
        clearAllUndoSnapshots();
        refreshNorthPanel();
    }

    public synchronized void endMultiplayerSession(boolean notifyRemote) {
        if (!multiplayerActive) {
            restartRequestPending = false;
            clearAllUndoSnapshots();
            refreshNorthPanel();
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
        restartRequestPending = false;
        clearAllUndoSnapshots();
        refreshNorthPanel();
    }

    private void notifyRemoteLeave() {
        if (multiplayerNet != null && multiplayerConnection != null) {
            multiplayerNet.send(multiplayerConnection, Protocol.buildLeave(localPlayerId));
        }
    }

    private void sendRestartAck(boolean accepted) {
        if (multiplayerNet == null || multiplayerConnection == null) {
            return;
        }
        multiplayerNet.send(multiplayerConnection, Protocol.buildRestartAck(localPlayerId, accepted));
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
            if (ChessFrame.model_.getPosition(x, y) == Model.SPACE) {
                captureRemoteUndoSnapshot();
            }
            try {
                ChessFrame.model_.setPosition(x, y, remoteColor);
                ChessFrame.chess_panel_.repaint();
                if (ChessFrame.model_.getGameState() != Model.ONGOING && ChessFrame.model_.getGameState() != Model.DRAW) {
                    endGame(ChessFrame.model_.getGameState());
                }
            } catch (Exception ignored) {}
            refreshNorthPanel();
        });
    }

    private void handleSync(Message parsed) {
        if (!multiplayerActive || parsed == null) {
            return;
        }
        String boardStr = parsed.get(Protocol.K_BOARD);
        int currentTurnValue = Integer.parseInt(parsed.get(Protocol.K_CURRENT_TURN));
        int gameStateValue = Integer.parseInt(parsed.get(Protocol.K_GAME_STATE));
        int[][] snapshot = parseBoardSnapshot(boardStr);
        if (snapshot == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            if (ChessFrame.model_ == null) {
                return;
            }
            boolean boardChanged = ChessFrame.model_ == null
                    || !ChessFrame.model_.serializeBoard().equals(boardStr)
                    || ChessFrame.model_.getCurrentTurn() != currentTurnValue
                    || ChessFrame.model_.getGameState() != gameStateValue;
            boolean wasFinished = ChessFrame.model_.getGameState() != Model.ONGOING
                    && ChessFrame.model_.getGameState() != Model.DRAW;
            if (boardChanged) {
                clearAllUndoSnapshots();
            }
            ChessFrame.model_.applyState(snapshot, currentTurnValue, gameStateValue);
            ChessFrame.chess_panel_.repaint();
            if (!wasFinished && ChessFrame.model_.getGameState() != Model.ONGOING
                    && ChessFrame.model_.getGameState() != Model.DRAW) {
                endGame(ChessFrame.model_.getGameState());
            }
            refreshNorthPanel();
        });
    }

    private void handleRestartRequest(Message parsed) {
        if (!multiplayerActive) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            String opponentDisplay = (remotePlayerId == null || remotePlayerId.isEmpty()) ? "Opponent" : remotePlayerId;
            int choice = JOptionPane.showConfirmDialog(null,
                    opponentDisplay + " requested to restart the game. Accept?",
                    "Restart Request",
                    JOptionPane.YES_NO_OPTION);
            boolean accepted = (choice == JOptionPane.YES_OPTION);
            if (accepted) {
                performRestartWithSync();
            }
            sendRestartAck(accepted);
        });
    }

    private void handleRestartAck(Message parsed) {
        boolean accepted = Boolean.parseBoolean(parsed.get(Protocol.K_ACCEPT));
        restartRequestPending = false;
        if (!accepted) {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,
                    "Opponent declined the restart request."));
            return;
        }
        SwingUtilities.invokeLater(this::performRestartWithSync);
    }

    private void handleUndoRequest(Message parsed) {
        if (!multiplayerActive || multiplayerNet == null || multiplayerConnection == null || parsed == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            if (undoRequestPending) {
                multiplayerNet.send(multiplayerConnection, Protocol.buildUndoAck(localPlayerId, false));
                JOptionPane.showMessageDialog(null,
                        "Declined opponent's undo request because your previous undo request is still pending.");
                return;
            }
            if (!remoteUndoAvailable || remoteUndoBoardSnapshot == null) {
                multiplayerNet.send(multiplayerConnection, Protocol.buildUndoAck(localPlayerId, false));
                JOptionPane.showMessageDialog(null,
                        "Unable to honor opponent's undo request because their last move is no longer available.");
                return;
            }
            String opponentDisplay = (remotePlayerId == null || remotePlayerId.isEmpty()) ? "Opponent" : remotePlayerId;
            int choice = JOptionPane.showConfirmDialog(null,
                    opponentDisplay + " requested to undo their last move. Accept?",
                    "Undo Request",
                    JOptionPane.YES_NO_OPTION);
            boolean accepted = (choice == JOptionPane.YES_OPTION);
            if (accepted) {
                applyRemoteUndoSnapshot();
            }
            multiplayerNet.send(multiplayerConnection, Protocol.buildUndoAck(localPlayerId, accepted));
        });
    }

    private void handleUndoAck(Message parsed) {
        boolean accepted = parsed != null && Boolean.parseBoolean(parsed.get(Protocol.K_ACCEPT));
        undoRequestPending = false;
        if (!multiplayerActive) {
            return;
        }
        if (!accepted) {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,
                    "Opponent declined the undo request."));
            return;
        }
        SwingUtilities.invokeLater(this::applyLocalUndoSnapshot);
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

    public void syncBoardState() {
        sendSyncIfNeeded();
    }
    private String fetchConfiguredUserName() {
        if (SettingsFrame.settings_panel_ != null) {
            String configured = SettingsFrame.settings_panel_.getUsername();
            if (configured != null && !configured.trim().isEmpty()) {
                return configured.trim();
            }
        }
        return "Player";
    }

    private String normalizeName(String value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? fallback : trimmed;
    }

    private void refreshNorthPanel() {
        Runnable task = () -> {
            if (ChessFrame.north_panel_ != null) {
                ChessFrame.north_panel_.refreshPlayerInfo();
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }

    private void captureLocalUndoSnapshot() {
        if (ChessFrame.model_ == null) {
            return;
        }
        localUndoBoardSnapshot = ChessFrame.model_.snapshotBoard();
        localUndoTurnSnapshot = ChessFrame.model_.getCurrentTurn();
        localUndoGameStateSnapshot = ChessFrame.model_.getGameState();
        localUndoAvailable = true;
    }

    private void captureRemoteUndoSnapshot() {
        if (ChessFrame.model_ == null) {
            return;
        }
        remoteUndoBoardSnapshot = ChessFrame.model_.snapshotBoard();
        remoteUndoTurnSnapshot = ChessFrame.model_.getCurrentTurn();
        remoteUndoGameStateSnapshot = ChessFrame.model_.getGameState();
        remoteUndoAvailable = true;
    }

    private void applyLocalUndoSnapshot() {
        Runnable task = () -> {
            if (!localUndoAvailable || localUndoBoardSnapshot == null || ChessFrame.model_ == null) {
                return;
            }
            ChessFrame.model_.applyState(localUndoBoardSnapshot, localUndoTurnSnapshot, localUndoGameStateSnapshot);
            ChessFrame.chess_panel_.repaint();
            clearAllUndoSnapshots();
            refreshNorthPanel();
        };
        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }

    private void applyRemoteUndoSnapshot() {
        Runnable task = () -> {
            if (!remoteUndoAvailable || remoteUndoBoardSnapshot == null || ChessFrame.model_ == null) {
                return;
            }
            ChessFrame.model_.applyState(remoteUndoBoardSnapshot, remoteUndoTurnSnapshot, remoteUndoGameStateSnapshot);
            ChessFrame.chess_panel_.repaint();
            clearAllUndoSnapshots();
            refreshNorthPanel();
        };
        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }

    private void clearAllUndoSnapshots() {
        localUndoBoardSnapshot = null;
        localUndoAvailable = false;
        localUndoTurnSnapshot = Model.BLACK;
        localUndoGameStateSnapshot = Model.ONGOING;

        remoteUndoBoardSnapshot = null;
        remoteUndoAvailable = false;
        remoteUndoTurnSnapshot = Model.BLACK;
        remoteUndoGameStateSnapshot = Model.ONGOING;

        undoRequestPending = false;
    }

    public String getLocalPlayerName() {
        return normalizeName(localPlayerId, fetchConfiguredUserName());
    }

    public String getRemotePlayerName() {
        if (gameMode != DUO) {
            return "";
        }
        String fallback = multiplayerActive ? "Opponent" : "Waiting...";
        return normalizeName(remotePlayerId, fallback);
    }

    public int getLocalPieceColor() {
        return localColor;
    }

    public int getRemotePieceColor() {
        return remoteColor;
    }

    public boolean shouldShowRemoteInfo() {
        return gameMode == DUO;
    }

    public boolean isLocalTurn() {
        if (ChessFrame.model_ == null) {
            return false;
        }
        return ChessFrame.model_.getCurrentTurn() == localColor;
    }

    public boolean isRemoteTurn() {
        if (ChessFrame.model_ == null) {
            return false;
        }
        return ChessFrame.model_.getCurrentTurn() == remoteColor;
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
        int value = Integer.parseInt(token);
        if (value != Model.BLACK && value != Model.WHITE) {
            return Model.SPACE;
        }
        return value;
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
                int px = Integer.parseInt(parsed.get(Protocol.K_X));
                int py = Integer.parseInt(parsed.get(Protocol.K_Y));
                if (px >= 0 && py >= 0) {
                    handleIncomingMove(px, py);
                }
            } else if (Protocol.T_SYNC.equals(type)) {
                handleSync(parsed);
            } else if (Protocol.T_RESTART_REQ.equals(type)) {
                handleRestartRequest(parsed);
            } else if (Protocol.T_RESTART_ACK.equals(type)) {
                handleRestartAck(parsed);
            } else if (Protocol.T_UNDO_REQ.equals(type)) {
                handleUndoRequest(parsed);
            } else if (Protocol.T_UNDO_ACK.equals(type)) {
                handleUndoAck(parsed);
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

}
