package Core.Network.Server;

import Core.Network.NetworkProtocol;

import java.net.*;
import java.util.*;

import Core.Network.PacketBuilder;
import Core.Player.Player;
import org.json.JSONObject;

import static Core.Network.PacketBuilder.createKickPacket;

public class GameServerManager {
    private static final int PING_INTERVAL_MS = 5000;
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    private GameState gameState = new GameState();
    private boolean running = false;

    public void startServer(int port) {
        running = true;
        // Ping loop ลบคนที่ timeout
        new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(PING_INTERVAL_MS);
                } catch (InterruptedException ex) {
                    break;
                }

                org.json.JSONObject ping = new org.json.JSONObject();
                ping.put("type", NetworkProtocol.PING.name());
                broadcast(ping);

                List<String> timedOut = new ArrayList<>();
                for (ClientHandler c : new ArrayList<>(clients)) {
                    if (c.isTimedOut())
                        timedOut.add(c.getPlayerId());
                }
                for (String id : timedOut) {
                    System.out.println("Ping timeout: " + id);
                    removeClient(id);
                }
            }
        }).start();

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server start");
            while (running) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket, this);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (Exception e) {
            if (running) {
                e.printStackTrace();
            }
        }
    }

    // Host ออก stopServer
    public void stopServer() {
        running = false;
        try {
            // บอก client ว่า Host ออก
            broadcast(createKickPacket());

            for (ClientHandler client : clients) {
                client.disconnect();
            }
            clients.clear();
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            System.out.println("Server stop");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (!action.has("type"))
            return;
        String type = action.getString("type");

        if(!type.equals(NetworkProtocol.PONG.name()))
            System.out.println("Server : %s form {%s}".formatted(action,playerId) );

        if (type.equals(NetworkProtocol.JOIN.name())) {
            onJoinGame(action, playerId);
        } else if (type.equals(NetworkProtocol.START_GAME.name())) {
            onStartGame();
        } else if(type.equals(NetworkProtocol.UPDATE_PLAYER.name())){
            onUpdatePlayerData(playerId, action);
        } else if (type.equals(NetworkProtocol.PONG.name())) {
            onPong(playerId);
        } else if (type.equals(NetworkProtocol.END_TURN.name())) {
            onNextTurn();
        } else if (type.equals(NetworkProtocol.USE_CARD.name())) {
            onUseCard(action); // อัพเดตค่าเมืองให้ทุกคนเห็นเหมือนกัน
        }else if (type.equals(NetworkProtocol.DESTROY_AND_SKIP_DRAW.name())) {
            broadcast(action); // ส่งให้ทุกคนรับกรรมพร้อมกัน
        }
    }

    public void removeClient(String playerId) {
        ClientHandler target = null;
        for (ClientHandler c : clients) {
            if (c.getPlayerId() != null && c.getPlayerId().equals(playerId)) {
                target = c;
                break;
            }
        }
        if (target != null)
            clients.remove(target);

        Core.Player.Player targetPlayer = null;
        for (Core.Player.Player p : gameState.getPlayers()) {
            if (p.getPlayerId().equals(playerId)) {
                targetPlayer = p;
                break;
            }
        }
        if (targetPlayer != null)
            gameState.getPlayers().remove(targetPlayer);

        System.out.println("Player removed: " + playerId);
        broadcast(gameState.generateSyncData());
    }

    // -------------------------------------
    //         server action method
    // -------------------------------------

    private synchronized void onJoinGame(JSONObject action,String playerId){
        // กันชื่อแตก ถ้าเกิดหาชื่อไม่เจอ ใช้ Unknown- id 4 ตัว
        String pName = action.optString("playerName", "Unknown-" + playerId.substring(0, 4));

        // สร้างตัวละครแล้วเพิ่มลงใน gameState
        Player newPlayer = new Player(playerId, pName, false);
        gameState.getPlayers().add(newPlayer);
        System.out.println(pName + " (" + playerId + ") joined the game.");

        // ส่งข้อมูลยืนยันรับเข้าห้องให้คนที่เข้ามา
        JSONObject ack = new JSONObject();
        ack.put("type", NetworkProtocol.JOIN_ACK.name());
        ack.put("assignedId", playerId);
        sendToClient(playerId, ack);

        broadcast(gameState.generateSyncData());
    }
    private synchronized void onUpdatePlayerData(String playerId, JSONObject action){
        //update game state player datas
        for (Player player : gameState.getPlayers()){
            if(player.getPlayerId().equals(playerId)){
                player.updateFromJSON(action);
                System.out.println("Server : update player data \n" + player.toString());
            }
        }
        action.put("type", NetworkProtocol.UPDATE_PLAYER.name());
        broadcast(action);//send to all clients to syn player data
    }
    private synchronized void onStartGame() {
        System.out.println("START_GAME all clients");
        org.json.JSONObject startPacket = new org.json.JSONObject();
        startPacket.put("type", NetworkProtocol.START_GAME.name());
        startPacket.put("mapSeed", gameState.getMapSeed());
        gameState.onStartGame();
        broadcast(startPacket);
        broadcast(gameState.generateSyncData());
    }
    private synchronized void onPong(String playerId){
        // อัพเดตเวลา PONG ของ client คนนี้
        for (ClientHandler c : clients) {
            if (c.getPlayerId() != null && c.getPlayerId().equals(playerId)) {
                c.updatePongTime();
                break;
            }
        }
    }
    public synchronized void onNextTurn() {
        gameState.nextTurn();
        gameState.incrementPhaseCounter();
        updateGameStateToClients();
    }

    // อัปเดตข้อมูลเมือง
    private synchronized void onUseCard(JSONObject action) {
        JSONObject relay = new JSONObject(action.toString());
        relay.put("type", NetworkProtocol.SYNC_STATE.name());
        broadcast(relay);
    }
    private synchronized void updateGameStateToClients(){
        broadcast(gameState.generateSyncData());
    }
}
