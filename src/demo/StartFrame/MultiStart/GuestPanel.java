package demo.StartFrame.MultiStart;

import javax.swing.*;

import demo.Start;
import demo.Chess.ChessControl;
import demo.Chess.ChessFrame;
import demo.NetManage.Connection;
import demo.NetManage.Message;
import demo.NetManage.Net;
import demo.NetManage.Protocol;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static demo.Apps.ColorDefine.*;

public class GuestPanel extends JPanel {
	private static final int LABEL_WIDTH    = 150;
	private static final int FIELD_WIDTH    = 220;
	private static final int FONT_SIZE      = 20;
	private static final int HEIGHT         = 40;

	JPanel hostIpPanel   = new JPanel();
	JPanel portPanel     = new JPanel();
	JPanel passwordPanel = new JPanel();
	JPanel buttonPanel   = new JPanel();

	JLabel hostIpLabel   = new JLabel("Host IP:");
	JLabel portLabel     = new JLabel("Port:");
	JLabel passwordLabel = new JLabel("Password:");

	JTextField hostIpField   = new JTextField();
	JTextField portField     = new JTextField();
	JPasswordField passwordField = new JPasswordField();

	JButton connectButton    = new JButton("Connect");
	JButton cancelButton     = new JButton("Cancel");
	JLabel statusLabel       = new JLabel("Confirm to join a room.");

    String userName = Start.settings_frame_.settings_panel_.getUsername();

	private volatile Connection activeConnection;
	private volatile AtomicBoolean connecting = new AtomicBoolean(false);

	public GuestPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(Box.createVerticalGlue());
		this.setBackground(FRAME_BACK_COLOR);

