package p.server;

import p.network.TCPConnection;
import p.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener {
    public static void main(String[] args) {
        new ChatServer();
    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer() {
        System.out.println("Server running");
        try (ServerSocket serverSocket = new ServerSocket(8189);) {
            while (true) {
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("TCPConnection exception " + e);
                    connections.remove(this);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(p.network.TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendAllConnections("New Client connected: " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(p.network.TCPConnection tcpConnection, String msg) {
        sendAllConnections(msg);
    }

    @Override
    public synchronized void onDisconnect(p.network.TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendAllConnections("Client disconnected: " + tcpConnection);
    }

    @Override
    public synchronized void onException(p.network.TCPConnection tcpConnection, Exception e) {
        System.out.println("TCP Connection exception: " + e);
        connections.remove(tcpConnection);
    }

    private void sendAllConnections(String msg) {
        System.out.println(msg);
        for (int i = 0; i < connections.size(); i++) {
            connections.get(i).sendMessage(msg);
        }
    }
}
