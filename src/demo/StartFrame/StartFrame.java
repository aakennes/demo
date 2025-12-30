package demo.StartFrame;

/*
 * StartFrame includes StartPanel in a JFrame.
 * Used to display the start GUI.
*/

import javax.swing.JFrame;

import demo.Chess.ChessFrame;

import java.awt.BorderLayout;

public class StartFrame extends JFrame {
	public static StartPanel start_panel_;
    // public static JFrame start_frame_ = new JFrame("StartPanel");
    public StartFrame() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(800, 700);
		start_panel_ = new StartPanel();
		this.getContentPane().add(start_panel_, BorderLayout.CENTER);
		this.setTitle("Menu");
		this.setResizable(false);
		this.setVisible(true);
    }

    public void showFrame() {
		// TODO : change switch mode
		this.setVisible(true);
	}

	public void hideFrame() {
		this.setVisible(false);
	}
}
