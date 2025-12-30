package demo.Chess;

/*
 * ChessPanel class to manage the chess panel of the GUI.
 * Used to display the chessboard and pieces.
 * Main window is divided into NorthPanel and ChessPanel.
*/

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


public class ChessPanel extends JPanel {
    private static final int sx = 50; // starting x position
    private static final int sy = 50; // starting y position
    private static final int gridSize = 40; // size of each grid
    public ChessPanel() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                System.out.println("Mouse pressed at: (" + x + ", " + y + ")");
                int row = (y - sy + gridSize / 2) / gridSize;
                int col = (x - sx + gridSize / 2) / gridSize;
                ChessFrame.control_.putChess(col, row);
            }
        });
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw chessboard logic
        System.out.println("Drawing chessboard");
        // Draw grid
        this.paintGrid(g);
        // Draw pieces
        this.paintPieces(g);
    }

    private void paintGrid(Graphics g) {
        // Paint grid logic
        System.out.println("Painting grid");
        for (int i = 0; i < Model.WIDTH; i++) {
            g.drawLine(sx, sy + i * gridSize, sx + (Model.WIDTH - 1) * gridSize, sy + i * gridSize);
            g.drawLine(sx + i * gridSize, sy, sx + i * gridSize, sy + (Model.HEIGHT - 1) * gridSize);
        }
    }

    private void paintPieces(Graphics g) {
        // Paint pieces logic
        System.out.println("Painting pieces");
        for (int i = 0; i < Model.WIDTH; i++) {
            for (int j = 0; j < Model.HEIGHT; j++) {
                int pieceColor = ChessFrame.model_.getPosition(i, j);
                if (pieceColor == Model.BLACK) {
                    g.setColor(Color.BLACK);
                    g.fillOval(sx + i * gridSize - gridSize / 2, sy + j * gridSize - gridSize / 2, gridSize, gridSize);
                } else if (pieceColor == Model.WHITE) {
                    g.setColor(Color.WHITE);
                    g.fillOval(sx + i * gridSize - gridSize / 2, sy + j * gridSize - gridSize / 2, gridSize, gridSize);
                }
            }
        }
    }

    // @Override
    // public void repaint() {
    //     // Use the default repaint to schedule painting on the EDT
    //     super.repaint();
    // }
}
