package Core.Network.Client;

import Core.Network.NetworkProtocol;
import Core.Player.Player;
import org.json.JSONObject;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameClientManager {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Player localPlayer;
    private List<Player> connectedPlayers = new ArrayList<>();

    public List<Player> getConnectedPlayers() {
        return connectedPlayers;
    }

    public void connect(String ip, int port, String playerName) {
        try {
            socket = new Socket(ip, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            JSONObject joinPacket = new JSONObject();
            joinPacket.put("actionType", NetworkProtocol.JOIN.name());
            joinPacket.put("playerName", playerName);
            sendAction(joinPacket);
            
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

    public void onDataReceived(JSONObject data) {
        if (data.has("type")) {
            String type = data.getString("type");

            if (type.equals(NetworkProtocol.JOIN_ACK.name())) {
                String assignedId = data.getString("assignedId");
                this.localPlayer = new Player(assignedId, "Me", true);
                System.out.println("Join success My ID: " + assignedId);
            }
            else if (type.equals(NetworkProtocol.SYNC_STATE.name())) {
                int phase = data.optInt("phaseCounter", 1);
                System.out.println("Client sync Phase: " + phase);

                if (data.has("players")) {
                    org.json.JSONArray playersArray = data.getJSONArray("players");
                    connectedPlayers.clear();
                    for (int i = 0; i < playersArray.length(); i++) {
                        JSONObject pData = playersArray.getJSONObject(i);
                        String pId = pData.getString("playerId");

                        Player p = new Player(pId, pData.optString("playerName", "Unknown"), false);
                        connectedPlayers.add(p);

                        if (localPlayer != null && pId.equals(localPlayer.getPlayerId())) {
                            localPlayer.updateFromJSON(pData);
                        }
                    }
                }
            }
            else if (type.equals(NetworkProtocol.START_GAME.name())) {
                System.out.println("Host start game");
                // เริ่มเกม
                ZhuzheeEngine.Screen.ChangeScreen(Core.ZhuzheeGame.MAIN_SCENE);
            }
        }
    }

    public void sendAction(JSONObject action) {
        if (out != null) {
            out.println(action.toString());
        }
    }
}