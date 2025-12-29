package demo;

/*
 * ChessPanel class to manage the chess panel of the GUI.
 * Used to display the chessboard and pieces.
 * Main window is divided into NorthPanel and ChessPanel.
*/

import java.awt.*;

import javax.swing.*;

public class ChessPanel extends JPanel {
    private static final int sx = 50; // starting x position
    private static final int sy = 50; // starting y position
    private static final int gridSize = 40; // size of each grid
    public ChessPanel() {
        
    }
    public void repaint() {
        // Paint chessboard logic
        System.out.println("Painting chessboard");

    }
}
