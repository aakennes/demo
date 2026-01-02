package demo.NetManage;

/*

*/

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import demo.NetManage.Net;

public class Connection {
    private final Net owner_;
    private final Socket socket_;
    private PrintWriter out_;
    private BufferedReader in_;
    private volatile boolean openDoor = false;
    private volatile boolean disconnectedNotified = false;

    Connection(Net owner, Socket socket_) {
        this.owner_ = owner;
        this.socket_ = socket_;
    }

    void startConnect() {
        try {
            out_ = new PrintWriter(socket_.getOutputStream(), true);
            in_  = new BufferedReader(new InputStreamReader(socket_.getInputStream(), "UTF-8"));
            openDoor = true;
        } catch (IOException e) {
            owner_.notifyError(this, e);
            closeConnect();
            return;
        }

        owner_.executor.execute(this::readerLoop);
    }

    private void readerLoop() {
        try {
            String line;
            while (openDoor && (line = in_.readLine()) != null) {
                owner_.notifyIncoming(this, line);
            }
        } catch (IOException e) {
            if (openDoor) {
                owner_.notifyError(this, e);
            }
        } finally {
            closeConnect();
        }
    }

    public void sendMessage(String message_) {
        if(!openDoor || out_ == null){
            return;
        }
        out_.println(message_);
    }

    public void closeConnect() {
        openDoor = false;
        try { 
            if (socket_ != null && !socket_.isClosed()) {
                socket_.close(); 
            }
        } catch (IOException ignored) {}
        try { 
            if (in_ != null) {
                in_.close(); 
            }
        } catch (IOException ignored) {}
        if (out_ != null) {
            out_.close();
        }
        notifyDisconnectedOnce();
    }

    private void notifyDisconnectedOnce() {
        if (!disconnectedNotified) {
            disconnectedNotified = true;
            owner_.notifyDisconnected(this);
        }
    }

    public String getRemoteAddress() {
        SocketAddress addr = socket_.getRemoteSocketAddress();
        return addr == null ? "" : addr.toString();
    }

    public boolean isOpen() { 
        return openDoor && socket_ != null && !socket_.isClosed();
    }
}