package Core.Network.Server;

import java.net.*;
import java.util.*;
import org.json.JSONObject;

public class GameServerManager {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>(); //
    private GameState gameState = new GameState();
    private boolean isStarted = false;

    public void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server start");
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket, this);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void broadcast(JSONObject mes) {
        for (ClientHandler client : clients) {
            client.sendMessage(mes);
        }
    }

    public synchronized void processAction(String playerId, JSONObject action) {
    }

    public void removeClient(String playerId) {
        clients.removeIf(client -> {
            if (client.getPlayerId() != null && client.getPlayerId().equals(playerId)) {
                return true;
            }
            return false;
        });
        System.out.println("Player removed: " + playerId);
    }

    public synchronized void nextTurn() {
    }

}