		this.add(Box.createRigidArea(new Dimension(0, 20)));
		HostIpSetting();
		this.add(Box.createRigidArea(new Dimension(0, 40)));
		PortSetting();
		this.add(Box.createRigidArea(new Dimension(0, 40)));
		PasswordSetting();
		this.add(Box.createRigidArea(new Dimension(0, 40)));
		StatusLabelSetting();
		this.add(Box.createRigidArea(new Dimension(0, 40)));
		ButtonSetting();
		this.add(Box.createRigidArea(new Dimension(0, 20)));
		this.add(Box.createVerticalGlue());
		populateDefaults();
	}

	private void HostIpSetting() {
		hostIpPanel.setLayout(new BoxLayout(hostIpPanel, BoxLayout.X_AXIS));
		hostIpField.setMaximumSize(new Dimension(FIELD_WIDTH, HEIGHT));
		hostIpLabel.setMaximumSize(new Dimension(LABEL_WIDTH, HEIGHT));
		hostIpLabel.setFont(new Font("Consolas", Font.BOLD, FONT_SIZE));
		hostIpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		hostIpPanel.setBackground(FRAME_BACK_COLOR);
		hostIpPanel.add(hostIpLabel);
		hostIpPanel.add(Box.createRigidArea(new Dimension(20, 0)));
		hostIpPanel.add(hostIpField);
		this.add(hostIpPanel);
	}

	private void PortSetting() {
		portPanel.setLayout(new BoxLayout(portPanel, BoxLayout.X_AXIS));
		portField.setMaximumSize(new Dimension(FIELD_WIDTH, HEIGHT));
		portLabel.setMaximumSize(new Dimension(LABEL_WIDTH, HEIGHT));
		portLabel.setFont(new Font("Consolas", Font.BOLD, FONT_SIZE));
		portLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		portPanel.setBackground(FRAME_BACK_COLOR);
		portPanel.add(portLabel);
		portPanel.add(Box.createRigidArea(new Dimension(20, 0)));
		portPanel.add(portField);
		this.add(portPanel);
	}

	private void PasswordSetting() {
		passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.X_AXIS));
		passwordField.setMaximumSize(new Dimension(FIELD_WIDTH, HEIGHT));
		passwordLabel.setMaximumSize(new Dimension(LABEL_WIDTH, HEIGHT));
		passwordLabel.setFont(new Font("Consolas", Font.BOLD, FONT_SIZE));
		passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		passwordPanel.setBackground(FRAME_BACK_COLOR);
		passwordPanel.add(passwordLabel);
		passwordPanel.add(Box.createRigidArea(new Dimension(20, 0)));
		passwordPanel.add(passwordField);
		this.add(passwordPanel);
	}

	private void StatusLabelSetting() {
		statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		statusLabel.setFont(new Font("Consolas", Font.PLAIN, 16));
		this.add(statusLabel);
	}

	private void ButtonSetting() {
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setBackground(FRAME_BACK_COLOR);
		connectButton.setFont(new Font("Consolas", Font.BOLD, FONT_SIZE));
		cancelButton.setFont(new Font("Consolas", Font.BOLD, FONT_SIZE));
		connectButton.addActionListener(e -> handleJoinRoom());
		cancelButton.addActionListener(e -> handleCancel());
		buttonPanel.add(connectButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
		buttonPanel.add(cancelButton);
		this.add(buttonPanel);
	}

	private void populateDefaults() {
		hostIpField.setText("127.0.0.1");
		portField.setText("34567");
		passwordField.setText("");
	}

	private void handleJoinRoom() {
        System.out.println("handleJoin");
		if (connecting.get()) {
			return;
		}

		final String hostIp = hostIpField.getText().trim();
		final String portText = portField.getText().trim();
		final String pwd = new String(passwordField.getPassword());

		if (hostIp.isEmpty()) {
			showErrorDialog("Please enter host IP address.");
			return;
		}

		final int port;
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

		connecting.set(true);
		connectButton.setEnabled(false);
		updateStatus("Connecting to " + hostIp + ":" + port + "...");
		Start.net_.setMessageListener(new GuestMessageListener(userName, pwd));

		Start.net_.executor.execute(() -> {
			try {
				Connection conn = Start.net_.connect(hostIp, port);
				activeConnection = conn;
				Start.net_.send(conn, Protocol.buildJoin(userName, pwd));
			} catch (IOException ex) {
				handleConnectionFailure(ex.getMessage());
			}
		});
	}

	private void handleConnectionFailure(String message) {
		connecting.set(false);
		connectButton.setEnabled(true);
		Start.net_.setMessageListener(null);
		activeConnection = null;
		String displayMessage = (message == null || message.isEmpty()) ? "Network error" : message;
		updateStatus("Connection failed: " + displayMessage);
		showErrorDialog("Failed to connect: " + displayMessage);
	}

	private void handleCancel() {
		if (activeConnection != null) {
			activeConnection.closeConnect();
			activeConnection = null;
		}
		connecting.set(false);
		connectButton.setEnabled(true);
		Start.net_.setMessageListener(null);
		updateStatus("Confirm to join a room.");
		MultiStartMenu.guest_dialog_.setVisible(false);
	}

	private void updateStatus(String text) {
		SwingUtilities.invokeLater(() -> statusLabel.setText(text));
	}

	private void showErrorDialog(String message) {
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	private class GuestMessageListener implements Net.MessageListener {
		private final String username;
		private final String password;

		GuestMessageListener(String username, String password) {
			this.username = username;
			this.password = password;
		}

		@Override
		public void onConnected(Connection conn) {
            System.out.println("guest onConnected");
			updateStatus("Connected. Sending join info...");
		}

		@Override
		public void onMessage(Connection conn, String message) {
			Message parsed = Protocol.parse(message);
			switch (parsed.getType()) {
				case Protocol.T_JOIN_ACK:
					onJoinAccepted();
					break;
				case Protocol.T_REJECT:
					String reason = parsed.get(Protocol.K_REASON);
					onJoinRejected(reason == null ? "Rejected by host" : reason);
					break;
				case Protocol.T_SYNC:
					// TODO: apply sync data to board once multiplayer integration is ready
					break;
				default:
					break;
			}
		}

		private void onJoinAccepted() {
			connecting.set(false);
			connectButton.setEnabled(true);
			Start.net_.setMessageListener(null);
			updateStatus("Guest joined. Launching game...");
			SwingUtilities.invokeLater(() -> {
				MultiStartMenu.guest_dialog_.setVisible(false);
				launchChessGame(hostIpField.getText().trim());
			});
		}

		private void onJoinRejected(String reason) {
			if (activeConnection != null) {
				activeConnection.closeConnect();
				activeConnection = null;
			}
			connecting.set(false);
			connectButton.setEnabled(true);
			Start.net_.setMessageListener(null);
			String displayMessage = (reason == null || reason.isEmpty()) ? "Rejected by host" : reason;
			updateStatus("Join rejected: " + displayMessage);
			showErrorDialog(displayMessage);
		}

		@Override
		public void onDisconnected(Connection conn) {
			if (connecting.get()) {
				connectButton.setEnabled(true);
				connecting.set(false);
				Start.net_.setMessageListener(null);
				updateStatus("Disconnected by host.");
				showErrorDialog("Disconnected from host.");
			}
		}

		@Override
		public void onError(Connection conn, Exception ex) {
			handleConnectionFailure(ex == null ? "Network error" : ex.getMessage());
		}
	}

	private void launchChessGame(String roomIp) {
		String resolvedIp = roomIp;
        System.out.println("launchChessGame");
		ChessFrame.control_.setGameMode(ChessControl.DUO);
		// ChessFrame.control_.setRoomIP(resolvedIp);
		ChessFrame.control_.startGame();
		Start.chess_frame_.showFrame();
	}
}
