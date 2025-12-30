package demo.StartFrame;

/*
 * StartPanel includes title, single button, multi button, exit button, and settings button.
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import demo.Chess.ChessFrame;
import demo.Start;

public class StartPanel extends JPanel{
    JTextArea Title = new JTextArea("Chess Game");
    JButton SingleStartButton = new JButton("Single Player Start");
    JButton MultiStartButton = new JButton("Multi player Start");
    JButton SettingsButton = new JButton("Settings");
    JButton ExitButton = new JButton("Exit");
    public StartPanel() {
        this.setLayout(new FlowLayout());
        this.add(Title);
        this.add(SingleStartButton);
        this.add(MultiStartButton);
        this.add(SettingsButton);
        this.add(ExitButton);
        SingleStartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Single Player Start button clicked");
                ChessFrame.control_.startGame();
                Start.start_frame_.hideFrame();
                Start.chess_frame_.showFrame();
            }
        });

        MultiStartButton.addActionListener(new ActionListener() {
            // TODO: implement multi player start logic
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Multi Player Start button clicked");
                ChessFrame.control_.startGame();
                Start.start_frame_.hideFrame();
                Start.chess_frame_.showFrame();
            }
        });

        SettingsButton.addActionListener(new ActionListener() {
            // TODO: implement settings logic
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Settings button clicked");
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
