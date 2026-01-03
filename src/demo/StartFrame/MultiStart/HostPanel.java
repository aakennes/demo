package demo.StartFrame.MultiStart;

import javax.swing.*;

import demo.Start;
import demo.Chess.ChessControl;
import demo.Chess.ChessFrame;
import demo.NetManage.Connection;
import demo.NetManage.Message;
import demo.NetManage.Net;
import demo.NetManage.Protocol;
import demo.Chess.Model;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import static demo.Apps.ColorDefine.*;

public class HostPanel extends JPanel{
    private static final int LABEL_WIDTH    = 150;
    private static final int FIELD_WIDTH    = 220;
    private static final int BUTTON_WIDTH   = 80;
    private static final int FONT_SIZE      = 20;
    private static final int HEIGHT         = 40;

    JPanel RoomIPPanel    = new JPanel();
    JPanel RoomPortPanel  = new JPanel();
    JPanel PassWordPanel  = new JPanel();

    JPanel ButtonPanel    = new JPanel();

    JLabel RoomIPLabel      = new JLabel("IP:");
    JLabel RoomPortLabel    = new JLabel("Port:");
    JLabel PassWordLabel    = new JLabel("Password:");
    
    JTextField IPTextField      = new JTextField();
    JTextField PortTextField    = new JTextField();
    JPasswordField PasswordField = new JPasswordField();

    JButton ComfirmButton = new JButton("Comfirm");
    JButton CancelButton  = new JButton("Cancel");
    JLabel StatusLabel    = new JLabel("Comfirm to host a room.");

    private volatile Connection activeConnection;
    private volatile boolean hosting = false;
    private volatile String roomPassword = "";

    public HostPanel(){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(Box.createVerticalGlue());
        this.setBackground(FRAME_BACK_COLOR);

        this.add(Box.createRigidArea(new Dimension(0, 20)));
        RoomIPSetting();
        this.add(Box.createRigidArea(new Dimension(0, 40)));
        RoomPortSetting();
        this.add(Box.createRigidArea(new Dimension(0, 40)));
        PassWordSetting();
        this.add(Box.createRigidArea(new Dimension(0, 40))); 
        StatusLabelSetting();
        this.add(Box.createRigidArea(new Dimension(0, 40)));
        ButtonSetting();
        this.add(Box.createRigidArea(new Dimension(0, 20)));
        this.add(Box.createVerticalGlue());

        SettingDefault();
    }

    private void SettingDefault() {
        try {
            String hostIp = InetAddress.getLocalHost().getHostAddress();
            IPTextField.setText(hostIp);
        } catch (UnknownHostException e) {
            IPTextField.setText("127.0.0.1");
        }
        PortTextField.setText("34567");
    }

    private void RoomIPSetting() {
        RoomIPPanel.setLayout(new BoxLayout(RoomIPPanel, BoxLayout.X_AXIS));
        IPTextField.setMaximumSize(new Dimension(FIELD_WIDTH, HEIGHT));

        RoomIPLabel.setMaximumSize(new Dimension(LABEL_WIDTH, HEIGHT));
        RoomIPLabel.setFont(new Font("Consolas", Font.BOLD, FONT_SIZE));
        RoomIPLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        RoomIPPanel.setBackground(FRAME_BACK_COLOR);
        RoomIPPanel.add(RoomIPLabel);
        RoomIPPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        RoomIPPanel.add(IPTextField);
        this.add(RoomIPPanel);
    }

    private void RoomPortSetting() {
        RoomPortPanel.setLayout(new BoxLayout(RoomPortPanel, BoxLayout.X_AXIS));
        PortTextField.setMaximumSize(new Dimension(FIELD_WIDTH, HEIGHT));

        RoomPortLabel.setMaximumSize(new Dimension(LABEL_WIDTH, HEIGHT));
        RoomPortLabel.setFont(new Font("Consolas", Font.BOLD, FONT_SIZE));
        RoomPortLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        RoomPortPanel.setBackground(FRAME_BACK_COLOR);
        RoomPortPanel.add(RoomPortLabel);
        RoomPortPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        RoomPortPanel.add(PortTextField);
        this.add(RoomPortPanel);
    }

    private void PassWordSetting() {
        PassWordPanel.setLayout(new BoxLayout(PassWordPanel, BoxLayout.X_AXIS));
        PasswordField.setMaximumSize(new Dimension(FIELD_WIDTH, HEIGHT));

        PassWordLabel.setMaximumSize(new Dimension(LABEL_WIDTH, HEIGHT));
        PassWordLabel.setFont(new Font("Consolas", Font.BOLD, FONT_SIZE));
        PassWordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        PassWordPanel.setBackground(FRAME_BACK_COLOR);
        PassWordPanel.add(PassWordLabel);
        PassWordPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        PassWordPanel.add(PasswordField);
        this.add(PassWordPanel);
    }

    private void StatusLabelSetting(){
        StatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        StatusLabel.setFont(new Font("Consolas", Font.PLAIN, 16));
        this.add(StatusLabel);
    }

