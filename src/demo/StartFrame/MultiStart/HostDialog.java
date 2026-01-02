package demo.StartFrame.MultiStart;

/*
 * Frame to display Host panel.
*/

import javax.swing.*;
import java.awt.*;


public class HostDialog extends JDialog{
    public static HostPanel host_panel_;

    public HostDialog(JFrame parentFrame, String titleName, boolean flag){
		super(parentFrame, titleName, flag);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(500, 400);
		host_panel_ = new HostPanel();
		this.setLayout(new BorderLayout());
		this.getContentPane().add(host_panel_, BorderLayout.CENTER);
		this.setResizable(false);
		this.setVisible(false);
    }
}
