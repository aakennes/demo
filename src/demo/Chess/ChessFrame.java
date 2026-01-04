package demo.Chess;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import demo.Apps.IconSupport;

public class ChessFrame extends JFrame {
	private static final Color FRAME_BACK_COLOR = new Color(250, 250, 250);

    public static ChessControl control_;
    public static Model model_;

	public static NorthPanel north_panel_;
	public static ChessPanel chess_panel_;
	
    public ChessFrame() {
		
		control_ = new ChessControl();
		model_ = new Model();
		north_panel_ = new NorthPanel();
		chess_panel_ = new ChessPanel();

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().add(north_panel_, BorderLayout.NORTH);
		this.getContentPane().add(chess_panel_, BorderLayout.CENTER);
		this.setSize(720, 900);
		this.setVisible(false);
		this.setResizable(false);
		this.setBackground(FRAME_BACK_COLOR);
		IconSupport.apply(this);
    }

	public void showFrame() {
		this.setVisible(true);
	}

	public void hideFrame() {
		this.setVisible(false);
	}
}
