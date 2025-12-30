package demo;

import javax.swing.JFrame;

import demo.StartFrame.StartFrame;
import demo.StartFrame.SettingsFrame;
import demo.Chess.ChessFrame;

import java.awt.BorderLayout;

public class Start {
	public static StartFrame start_frame_ = new StartFrame();
	public static ChessFrame chess_frame_ = new ChessFrame();
	public static SettingsFrame settings_frame_ = new SettingsFrame();
    public static void main(String[] args) {
		start_frame_.showFrame();
    }
}
