package demo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/*
 * NorthPanel class to manage the north panel of the GUI.
 * Main window is divided into NorthPanel and ChessPanel.
*/

public class NorthPanel extends JPanel {
    JButton StartButton = new JButton("Start");
    JButton ExitButton = new JButton("Exit");
    JButton RestartButton = new JButton("Restart");
    
    public NorthPanel() {
        this.setLayout(new FlowLayout());
        this.add(StartButton);
        this.add(ExitButton);
        this.add(RestartButton);
        StartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Start button clicked");
                Vars.control_.startGame();
            }
        });
        ExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: 
                //  1. add exit confirmation dialog
                //  2. terminate network connection and ChessBoard but not app
                System.out.println("Exit button clicked");
                System.exit(0);
            }
        });
        RestartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Restart button clicked");
                Vars.control_.restartGame();
            }
        });
    }
}
