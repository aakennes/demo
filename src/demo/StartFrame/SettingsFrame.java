package demo.StartFrame;

/*
 * SettingsFrame includes SettingsPanel in a JFrame.
 * Used to display the settings GUI.
*/

import javax.swing.JFrame;
import java.awt.BorderLayout;

public class SettingsFrame extends JFrame{
    public static SettingsPanel settings_panel_;

    public SettingsFrame() {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(800, 700);
        settings_panel_ = new SettingsPanel();
        this.setLayout(new BorderLayout());
        this.getContentPane().add(settings_panel_, BorderLayout.CENTER);
        this.setTitle("Settings");
        this.setResizable(false);
		this.setVisible(false);
    }

    public void showFrame() {
		this.setVisible(true);
	}

	public void hideFrame() {
		this.setVisible(false);
	}
}