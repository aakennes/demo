package demo.StartFrame.MultiStart;

import javax.swing.*;
import java.awt.*;

public class GuestFrame extends JDialog {
	public static GuestPanel guest_panel_;

	public GuestFrame(JFrame parentFrame, String titleName, boolean modal) {
		super(parentFrame, titleName, modal);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(500, 450);
		guest_panel_ = new GuestPanel();
		this.setLayout(new BorderLayout());
		this.getContentPane().add(guest_panel_, BorderLayout.CENTER);
		this.setResizable(false);
		this.setLocationRelativeTo(parentFrame);
		this.setVisible(false);
	}
}
