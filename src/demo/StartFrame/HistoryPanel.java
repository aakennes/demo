package demo.StartFrame;

/*
 * Show history from data/history.csv.
*/

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import demo.Start;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import static demo.Apps.StringEscape.*;

public class HistoryPanel extends JPanel {
	DefaultTableModel model_;
	JTable table_;
	JScrollPane scroll_;
    
	JButton refreshButton;
	JButton clearButton;
    JButton returnButton;

	public HistoryPanel() {
		this.setLayout(new BorderLayout());
		model_ = new DefaultTableModel();
		table_ = new JTable(model_);
		scroll_ = new JScrollPane(table_);
		this.add(scroll_, BorderLayout.CENTER);

		JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		refreshButton = new JButton("Refresh");
		clearButton = new JButton("Clear History");
        returnButton = new JButton("Return");
		top.add(refreshButton);
		top.add(clearButton);
        top.add(returnButton);
		this.add(top, BorderLayout.NORTH);

		returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Cancel button clicked");
				Start.history_frame_.hideFrame();
                // Start.start_frame_.showFrame();
            }
        }); 
		refreshButton.addActionListener(e -> loadData());
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Cancel button clicked");
				int r = JOptionPane.showConfirmDialog(HistoryPanel.this, "Clear all history?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (r == JOptionPane.YES_OPTION) {
                    clearHistory();
                }
            }
        }); 

		loadData();
	}

	public void loadData() {
		Path path = Paths.get("data", "history.csv");
		model_.setRowCount(0);
		model_.setColumnCount(0);
		if (!Files.exists(path)) return;

		try (BufferedReader br = Files.newBufferedReader(path)) {
			String header = br.readLine();
			if (header == null) return;
			List<String> cols = parseCsvLine(header);
			for (String c : cols) model_.addColumn(c);

			String line;
			while ((line = br.readLine()) != null) {
				List<String> fields = parseCsvLine(line);
				// ensure fields size matches columns
				while (fields.size() < cols.size()) fields.add("");
				model_.addRow(fields.toArray(new Object[0]));
			}

		} catch (IOException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Failed to load history: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void clearHistory() {
		Path path = Paths.get("data", "history.csv");
		try {
            // TODO: Beautify the table
			if (!Files.exists(path)) {
				model_.setRowCount(0);
				model_.setColumnCount(0);
				JOptionPane.showMessageDialog(this, "History cleared.", "Cleared", JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			List<String> lines = Files.readAllLines(path);
			if (lines.isEmpty()) {
				Files.delete(path);
				model_.setRowCount(0);
				model_.setColumnCount(0);
			} else {
				String header = lines.get(0);
				// overwrite file with header only
				try (BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING)) {
					bw.write(header);
					bw.newLine();
				}
				// reset table columns to header
				List<String> cols = parseCsvLine(header);
				model_.setRowCount(0);
				model_.setColumnCount(0);
				for (String c : cols) model_.addColumn(c);
			}
			JOptionPane.showMessageDialog(this, "History cleared.", "Cleared", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Failed to clear history: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
