package demo.StartFrame.MultiStart;

import javax.swing.*;

import demo.Start;
import demo.StartFrame.SettingsPanel;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;

import static demo.Apps.ColorDefine.*;
import static demo.Apps.StringEscape.*;

public class HostPanel extends JPanel{
    private static final int LABEL_WIDTH    = 150;
    private static final int FIELD_WIDTH    = 220;
    private static final int BUTTON_WIDTH   = 80;
    private static final int FONT_SIZE      = 20;
    private static final int HEIGHT         = 40;

    JPanel RoomIPPanel    = new JPanel();
    JPanel RoomPortPanel  = new JPanel();
    JPanel PassWordPanel  = new JPanel();

    JPanel ButtonPanel    = new JPanel();

    JLabel RoomIPLabel      = new JLabel("IP:");
    JLabel RoomPortLabel    = new JLabel("Port:");
    JLabel PassWordLabel    = new JLabel("Password:");
    
    JTextField IPTextField      = new JTextField();
    JTextField PortTextField    = new JTextField();
    JPasswordField PasswordField = new JPasswordField();

    JButton ComfirmButton = new JButton("Comfirm");
    JButton CancelButton  = new JButton("Cancel");

    public HostPanel(){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(Box.createVerticalGlue());
        this.setBackground(FRAME_BACK_COLOR);

        this.add(Box.createRigidArea(new Dimension(0, 30)));
        RoomIPSetting();
        this.add(Box.createRigidArea(new Dimension(0, 60)));
        RoomPortSetting();
        this.add(Box.createRigidArea(new Dimension(0, 60)));
        PassWordSetting();
        this.add(Box.createRigidArea(new Dimension(0, 60))); 
        ButtonSetting();
        this.add(Box.createRigidArea(new Dimension(0, 60))); 
        this.add(Box.createVerticalGlue());
    }

    private void RoomIPSetting() {
        RoomIPPanel.setLayout(new BoxLayout(RoomIPPanel, BoxLayout.X_AXIS));
        IPTextField.setMaximumSize(new Dimension(FIELD_WIDTH, HEIGHT));

        RoomIPLabel.setMaximumSize(new Dimension(LABEL_WIDTH, HEIGHT));
        RoomIPLabel.setFont(new Font("Consolas", Font.BOLD, FONT_SIZE));
        RoomIPLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        RoomIPPanel.setBackground(FRAME_BACK_COLOR);
        RoomIPPanel.add(RoomIPLabel);
        RoomIPPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        RoomIPPanel.add(IPTextField);
        this.add(RoomIPPanel);
    }

    private void RoomPortSetting() {
        RoomPortPanel.setLayout(new BoxLayout(RoomPortPanel, BoxLayout.X_AXIS));
        PortTextField.setMaximumSize(new Dimension(FIELD_WIDTH, HEIGHT));

        RoomPortLabel.setMaximumSize(new Dimension(LABEL_WIDTH, HEIGHT));
        RoomPortLabel.setFont(new Font("Consolas", Font.BOLD, FONT_SIZE));
        RoomPortLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        RoomPortPanel.setBackground(FRAME_BACK_COLOR);
        RoomPortPanel.add(RoomPortLabel);
        RoomPortPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        RoomPortPanel.add(PortTextField);
        this.add(RoomPortPanel);
    }

    private void PassWordSetting() {
        PassWordPanel.setLayout(new BoxLayout(PassWordPanel, BoxLayout.X_AXIS));
        PasswordField.setMaximumSize(new Dimension(FIELD_WIDTH, HEIGHT));

        PassWordLabel.setMaximumSize(new Dimension(LABEL_WIDTH, HEIGHT));
        PassWordLabel.setFont(new Font("Consolas", Font.BOLD, FONT_SIZE));
        PassWordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        PassWordPanel.setBackground(FRAME_BACK_COLOR);
        PassWordPanel.add(PassWordLabel);
        PassWordPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        PassWordPanel.add(PasswordField);
        this.add(PassWordPanel);
    }

    private void ButtonSetting(){
        ButtonPanel.setLayout(new BoxLayout(ButtonPanel, BoxLayout.X_AXIS));
        // ComfirmButton.setMaximumSize(new Dimension(BUTTON_WIDTH, HEIGHT));
        // CancelButton.setMaximumSize(new Dimension(BUTTON_WIDTH, HEIGHT));
        ComfirmButton.setFont(new Font("Consolas", Font.BOLD, FONT_SIZE));
        CancelButton.setFont(new Font("Consolas", Font.BOLD, FONT_SIZE));
        // Action Listening

        ComfirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: connect web
                System.out.println("Comfirm button clicked");
                MultiStartMenu.host_dialog_.setVisible(false);
            }
        });

        CancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Cancel button clicked");
                MultiStartMenu.host_dialog_.setVisible(false);
            }
        });

        ButtonPanel.setBackground(FRAME_BACK_COLOR);
        ButtonPanel.add(ComfirmButton);
        ButtonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        ButtonPanel.add(CancelButton);
        this.add(ButtonPanel);
    }
}
