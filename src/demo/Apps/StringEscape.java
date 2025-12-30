package demo.Apps;

import java.util.ArrayList;
import java.util.List;

/*
 * Handle escape in string
*/

public class StringEscape {
    public static String escapeCsv(String str) {
        // Add "" and escape if necessary
        if (str == null) return "";
        String escaped = str.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
    // Unescape a CSV field produced by escapeCsv
    public static String unescapeCsvField(String str) {
        if (str == null) return "";
        // delete first and tail space
        String t = str.trim();
        if (t.length() >= 2 && t.startsWith("\"") && t.endsWith("\"")) {
            t = t.substring(1, t.length() - 1);
        }
        return t.replace("\"\"", "\"");
    }

    // handle quoted fields
    public static List<String> parseCsvLine(String line) {
        List<String> out = new ArrayList<>();
        if (line == null || line.isEmpty()) return out;
        StringBuilder currentStr = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // escaped quote
                    currentStr.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                out.add(currentStr.toString());
                currentStr.setLength(0);
            } else {
                currentStr.append(ch);
            }
        }
        out.add(currentStr.toString());
        return out;
    }
}
