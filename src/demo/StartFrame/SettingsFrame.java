package demo.StartFrame;

/*
 * SettingsFrame includes SettingsPanel in a JFrame.
 * Used to display the settings GUI.
*/
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.nio.file.*;
import java.io.*;
import java.util.*;

import static demo.Apps.StringEscape.*;

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
        // read settings.csv
        Path path = Paths.get("data", "settings.csv");
        try (BufferedReader br = Files.newBufferedReader(path)) {
            // skip header
            String header = br.readLine();
            String line = br.readLine();
            if (line != null) {
                List<String> fields = parseCsvLine(line);
                if (fields.size() >= 3) {
                    String color = unescapeCsvField(fields.get(0));
                    String username = unescapeCsvField(fields.get(1));
                    String ip = unescapeCsvField(fields.get(2));
                    settings_panel_.setProfileColor(color);
                    settings_panel_.setUsername(username);
                    settings_panel_.setDefaultIP(ip);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.setVisible(true);
	}

    

	public void hideFrame() {
		this.setVisible(false);
	}
}