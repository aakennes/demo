package demo;

import javax.swing.JFrame;
import java.awt.BorderLayout;

public class Start {
    public static void main(String[] args) {
        JFrame ChessFrame = new JFrame("ChessGame");
		ChessFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ChessFrame.getContentPane().add(Vars.north_panel_, BorderLayout.NORTH);
		ChessFrame.getContentPane().add(Vars.chess_panel_, BorderLayout.CENTER);
		ChessFrame.setSize(800, 1000);
		ChessFrame.setVisible(true);
    }
}
