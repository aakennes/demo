package demo.Chess;

/*
 * NorthPanel class to manage the north panel of the GUI.
 * Main window is divided into NorthPanel and ChessPanel.
*/

import javax.swing.*;
import javax.swing.border.*;

import static demo.Apps.ColorDefine.*;

import java.awt.*;

public class NorthPanel extends JPanel {
    private final JButton exitButton = new JButton("Exit");
    private final JButton restartButton = new JButton("Restart");
    private final JButton undoButton = new JButton("Undo");
    private final JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
    private final JPanel infoPanel = new JPanel();
    private final PlayerCard localCard = new PlayerCard();
    private final PlayerCard opponentCard = new PlayerCard();
    private final Component infoSpacer = Box.createHorizontalStrut(18);

    public NorthPanel() {
        this.setLayout(new BorderLayout());
        this.setBackground(PANEL_BG);
        this.setBorder(new EmptyBorder(12, 18, 18, 18));
        configureButtons();

        configureInfoPanel();
        refreshPlayerInfo();
    }

    private void configureButtons() {
        buttonRow.setOpaque(false);
        styleButton(undoButton, CHESS_BUTTON_BG);
        styleButton(restartButton, CHESS_BUTTON_BG);
        styleButton(exitButton, CHESS_BUTTON_EXIT_BG);
        undoButton.addActionListener(e -> {
            System.out.println("Undo button clicked");
            ChessFrame.control_.undoLastMove();
        });
        restartButton.addActionListener(e -> {
            System.out.println("Restart button clicked");
            ChessFrame.control_.requestRestart();
        });
        exitButton.addActionListener(e -> {
            System.out.println("Exit button clicked");
            ChessFrame.control_.exitGame();
        });
        buttonRow.add(undoButton);
        buttonRow.add(restartButton);
        buttonRow.add(exitButton);
        add(buttonRow, BorderLayout.NORTH);
    }

    private void styleButton(JButton button, Color color) {
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
    }

    private void configureInfoPanel() {
        infoPanel.setOpaque(true);
        infoPanel.setBackground(PANEL_BG);
        infoPanel.setBorder(new EmptyBorder(12, 0, 0, 0));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
        infoPanel.add(localCard);
        infoPanel.add(infoSpacer);
        infoPanel.add(opponentCard);
        add(infoPanel, BorderLayout.CENTER);
    }

    public void refreshPlayerInfo() {
        ChessControl control = ChessFrame.control_;
        if (control == null) {
            return;
        }
        boolean showRemote = control.shouldShowRemoteInfo();
        localCard.updateCard(
                control.getLocalPlayerName(),
                describePiece(control.getLocalPieceColor(), !showRemote),
                pieceSwatch(control.getLocalPieceColor()),
                showRemote,
                control.isLocalTurn());

        infoSpacer.setVisible(showRemote);
        opponentCard.setVisible(showRemote);
        if (showRemote) {
            opponentCard.updateCard(
                    control.getRemotePlayerName(),
                    describePiece(control.getRemotePieceColor(), false),
                    pieceSwatch(control.getRemotePieceColor()),
                    true,
                    control.isRemoteTurn());
        }
        revalidate();
        repaint();
    }

    private String describePiece(int color, boolean shortLabel) {
        String colorName;
        if (color == Model.BLACK) {
            colorName = "Black";
        } else if (color == Model.WHITE) {
            colorName = "White";
        } else {
            colorName = "TBD";
        }
        return shortLabel ? colorName : "Pieces: " + colorName;
    }

    private Color pieceSwatch(int color) {
        if (color == Model.BLACK) {
            return BLACK_COLOR;
        }
        if (color == Model.WHITE) {
            return WHITE_COLOR;
        }
        return new Color(90, 90, 90);
    }
}
