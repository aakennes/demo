package demo.StartFrame;

/*
 * Frame to display history panel.
*/

import javax.swing.JFrame;
import java.awt.BorderLayout;

import demo.Apps.IconSupport;

public class HistoryFrame extends JFrame {
	public static HistoryPanel history_panel_;

	public HistoryFrame() {
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(800, 700);
		history_panel_ = new HistoryPanel();
		this.setLayout(new BorderLayout());
		this.getContentPane().add(history_panel_, BorderLayout.CENTER);
		this.setTitle("History");
		this.setResizable(false);
		IconSupport.apply(this);
		this.setVisible(false);
	}

	public void showFrame() {
		if (history_panel_ != null) {
            history_panel_.loadData();
        }
		this.setVisible(true);
	}

	public void hideFrame() {
		this.setVisible(false);
	}
}
