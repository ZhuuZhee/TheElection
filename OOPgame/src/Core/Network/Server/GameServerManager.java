package Core.Network.Server;

import Core.Network.NetworkProtocol;
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

    public void sendToClient(String targetPlayerId, JSONObject mes) {
        for (ClientHandler client : clients) {
            if (client.getPlayerId() != null && client.getPlayerId().equals(targetPlayerId)) {
                client.sendMessage(mes);
                break;
            }
        }
    }

    public synchronized void processAction(String playerId, JSONObject action) {
        if (!action.has("actionType")) return;
        String type = action.getString("actionType");

        if (type.equals(NetworkProtocol.JOIN.name())) {
            // กันชื่อแตก ถ้าเกิดหาชื่อไม่เจอ ใช้ Unknown- id 4 ตัว
            String pName = action.optString("playerName", "Unknown-" + playerId.substring(0, 4));
            
            // สร้างตัวละครแล้วเพิ่มลงใน gameState
            Core.Player.Player newPlayer = new Core.Player.Player(playerId, pName, false);
            gameState.getPlayers().add(newPlayer);
            System.out.println(pName + " ("+ playerId +") joined the game.");

            // ส่งข้อมูลยืนยันรับเข้าห้องให้คนที่เข้ามา
            JSONObject ack = new JSONObject();
            ack.put("type", NetworkProtocol.JOIN_ACK.name());
            ack.put("assignedId", playerId);
            sendToClient(playerId, ack);

            broadcast(gameState.generateSyncData());
        } else if (type.equals(NetworkProtocol.START_GAME.name())) {
            System.out.println("START_GAME all clients");
            org.json.JSONObject startPacket = new org.json.JSONObject();
            startPacket.put("type", NetworkProtocol.START_GAME.name());
            broadcast(startPacket);
        } else if (type.equals(NetworkProtocol.END_TURN.name())) {
            nextTurn();
        } else if (type.equals(NetworkProtocol.USE_CARD.name())) {
            // Logic อัพเดตเมือง ???

            broadcast(gameState.generateSyncData());
        }
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
        gameState.incrementPhaseCounter();

        broadcast(gameState.generateSyncData());
    }

}
