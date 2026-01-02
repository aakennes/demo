package demo.StartFrame.MultiStart;

/*
 * Popup Menu when clicking the Multistart button.
*/
import javax.swing.*;

import demo.Start;
import demo.Chess.ChessFrame;
import demo.StartFrame.SettingsFrame;

import java.awt.*;
import java.awt.event.*;

public class MultiStartMenu extends JPopupMenu{
    JMenuItem HostModeButton;
    JMenuItem GuestModeButton;

    public static HostDialog host_dialog_;
    public static GuestFrame guest_dialog_;
    public MultiStartMenu(){
        HostModeButton = new JMenuItem("Create Room");
        GuestModeButton = new JMenuItem("Join Room");
        HostModeButton.setFont(new Font("Consolas", Font.PLAIN, 15));
        GuestModeButton.setFont(new Font("Consolas", Font.PLAIN, 15));
        this.add(HostModeButton);
        this.add(GuestModeButton);
        host_dialog_ = new HostDialog(Start.start_frame_, "Create Room", true);
        guest_dialog_ = new GuestFrame(Start.start_frame_, "Join Room", true);
        ButtonActionSetting();
    }

    private void ButtonActionSetting(){
        HostModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Host Game Mode Start button clicked");
                host_dialog_.setVisible(true);
            }
        });

        GuestModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Guest Game Mode Start button clicked");
                guest_dialog_.setVisible(true);
            }
        });
    }
}
