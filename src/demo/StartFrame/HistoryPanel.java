package demo.StartFrame;

/*
 * Show history from data/history.csv.
*/

import javax.swing.*;
import javax.swing.table.*;

import demo.Start;
import demo.Apps.ColorDefine;
import demo.Apps.ColorDefine.*;

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
		model_ = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) { return false; }
		};
		table_ = new JTable(model_);
		table_.setFillsViewportHeight(true);
		table_.setRowHeight(24);
		table_.setAutoCreateRowSorter(true);
		table_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table_.getTableHeader().setFont(table_.getTableHeader().getFont().deriveFont(Font.BOLD));
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
			// ensure table columns reflect model (useful after clearing/reloading)
			table_.setModel(model_);
			// custom renderer: center by default, left for IP, alternate row colors
			DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					if (!isSelected) c.setBackground((row % 2 == 0) ? Color.WHITE : ColorDefine.TABLE_LINE_COLOR);
					setHorizontalAlignment(SwingConstants.CENTER);
					return c;
				}
			};
			// apply renderer to all columns (will be refined below)
			table_.setDefaultRenderer(Object.class, renderer);
			// set preferred column widths by column name
			TableColumnModel colModel = table_.getColumnModel();
			for (int i = 0; i < cols.size(); i++) {
				String name = cols.get(i);
				if (i >= colModel.getColumnCount()) continue;
				TableColumn col = colModel.getColumn(i);
				switch (name) {
					case "GameMode": {
						col.setPreferredWidth(100); 
						break;
					}
					case "RoomIP": {
						col.setPreferredWidth(150);
						break;
					}
					case "OpponentName": {
						col.setPreferredWidth(140);
						break;
					}
					case "MyColor": {
						col.setPreferredWidth(80); 
						break;
					}
					case "WinColor": {
						col.setPreferredWidth(80); 
						break;
					}
					case "Time": {
						col.setPreferredWidth(140); 
						break;
					}
					default: {
						col.setPreferredWidth(100); 
						break;
					}
				}
			}

			String line;
			while ((line = br.readLine()) != null) {
				List<String> fields = parseCsvLine(line);
				// ensure fields size matches columns
				while (fields.size() < cols.size()) fields.add("");
				// handle empty OpponentName and WinColor(Single gamemode)
				for (int i = 0; i < cols.size() && i < fields.size(); i++) {
					String colName = cols.get(i);
					String val = fields.get(i);
					if (("OpponentName".equals(colName) || "WinColor".equals(colName)) 
												&& (val == null || val.trim().isEmpty())) 
					{
						fields.set(i, "-");
					}
				}
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
			if (!Files.exists(path)) {
				model_.setRowCount(0);
				model_.setColumnCount(0);
				JOptionPane.showMessageDialog(this, "History cleared.", "Cleared", JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			List<String> lines = Files.readAllLines(path);
			String header = lines.get(0);
			try (BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING)) {
				bw.write(header);
				bw.newLine();
			}
			// reset table columns to header
			List<String> cols = parseCsvLine(header);
			model_.setRowCount(0);
			model_.setColumnCount(0);
			for (String c : cols) model_.addColumn(c);
			JOptionPane.showMessageDialog(this, "History cleared.", "Cleared", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Failed to clear history: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
