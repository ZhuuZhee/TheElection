package Core.Network.Client;

import Core.Network.NetworkProtocol;
import Core.Network.PacketBuilder;
import Core.Player.Player;
import Core.Maps.City;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.HashSet;
import java.util.List;

public class GameClientManager {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Player localPlayer;
    private String currentPlayerId;
    private final ArrayList<Player> connectedPlayers = new ArrayList<>();
    private int turnCounter;
    private final HashSet<ClientListener> clientListeners = new HashSet<>();

    public List<Player> getConnectedPlayers() {
        return connectedPlayers;
    }

    public int getLocalPlayerIndex() {
        if (localPlayer == null) return 0;
        String id = localPlayer.getPlayerId();
        for (int i = 0; i < connectedPlayers.size(); i++) {
            if (connectedPlayers.get(i).getPlayerId().equals(id)) return i;
        }
        return 0;
    }

    public void connect(String ip, int port, String playerName) {
        try {
            socket = new Socket(ip, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            sendAction(PacketBuilder.createJoinPacket(playerName));

            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        JSONObject data = new JSONObject(message);
                        onDataReceived(data);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendAction(JSONObject action) {
        if (out != null) {
            out.println(action.toString());
        }
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public Player getLocalPlayer() {
        return localPlayer;
    }

    public void endTurn() {
        sendAction(PacketBuilder.createEndTurnPacket());
    }

    public void onDataReceived(JSONObject data) {
        if (data.has("type")) {
            String type = data.getString("type");

            if (!type.equals(NetworkProtocol.PING.name()))
                System.out.println("Client : Received %s".formatted(data.toString()));

            if (type.equals(NetworkProtocol.JOIN_ACK.name())) {
                onJoinAcknowledge(data);
            } else if (type.equals(NetworkProtocol.SYNC_STATE.name())) {
                onSyncGameState(data);
            } else if (type.equals(NetworkProtocol.START_GAME.name())) {
                onStartGame(data);
            } else if (type.equals(NetworkProtocol.HOST_LEFT.name())) {
                onHostLeft();
            } else if (type.equals(NetworkProtocol.PING.name())) {
                onPing();
            } else if (type.equals(NetworkProtocol.UPDATE_PLAYER.name())) {
                onUpdatePlayer(data);
            }
        }
    }

    public int getTurnCounter() {
        return turnCounter;
    }

    public void setTurnCounter(int turnCounter) {
        this.turnCounter = turnCounter;
    }

    public String getCurrentPlayerId() {
        return currentPlayerId;
    }

    //----------------------------------------
    //---- player action on received data ----
    //----------------------------------------

    private synchronized void onJoinAcknowledge(JSONObject data) {
        String assignedId = data.getString("assignedId");
        this.localPlayer = new Player(assignedId, "Me", true);
        System.out.println("Join success My ID: " + assignedId);

        // Notify listeners
        for (ClientListener listener : clientListeners) {
            listener.onJoinAcknowledge();
        }
    }

    private synchronized void onSyncGameState(JSONObject data) {
        // cuurentPlayerId คือ ไอดีของผู้เล่นที่มีเทิร์นตอนนี้
        if (data.has("currentPlayerId")) {
            int turn = data.optInt("turnCounter", 1);
            String currentPlayerId = data.optString("currentPlayerId", "");
            this.currentPlayerId = currentPlayerId;
            System.out.println("Client sync Phase: " + turn + ", CurrentPlayer: " + currentPlayerId);
            turnCounter = turn;
            boolean myTurnNow = (localPlayer != null && currentPlayerId.equals(localPlayer.getPlayerId()));
            // ถ้าเป็นเทิร์นของเรา ให้เริ่มเทิร์น (จั่วการ์ด)
            if (myTurnNow && localPlayer != null) {
                localPlayer.OnStartTurn();
            }
        }
        // ตรวจสอบว่าเป็นเทิร์นของเราหรือไม่

        if (data.has("players")) {
            org.json.JSONArray playerDatasArray = data.getJSONArray("players");

            // สร้าง Map ชั่วคราวเพื่อเก็บ Player ที่มีอยู่แล้ว เพื่อการค้นหาที่เร็วขึ้น
            java.util.Map<String, Player> existingPlayersMap = new java.util.HashMap<>();
            for (Player p : this.connectedPlayers) {
                existingPlayersMap.put(p.getPlayerId(), p);
            }

            // ล้าง connectedPlayers เดิมออก และจะสร้างใหม่จากข้อมูลที่ได้รับจาก Server
            this.connectedPlayers.clear();

            for (int i = 0; i < playerDatasArray.length(); i++) {
                JSONObject pData = playerDatasArray.getJSONObject(i);
                String pId = pData.getString("playerId");

                Player playerToUpdate = existingPlayersMap.get(pId);
                if (playerToUpdate == null) {
                    // ถ้าเป็นผู้เล่นใหม่ ให้สร้าง Player object ใหม่
                    playerToUpdate = new Player(pId, pData.optString("playerName", "Unknown"), pId.equals(localPlayer != null ? localPlayer.getPlayerId() : ""));
                }

                playerToUpdate.updateFromJSON(pData); // อัปเดตข้อมูลผู้เล่นจาก JSON
                this.connectedPlayers.add(playerToUpdate); // เพิ่มผู้เล่น (ที่อัปเดตแล้วหรือใหม่) เข้าไปใน Set

                // หากเป็น localPlayer ของเรา ให้อัปเดต reference ด้วย
                if (localPlayer != null && pId.equals(localPlayer.getPlayerId())) {
                    this.localPlayer = playerToUpdate;
                }
            }
        }

        // Sync ข้อมูลเมืองที่มาจาก USE_CARD
        if (data.has("city") && Core.ZhuzheeGame.MAP != null) {
            JSONObject cityData = data.getJSONObject("city");
            String cityName = cityData.optString("name", "");
            City target = Core.ZhuzheeGame.MAP.getCityByName(cityName);
            if (target != null) {
                target.updateFromJson(cityData);
            }
        }

        // Notify listeners
        for (ClientListener listener : clientListeners) {
            listener.onSyncGameState();
        }
    }

    private synchronized void onStartGame(JSONObject data) {
        System.out.println("Host start game");
        if (data.has("mapSeed")) {
            Core.ZhuzheeGame.MAP_SEED = data.getLong("mapSeed");
        }

        System.out.println("Client : All players data logs");
        for (Player p : connectedPlayers) {
            System.out.println(p.toString());
        }
        System.out.println("-------------------------------");

        // Notify listeners
        for (ClientListener listener : clientListeners) {
            listener.onStartGame();
        }

        // เริ่มเกม
        javax.swing.SwingUtilities.invokeLater(Core.ZhuzheeGame::startMainScene);
    }

    private synchronized void onHostLeft() {
        System.out.println("Host left the room");

        // Notify listeners before cleanup
        for (ClientListener listener : clientListeners) {
            listener.onHostLeft();
        }

        Core.ZhuzheeGame.CLIENT = null;
        // กลับ LOBBY_MENU
        ZhuzheeEngine.Screen.ChangeScreen(Core.ZhuzheeGame.LOBBY_MENU);
    }

    private synchronized void onPing() {
        JSONObject pong = PacketBuilder.createPongPacket();
        sendAction(pong);
    }

    private synchronized void onUpdatePlayer(JSONObject data) {
        String pId = data.optString("playerId", ""); // แก้ไข: ใช้ "playerId" แทน "currentPlayerId"
        if (pId.isEmpty()) {
            System.err.println("Client : Received UPDATE_PLAYER packet without playerId.");
            return;
        }

        System.out.println("Client : Updating Player Data for ID %s".formatted(pId));

        // อัปเดต localPlayer ถ้า packet นี้เป็นของตัวเราเอง
        if (localPlayer != null && localPlayer.getPlayerId().equals(pId)) {
            localPlayer.updateFromJSON(data);
            System.out.println("Client : Updated local player: " + localPlayer.toString());
        }

        // อัปเดตผู้เล่นคนอื่นๆ ใน connectedPlayers
        // สร้าง List ชั่วคราวเพื่อหลีกเลี่ยง ConcurrentModificationException
        List<Player> playersToUpdate = new ArrayList<>(connectedPlayers);
        for (Player p : playersToUpdate) {
            if (p.getPlayerId().equals(pId)) {
                p.updateFromJSON(data);
                System.out.println("Client : Updated connected player: " + p.toString());
                break; // พบผู้เล่นแล้ว ไม่ต้องวนลูปต่อ
            }
        }

        // Notify listeners
        for (ClientListener listener : clientListeners) {
            listener.onUpdatePlayer();
        }
    }

    //--------------------------------
    //--------- event handler --------
    //--------------------------------

    public void addClientListener(ClientListener listener) {
        if (listener != null) {
            clientListeners.add(listener);
        }
    }

    public void removeClientListener(ClientListener listener) {
        clientListeners.remove(listener);
    }
}