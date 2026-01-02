package demo;

import javax.swing.JFrame;

import demo.StartFrame.StartFrame;
import demo.StartFrame.HistoryFrame;
import demo.StartFrame.SettingsFrame;
import demo.Chess.ChessFrame;
import demo.NetManage.Net;

import java.awt.BorderLayout;

public class Start {
	public static SettingsFrame settings_frame_ = new SettingsFrame();
	public static HistoryFrame history_frame_ = new HistoryFrame();
	public static StartFrame start_frame_ = new StartFrame();
	public static ChessFrame chess_frame_ = new ChessFrame();
	public static Net net_ = new Net();
    public static void main(String[] args) {
		start_frame_.showFrame();
    }
}
