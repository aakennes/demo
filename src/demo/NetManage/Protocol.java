package demo.NetManage;

/*
 * Lightweight text protocol for handshake and messages.
*/

import java.util.*;
import static demo.Apps.ColorDefine.*;
import static demo.Apps.StringEscape.*;

public final class Protocol {
    // message types
	public static final String T_JOIN = "JOIN";
	public static final String T_JOIN_ACK = "JOIN_ACK";
	public static final String T_REJECT = "REJECT";
	public static final String T_MOVE = "MOVE";
	public static final String T_SYNC = "SYNC";
	public static final String T_HEARTBEAT = "HEARTBEAT";
	public static final String T_LEAVE = "LEAVE";

	// common keys
	public static final String K_TYPE = "type";
	public static final String K_NAME = "name";
	public static final String K_PASSWORD = "password";
	public static final String K_REASON = "reason";
	public static final String K_PLAYER_ID = "playerId";
	public static final String K_COLOR = "color";
	public static final String K_X = "x";
	public static final String K_Y = "y";
	public static final String K_BOARD = "board"; // serialized board
	public static final String K_CURRENT_TURN = "currentTurn";
	public static final String K_GAME_STATE = "gameState";

	public static String buildJoin(String name, String password) {
		Map<String, String> m = new LinkedHashMap<>();
		m.put(K_TYPE, T_JOIN);
		m.put(K_NAME, name == null ? "" : name);
		m.put(K_PASSWORD, password == null ? "" : password);
		return pack(m);
	}

	public static String buildJoinAck(String playerId, String color) {
		Map<String, String> m = new LinkedHashMap<>();
		m.put(K_TYPE, T_JOIN_ACK);
		m.put(K_PLAYER_ID, playerId);
		m.put(K_COLOR, color == null ? "" : color);
		return pack(m);
	}

	public static String buildReject(String reason) {
		Map<String, String> m = new LinkedHashMap<>();
		m.put(K_TYPE, T_REJECT);
		m.put(K_REASON, reason == null ? "" : reason);
		return pack(m);
	}

	public static String buildMove(int x, int y, String playerId) {
		Map<String, String> m = new LinkedHashMap<>();
		m.put(K_TYPE, T_MOVE);
		m.put(K_X, Integer.toString(x));
		m.put(K_Y, Integer.toString(y));
		m.put(K_PLAYER_ID, playerId == null ? "" : playerId);
		return pack(m);
	}

	/**
	 * boardStr: application-specific serialized board
     * TODO: more notes about the format
	 */
	public static String buildSync(String boardStr, int currentTurn, int gameState) {
		Map<String, String> m = new LinkedHashMap<>();
		m.put(K_TYPE, T_SYNC);
		m.put(K_BOARD, boardStr == null ? "" : boardStr);
		m.put(K_CURRENT_TURN, Integer.toString(currentTurn));
		m.put(K_GAME_STATE, Integer.toString(gameState));
		return pack(m);
	}

	public static String buildHeartbeat() {
		Map<String, String> m = new LinkedHashMap<>();
		m.put(K_TYPE, T_HEARTBEAT);
		return pack(m);
	}

	public static String buildLeave(String playerId) {
		Map<String, String> m = new LinkedHashMap<>();
		m.put(K_TYPE, T_LEAVE);
		m.put(K_PLAYER_ID, playerId == null ? "" : playerId);
		return pack(m);
	}

	// ----- packing / parsing -----
	public static String pack(Map<String, String> pairs) {
		if (pairs == null || pairs.isEmpty()) return "";
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Map.Entry<String, String> e : pairs.entrySet()) {
			if (!first) sb.append(';');
			first = false;
			sb.append(escapeCsv(e.getKey())).append('=').append(escapeCsv(e.getValue()));
		}
		return sb.toString();
	}

	public static Message parse(String text) {
		if (text == null) return new Message("", Collections.emptyMap());
		Map<String, String> m = new LinkedHashMap<>();
		int len = text.length();
		StringBuilder key = new StringBuilder();
		StringBuilder val = new StringBuilder();
		String cur = "key"; // switch between key and val
		boolean escape = false;
		for (int i = 0; i < len; ++i) {
			char c = text.charAt(i);
			if (escape) {
				if (cur.equals("key")) key.append(c); else val.append(c);
				escape = false;
				continue;
			}
			if (c == '\\') { escape = true; continue; }
			if (cur.equals("key") && c == '=') { cur = "val"; continue; }
			if (cur.equals("val") && c == ';') {
				putParsedPair(m, key, val);
				key.setLength(0); val.setLength(0); cur = "key"; continue;
			}
			if (cur.equals("key")) key.append(c); else val.append(c);
		}
		if (key.length() > 0 || val.length() > 0) {
			putParsedPair(m, key, val);
		}
		String type = m.getOrDefault(K_TYPE, "");
		return new Message(type, m);
	}

	private static void putParsedPair(Map<String, String> map, StringBuilder rawKey, StringBuilder rawVal) {
		String cleanKey = unescapeCsvField(rawKey.toString());
		String cleanVal = unescapeCsvField(rawVal.toString());
		map.put(cleanKey, cleanVal);
	}

}
