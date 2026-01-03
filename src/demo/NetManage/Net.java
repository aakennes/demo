package demo.NetManage;

/**
 *  network prototype for server/client connections.
 * - starting a server (accepting connections)
 * - connecting as client
 * - sending messages
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Net {
    // message listener_ callback
	public interface MessageListener {
		void onConnected(Connection conn);
		void onMessage(Connection conn, String message);
		void onDisconnected(Connection conn);
		void onError(Connection conn, Exception ex);
	}

	// executor for background IO tasks
	public final ExecutorService executor = Executors.newCachedThreadPool();

	// server-side socket and state
	private ServerSocket server_socket_;
	// listener is an instance field (not static) and volatile for safe publication across threads
	private volatile MessageListener listener_ = new MessageListener() {
		@Override public void onConnected(Connection conn) {}
		@Override public void onMessage(Connection conn, String message) {}
		@Override public void onDisconnected(Connection conn) {}
		@Override public void onError(Connection conn, Exception ex) {}
	};
	private boolean serverRunning = false;

	private final List<Connection> connections = Collections.synchronizedList(new ArrayList<>());

	public void setMessageListener(MessageListener listener_) {
		if (listener_ == null) {
			this.listener_ = new MessageListener() {
				@Override public void onConnected(Connection conn) {}
				@Override public void onMessage(Connection conn, String message) {}
				@Override public void onDisconnected(Connection conn) {}
				@Override public void onError(Connection conn, Exception ex) {}
			};
		} else {
			this.listener_ = listener_;
		}
	}

	public void startServer(int port) throws IOException {
		if (serverRunning) return;
		System.out.println("Net.java:start server.");
		server_socket_ = new ServerSocket(port);
		serverRunning = true;
		executor.execute(() -> {
			try {
				while (serverRunning && !server_socket_.isClosed()) {
                    // block until new connection
					Socket client_socket_ = server_socket_.accept();
					// if already have a client connected, reject new connection
					synchronized (connections) {
						if (connections.size() >= 1) {
							try {
								PrintWriter tmpOut = new PrintWriter(client_socket_.getOutputStream(), true);
								tmpOut.println("REJECT:SERVER_FULL");
								// tmpOut.flush();
							} catch (IOException e) {
                                e.printStackTrace();
							} finally {
								client_socket_.close();
							}
                            listener_.onError(null, 
                                new IOException("Rejected incoming connection (server full): " + client_socket_.getRemoteSocketAddress()));
							continue;
						} else {
							Connection newConnection = new Connection(this, client_socket_);
							connections.add(newConnection);
							newConnection.startConnect();
							listener_.onConnected(newConnection);
						}
					}
				}
			} catch (IOException e) {
				if (serverRunning) {
                    listener_.onError(null, e);
                }
			}
		});
	}

	public void stopServer() {
		serverRunning = false;
		try {
			if (server_socket_ != null) server_socket_.close();
		} catch (IOException e) {
            e.printStackTrace();
        }
		// close all connections
        // actually connections.size() = 1 all the time
		synchronized (connections) {
			for (Connection connection_ : new ArrayList<>(connections)){
                connection_.closeConnect();
            } 
			connections.clear();
		}
	}

	public Connection connect(String host, int port) throws IOException {
		Socket client_socket_ = new Socket(host, port);
		// System.out.println("Net.java: connect to server " + host + ":" + port);
		// check immediate server response for rejection
		BufferedReader preReader = new BufferedReader(new InputStreamReader(client_socket_.getInputStream(), "UTF-8"));
		try {
			client_socket_.setSoTimeout(3000);
			String first = null;
			try {
				first = preReader.readLine();
			} catch (SocketTimeoutException ste) {
                // ste.printStackTrace();
			}
			client_socket_.setSoTimeout(0);
			if (first != null && first.startsWith("REJECT:")) {
				String reason = first.substring(7);
				client_socket_.close();
                // TODO: notice ClientPanel to handle
				throw new IOException("Connection rejected by server: " + reason);
			}
		} catch (IOException e) {
            client_socket_.close(); 
			throw e;
		} finally {
			client_socket_.setSoTimeout(0);
		}

		Connection newConnection = new Connection(this, client_socket_);
		connections.add(newConnection);
		newConnection.startConnect();
		// System.out.println("Net.java: connect to server " + host + ":" + port);
		listener_.onConnected(newConnection);
		return newConnection;
	}

	public void disconnect(Connection connection_) {
		if (connection_ != null) {
			connection_.closeConnect();
		}
	}

	public void send(Connection connection_, String message_) {
        connection_.sendMessage(message_);
    }

	// callbacks from Connection
	void notifyIncoming(Connection conn, String message) {
		listener_.onMessage(conn, message);
	}

	void notifyError(Connection conn, Exception ex) {
		listener_.onError(conn, ex);
	}

	void notifyDisconnected(Connection conn) {
		connections.remove(conn);
		listener_.onDisconnected(conn);
	}

}
