package Core.Network.Client;

import Core.Player.Player;
import org.json.JSONObject;
import java.io.*;
import java.net.Socket;

public class GameClientManager {
    private Socket socket;
    private PrintWriter out;
    private Player localPlayer;

    public void connect(String ip, int port) {
    }

    public void onDataReceived(JSONObject data) {
    }

    public void sendAction(JSONObject action) {
        out.println(action.toString());
    }
}