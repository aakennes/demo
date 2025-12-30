package demo.StartFrame;

/*
 * StartFrame includes StartPanel in a JFrame.
 * Used to display the start GUI.
*/

import javax.swing.JFrame;

import demo.Chess.ChessFrame;

import java.awt.BorderLayout;

public class StartFrame extends JFrame {
    public static StartPanel start_panel_ = new StartPanel();
    // public static JFrame start_frame_ = new JFrame("StartPanel");
    public StartFrame() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(800, 800);
		this.setVisible(true);
		this.getContentPane().add(start_panel_, BorderLayout.CENTER);
    }

    public void showFrame() {
		this.setVisible(true);
	}

	public void hideFrame() {
		this.setVisible(false);
	}
}
