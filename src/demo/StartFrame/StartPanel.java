package demo.StartFrame;

/*
 * StartPanel includes title, single button, multi button, exit button, and settings button.
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import demo.Chess.ChessControl;
import demo.Chess.ChessFrame;
import demo.StartFrame.SettingsFrame;
import demo.Start;
import static demo.Apps.ColorDefine.*;

public class StartPanel extends JPanel{
    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 600;

    JLabel TitleLabel = new JLabel("Gobang Game");
    JButton SingleStartButton = new JButton("Single Player Start");
    JButton MultiStartButton = new JButton("Multi player Start");
    JButton SettingsButton = new JButton("Settings");
    JButton HistoryButton = new JButton("History");
    JButton ExitButton = new JButton("Exit");

    public StartPanel() {
        // Layout setup
        LayoutSetup();
        // Button actions
        ButtonActions();
    }
    
    private void LayoutSetup() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(Box.createVerticalGlue());
        this.setBackground(FRAME_BACK_COLOR);
        
        this.add(Box.createRigidArea(new Dimension(0, 20))); // top to title top
        TitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        TitleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 56));
        this.add(TitleLabel);
        this.add(Box.createRigidArea(new Dimension(0, 60))); // title bottom to button top
        
        JButton[] buttons = {SingleStartButton, MultiStartButton, SettingsButton, HistoryButton, ExitButton};
        for (JButton button : buttons) {
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(250, 45));
            button.setPreferredSize(new Dimension(250, 45));
            button.setFont(new Font("Consolas", Font.PLAIN, 18));
            button.setContentAreaFilled(false);

            this.add(button);
            this.add(Box.createRigidArea(new Dimension(0, 45))); // button to next button
        }
        this.add(Box.createVerticalGlue());
    }

    private void ButtonActions() {
        SingleStartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Single Player Start button clicked");
                ChessFrame.control_.setGameMode(0);
                ChessFrame.control_.setRoomIP(SettingsFrame.settings_panel_.getDefaultIP());
                ChessFrame.control_.startGame();
                // Start.start_frame_.hideFrame();
                Start.chess_frame_.showFrame();
            }
        });

        MultiStartButton.addActionListener(new ActionListener() {
            // TODO: implement multi player start logic
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Multi Player Start button clicked");
                ChessFrame.control_.startGame();
                // Start.start_frame_.hideFrame();
                Start.chess_frame_.showFrame();
            }
        });

        SettingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Settings button clicked");
                // Start.start_frame_.hideFrame();
                Start.settings_frame_.showFrame();
            }
        });

        HistoryButton.addActionListener(new ActionListener() {
            // TODO: implement history logic
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("History button clicked");
                Start.history_frame_.showFrame();
            }
        });

        ExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Exit button clicked");
                System.exit(0);
            }
        });
    }
}
