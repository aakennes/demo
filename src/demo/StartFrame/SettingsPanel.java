package demo.StartFrame;

/*
 * SettingsPanel includes settings options for the game.
 * Including profile color, username, default IP.
*/

import javax.swing.*;

import demo.Start;
import demo.Chess.ChessFrame;

import java.awt.*;
import java.awt.event.*;
import java.io.*;


import static demo.Apps.ColorDefine.*;

public class SettingsPanel extends JPanel{
    private static final int LABEL_WIDTH    = 150;
    private static final int FIELD_WIDTH    = 220;
    private static final int BUTTON_WIDTH   = 80;
    // private static final int BUTTON_HEIGHT  = 40;
    private static final int FONT_SIZE      = 20;
    private static final int HEIGHT         = 40;
    

    JLabel TitleLabel = new JLabel("Settings");
    JLabel ProfileColorLabel = new JLabel("Profilecolor:");
    JLabel UsernameLabel = new JLabel("Username:");
    JLabel DefaultIPLabel = new JLabel("DefaultIP:");

    JPanel ProfileColorPanel = new JPanel();
    JPanel UsernamePanel    = new JPanel();
    JPanel DefaultIPPanel   = new JPanel();
    JPanel ButtonPanel      = new JPanel();

    JButton ComfirmButton = new JButton("Comfirm");
    JButton CancelButton  = new JButton("Cancel");


    JComboBox<String> ProfileColorComboBox = new JComboBox<>(new String[] {"Blue", "Green", "Yellow", "Red", "Grey", "Black", "Purple"});
    JTextField UsernameTextField = new JTextField();
    JTextField DefaultIPTextField = new JTextField();
    public SettingsPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(Box.createVerticalGlue());
        this.setBackground(FRAME_BACK_COLOR);

        this.add(Box.createRigidArea(new Dimension(0, 20))); // top to title top
        TitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        TitleLabel.setFont(new Font("Consolas", Font.BOLD, 46));
        this.add(TitleLabel);
        this.add(Box.createRigidArea(new Dimension(0, 60))); // title bottom to button top
        // Profile Color Setting
        ProfileColorSetting();
        this.add(Box.createRigidArea(new Dimension(0, 60))); // button to next button
        // Username Setting
        UsernameSetting();
        this.add(Box.createRigidArea(new Dimension(0, 60))); // button to next button
        // Default IP Setting
        DefaultIPSetting();
        this.add(Box.createRigidArea(new Dimension(0, 60))); 
        // Button Setting
        ButtonSetting();
        this.add(Box.createRigidArea(new Dimension(0, 60))); 
        this.add(Box.createVerticalGlue());
    }

    private void ProfileColorSetting() {
        ProfileColorPanel.setLayout(new BoxLayout(ProfileColorPanel, BoxLayout.X_AXIS));
        ProfileColorComboBox.setMaximumSize(new Dimension(FIELD_WIDTH, HEIGHT));

        ProfileColorLabel.setMaximumSize(new Dimension(LABEL_WIDTH, HEIGHT));
        ProfileColorLabel.setFont(new Font("Consolas", Font.BOLD, FONT_SIZE));
        ProfileColorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        ProfileColorPanel.setBackground(FRAME_BACK_COLOR);
        ProfileColorPanel.add(ProfileColorLabel);
        ProfileColorPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        ProfileColorPanel.add(ProfileColorComboBox);
        this.add(ProfileColorPanel);
    }

    private void UsernameSetting() {
        UsernamePanel.setLayout(new BoxLayout(UsernamePanel, BoxLayout.X_AXIS));
        UsernameTextField.setMaximumSize(new Dimension(FIELD_WIDTH, HEIGHT));

        UsernameLabel.setMaximumSize(new Dimension(LABEL_WIDTH, HEIGHT));
        UsernameLabel.setFont(new Font("Consolas", Font.BOLD, FONT_SIZE));
        UsernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        UsernamePanel.setBackground(FRAME_BACK_COLOR);
        UsernamePanel.add(UsernameLabel);
        UsernamePanel.add(Box.createRigidArea(new Dimension(20, 0)));
        UsernamePanel.add(UsernameTextField);
        this.add(UsernamePanel);
    }

    private void DefaultIPSetting(){
        DefaultIPPanel.setLayout(new BoxLayout(DefaultIPPanel, BoxLayout.X_AXIS));
        DefaultIPTextField.setMaximumSize(new Dimension(FIELD_WIDTH, HEIGHT));

        DefaultIPLabel.setMaximumSize(new Dimension(LABEL_WIDTH, HEIGHT));
        DefaultIPLabel.setFont(new Font("Consolas", Font.BOLD, FONT_SIZE));
        DefaultIPLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        DefaultIPPanel.setBackground(FRAME_BACK_COLOR);
        DefaultIPPanel.add(DefaultIPLabel);
        DefaultIPPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        DefaultIPPanel.add(DefaultIPTextField);
        this.add(DefaultIPPanel);
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
                System.out.println("Comfirm button clicked");
                // TODO : write in data/settings.csv
                Start.settings_frame_.hideFrame();
                // Start.start_frame_.showFrame();
            }
        });

        CancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Cancel button clicked");
                Start.settings_frame_.hideFrame();
                // Start.start_frame_.showFrame();
            }
        });

        ButtonPanel.setBackground(FRAME_BACK_COLOR);
        ButtonPanel.add(ComfirmButton);
        ButtonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        ButtonPanel.add(CancelButton);
        this.add(ButtonPanel);
    }
}
