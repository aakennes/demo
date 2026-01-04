package demo.Chess;

/*
 * ChessPanel class to manage the chess panel of the GUI.
 * Used to display the chessboard and pieces.
 * Main window is divided into NorthPanel and ChessPanel.
*/

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import static demo.Apps.ColorDefine.PANEL_BG;

public class ChessPanel extends JPanel {
    private static final int sx_ = 60;
    private static final int sy_ = 60;
    private static final int GRID_SIZE = 42;
    private static final Color BACKGROUND_COLOR = PANEL_BG;
    private static final Color BOARD_COLOR = new Color(246, 232, 205);
    private static final Color GRID_COLOR = new Color(120, 96, 62);
    private static final Color BOARD_BORDER_COLOR = new Color(80, 60, 35);
    private static final BasicStroke GRID_STROKE = new BasicStroke(1.5f);
    private static final BasicStroke BORDER_STROKE = new BasicStroke(3f);

    public ChessPanel() {
        setPreferredSize(new Dimension(800, 700));
        setBackground(BACKGROUND_COLOR);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                int row = (y - sy_ + GRID_SIZE / 2) / GRID_SIZE;
                int col = (x - sx_ + GRID_SIZE / 2) / GRID_SIZE;
                ChessFrame.control_.putChess(col, row);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        paintBoardBackground(g2);
        paintGrid(g2);
        paintPieces(g2);
        g2.dispose();
    }

    private void paintBoardBackground(Graphics2D g2) {
        int width = (Model.WIDTH - 1) * GRID_SIZE;
        int height = (Model.HEIGHT - 1) * GRID_SIZE;
        int boardX = sx_ - GRID_SIZE / 2;
        int boardY = sy_ - GRID_SIZE / 2;
        g2.setColor(BOARD_COLOR);
        g2.fillRoundRect(boardX, boardY, width + GRID_SIZE, height + GRID_SIZE, 24, 24);
        g2.setColor(BOARD_BORDER_COLOR);
        g2.setStroke(BORDER_STROKE);
        g2.drawRoundRect(boardX, boardY, width + GRID_SIZE, height + GRID_SIZE, 24, 24);
    }

    private void paintGrid(Graphics2D g2) {
        g2.setColor(GRID_COLOR);
        g2.setStroke(GRID_STROKE);
        for (int i = 0; i < Model.WIDTH; i++) {
            g2.drawLine(sx_, sy_ + i * GRID_SIZE, sx_ + (Model.WIDTH - 1) * GRID_SIZE, sy_ + i * GRID_SIZE);
            g2.drawLine(sx_ + i * GRID_SIZE, sy_, sx_ + i * GRID_SIZE, sy_ + (Model.HEIGHT - 1) * GRID_SIZE);
        }
    }

    private void paintPieces(Graphics2D g2) {
        for (int i = 0; i < Model.WIDTH; i++) {
            for (int j = 0; j < Model.HEIGHT; j++) {
                int pieceColor = ChessFrame.model_.getPosition(i, j);
                if (pieceColor == Model.BLACK) {
                    g2.setColor(new Color(25, 25, 25));
                    g2.fillOval(sx_ + i * GRID_SIZE - GRID_SIZE / 2, sy_ + j * GRID_SIZE - GRID_SIZE / 2, GRID_SIZE, GRID_SIZE);
                    g2.setColor(new Color(255, 255, 255, 90));
                    g2.drawOval(sx_ + i * GRID_SIZE - GRID_SIZE / 2, sy_ + j * GRID_SIZE - GRID_SIZE / 2, GRID_SIZE, GRID_SIZE);
                } else if (pieceColor == Model.WHITE) {
                    g2.setColor(new Color(250, 250, 250));
                    g2.fillOval(sx_ + i * GRID_SIZE - GRID_SIZE / 2, sy_ + j * GRID_SIZE - GRID_SIZE / 2, GRID_SIZE, GRID_SIZE);
                    g2.setColor(new Color(0, 0, 0, 100));
                    g2.drawOval(sx_ + i * GRID_SIZE - GRID_SIZE / 2, sy_ + j * GRID_SIZE - GRID_SIZE / 2, GRID_SIZE, GRID_SIZE);
                }
            }
        }
    }
}
