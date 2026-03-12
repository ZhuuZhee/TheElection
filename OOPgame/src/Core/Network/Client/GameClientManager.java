package Core.Network.Client;

import Core.Player.Player;
import org.json.JSONObject;
import java.io.*;
import java.net.Socket;

public class GameClientManager {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Player localPlayer;

    public void connect(String ip, int port, String playerName) {
        try {
            socket = new Socket(ip, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            JSONObject joinPacket = new JSONObject();
            joinPacket.put("actionType", "JOIN");
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

            if (type.equals("JOIN_ACK")) {
                String assignedId = data.getString("assignedId");
                this.localPlayer = new Player(assignedId, "Me", true);
                System.out.println("Join success My ID: " + assignedId);
            }
            else if (type.equals("SYNC_STATE")) {
                int phase = data.optInt("phaseCounter", 1);
                System.out.println("Client sync Phase: " + phase);

                if (data.has("players")) {
                    org.json.JSONArray playersArray = data.getJSONArray("players");
                    for (int i = 0; i < playersArray.length(); i++) {
                        JSONObject pData = playersArray.getJSONObject(i);
                        String pId = pData.getString("playerId");

                        if (localPlayer != null && pId.equals(localPlayer.getPlayerId())) {
                            localPlayer.updateFromJSON(pData);
                        }
                    }
                }
            }
        }
    }

    public void sendAction(JSONObject action) {
        if (out != null) {
            out.println(action.toString());
        }
    }
}