    private void ButtonSetting(){
        ButtonPanel.setLayout(new BoxLayout(ButtonPanel, BoxLayout.X_AXIS));
        // ComfirmButton.setMaximumSize(new Dimension(BUTTON_WIDTH, HEIGHT));
        // CancelButton.setMaximumSize(new Dimension(BUTTON_WIDTH, HEIGHT));
        ComfirmButton.setFont(new Font("Consolas", Font.BOLD, FONT_SIZE));
        CancelButton.setFont(new Font("Consolas", Font.BOLD, FONT_SIZE));
        // Action Listening

        ComfirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Comfirm button clicked");
                handleCreateRoom();
            }
        });

        CancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Cancel button clicked");
                if (hosting) {
                    stopHosting();
                }
                MultiStartMenu.host_dialog_.setVisible(false);
            }
        });

        ButtonPanel.setBackground(FRAME_BACK_COLOR);
        ButtonPanel.add(ComfirmButton);
        ButtonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        ButtonPanel.add(CancelButton);
        this.add(ButtonPanel);
    }

    

    private void handleCreateRoom() {
        if (hosting) {
            return;
        }

        String portText = PortTextField.getText().trim();
        int port;
        if (portText.isEmpty()) {
            showErrorDialog("Please enter a port number.");
            return;
        }
        try {
            port = Integer.parseInt(portText);
        } catch (NumberFormatException ex) {
            showErrorDialog("Invalid port number.");
            return;
        }
        if (port < 1024 || port > 65535) {
            showErrorDialog("Port must be between 1024 and 65535.");
            return;
        }

        roomPassword = new String(PasswordField.getPassword());
        Start.net_.setMessageListener(new HostMessageListener());
        try {
            Start.net_.startServer(port);
            hosting = true;
            ComfirmButton.setEnabled(false);
            updateStatus("Waiting for opponent to connect...");
        } catch (IOException ex) {
            Start.net_.setMessageListener(null);
            showErrorDialog("Failed to start server: " + ex.getMessage());
        }
    }

    private void stopHosting() {
        Start.net_.stopServer();
        hosting = false;
        activeConnection = null;
        ComfirmButton.setEnabled(true);
        updateStatus("Server stopped.");
        Start.net_.setMessageListener(null);
    }

    private void updateStatus(String text) {
        SwingUtilities.invokeLater(() -> StatusLabel.setText(text));
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void handleJoin(Connection conn, Message msg) {
        String incomingPassword = msg.get(Protocol.K_PASSWORD);
        System.out.println("handleJoin");
        if (!Objects.equals(roomPassword, incomingPassword)) {
            Start.net_.send(conn, Protocol.buildReject("WRONG_PASSWORD"));
            conn.closeConnect();
            updateStatus("Rejected opponent: wrong password.");
            return;
        }

        String hostName = Start.settings_frame_.settings_panel_.getUsername();
        Start.net_.send(conn, Protocol.buildJoinAck(hostName, "WHITE"));
        updateStatus("Guest joined! Launching game...");
        SwingUtilities.invokeLater(() -> {
            MultiStartMenu.host_dialog_.setVisible(false);
            launchChessGame(conn, msg);
        });
    }

    private void launchChessGame(Connection conn, Message joinMessage) {
        if (conn == null) {
            showErrorDialog("Connection unavailable. Cannot start game.");
            return;
        }
        String resolvedIp = IPTextField.getText().trim();
        if (resolvedIp == null || resolvedIp.isEmpty()) {
            resolvedIp = Start.settings_frame_.settings_panel_.getDefaultIP();
        }
        String opponentName = joinMessage == null ? "" : joinMessage.get(Protocol.K_NAME);
        if (opponentName == null || opponentName.isEmpty()) {
            opponentName = "Guest";
        }
        String hostName = Start.settings_frame_.settings_panel_.getUsername();
        ChessFrame.control_.setGameMode(ChessControl.DUO);
        ChessFrame.control_.setRoomIP(resolvedIp);
        ChessFrame.control_.setOpponentName(opponentName);
        ChessFrame.control_.startMultiplayerSession(
                Start.net_,
                conn,
                true,
                Model.BLACK,
                hostName,
                opponentName);
        ChessFrame.control_.restartGame();
        ChessFrame.control_.syncBoardState();
        Start.chess_frame_.showFrame();
    }

    private class HostMessageListener implements Net.MessageListener {
        @Override
        public void onConnected(Connection conn) {
            activeConnection = conn;
            updateStatus("Opponent connected from " + conn.getRemoteAddress() + ". Waiting ...");
        }

        @Override
        public void onMessage(Connection conn, String message) {
            Message parsed = Protocol.parse(message);
            String type = parsed.getType();
            if (Protocol.T_JOIN.equals(type)) {
                handleJoin(conn, parsed);
            } else if (Protocol.T_LEAVE.equals(type)) {
                updateStatus("Opponent left the room.");
                conn.closeConnect();
            }
        }

        @Override
        public void onDisconnected(Connection conn) {
            if (!hosting) return;
            if (conn == activeConnection) {
                activeConnection = null;
            }
            updateStatus("Opponent disconnected.");
        }

        @Override
        public void onError(Connection conn, Exception ex) {
            if (!hosting) return;
            updateStatus("Network error: " + (ex == null ? "unknown" : ex.getMessage()));
        }
    }
}
