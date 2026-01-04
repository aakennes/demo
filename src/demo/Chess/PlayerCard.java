package demo.Chess;

import javax.swing.*;
import javax.swing.border.*;

import static demo.Apps.ColorDefine.*;

import java.awt.*;

import demo.Start;

public class PlayerCard extends JPanel {
    private static final Dimension AVATAR_SIZE = new Dimension(56, 56);
    private final JPanel avatar = new SquarePanel();
    private final JLabel nameLabel = new JLabel("Player");
    private final JLabel pieceLabel = new JLabel("Pieces: --");
    private final JLabel statusLabel = new JLabel("Waiting");

    PlayerCard() {
        this.setLayout(new BorderLayout(14, 0));
        this.setBorder(new EmptyBorder(12, 16, 12, 16));
        this.setOpaque(true);
        this.setMaximumSize(new Dimension(320, 110));
        this.setBackground(CARD_BG);

        avatar.setPreferredSize(AVATAR_SIZE);
        avatar.setMinimumSize(AVATAR_SIZE);
        avatar.setMaximumSize(AVATAR_SIZE);
        avatar.setOpaque(true);
        avatar.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 60), 2, false));

        nameLabel.setForeground(CHESS_TEXT_PRIMARY);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pieceLabel.setForeground(CHESS_TEXT_SECONDARY);
        pieceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(CHESS_TEXT_SECONDARY);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(nameLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(pieceLabel);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(statusLabel);

        add(avatar, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);
    }

    void updateCard(String username, String pieceDescription, Color swatchColor, boolean showStatus, boolean isNextTurn) {
        nameLabel.setText(username);
        pieceLabel.setText(pieceDescription);
        statusLabel.setVisible(showStatus);
        if (showStatus) {
            statusLabel.setText(isNextTurn ? "Next move" : "Waiting");
            statusLabel.setForeground(isNextTurn ? CHESS_ACCENT_COLOR : CHESS_TEXT_SECONDARY);
        }
        Color avatarColor = resolveAvatarColor(swatchColor);
        avatar.setBackground(avatarColor);
    }

    private Color resolveAvatarColor(Color fallback) {
        if (Start.settings_frame_ != null && Start.settings_frame_.settings_panel_ != null) {
            Color profileColor = Start.settings_frame_.settings_panel_.getProfileColorSwatch();
            if (profileColor != null) {
                return profileColor;
            }
        }
        return fallback == null ? CARD_BG : fallback;
    }

    private static final class SquarePanel extends JPanel {
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(AVATAR_SIZE);
        }

        @Override
        public Dimension getMinimumSize() {
            return new Dimension(AVATAR_SIZE);
        }

        @Override
        public Dimension getMaximumSize() {
            return new Dimension(AVATAR_SIZE);
        }

        @Override
        public void setBounds(int x, int y, int width, int height) {
            int size = Math.min(width, height);
            int yOffset = y + (height - size) / 2;
            super.setBounds(x, yOffset, size, size);
        }
    }
}