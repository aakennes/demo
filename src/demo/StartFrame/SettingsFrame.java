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
                    String color = SettingsPanel.unescapeCsvField(fields.get(0));
                    String username = SettingsPanel.unescapeCsvField(fields.get(1));
                    String ip = SettingsPanel.unescapeCsvField(fields.get(2));
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

    // handle quoted fields
    private static List<String> parseCsvLine(String line) {
        List<String> out = new ArrayList<>();
        if (line == null || line.isEmpty()) return out;
        StringBuilder currentStr = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // escaped quote
                    currentStr.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                out.add(currentStr.toString());
                currentStr.setLength(0);
            } else {
                currentStr.append(c);
            }
        }
        out.add(currentStr.toString());
        return out;
    }

	public void hideFrame() {
		this.setVisible(false);
	}
}