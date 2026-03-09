package Core.Network.Server;

import org.json.JSONObject;
import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    private GameServerManager server;
    private String playerId;

    public ClientHandler(Socket s, GameServerManager svr) {
        this.socket = s;
        this.server = svr;
    }

    public void run() {
    }

    public void sendMessage(JSONObject mes) {
        output.println(mes.toString());
    }

    public String getPlayerId() {
        return playerId;
    }
}
