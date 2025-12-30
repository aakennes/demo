package demo.Chess;

import javax.swing.JFrame;
import java.awt.BorderLayout;

public class ChessFrame extends JFrame {
    public static ChessControl control_ = new ChessControl();
    public static Model model_ = new Model();

	public static NorthPanel north_panel_ = new NorthPanel();
	public static ChessPanel chess_panel_ = new ChessPanel();
	
    public ChessFrame() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().add(north_panel_, BorderLayout.NORTH);
		this.getContentPane().add(chess_panel_, BorderLayout.CENTER);
		this.setSize(800, 800);
		this.setVisible(false);
    }

	public void showFrame() {
		this.setVisible(true);
	}

	public void hideFrame() {
		this.setVisible(false);
	}
}
