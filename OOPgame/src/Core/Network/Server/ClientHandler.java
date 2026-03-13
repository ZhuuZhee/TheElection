package Core.Network.Server;

import org.json.JSONObject;
import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class ClientHandler implements Runnable {
    private static final long PONG_TIMEOUT_MS = 10000;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    private GameServerManager server;
    private String playerId;
    private long lastPongTime = System.currentTimeMillis();

    public ClientHandler(Socket s, GameServerManager svr) {
        this.socket = s;
        this.server = svr;
        // สร้าง playerId แบบสุ่ม จาก java.util.UUID มันจะเป็นเลข 128 bit แบบ 123asd-12das1
        this.playerId = UUID.randomUUID().toString();
        try {
            this.output = new PrintWriter(socket.getOutputStream(), true);
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updatePongTime() {
        lastPongTime = System.currentTimeMillis();
    }

    public boolean isTimedOut() {
        return System.currentTimeMillis() - lastPongTime > PONG_TIMEOUT_MS;
    }

    public void run() {
        try {
            String message;
            while ((message = input.readLine()) != null) {
                JSONObject json = new JSONObject(message);
                server.processAction(playerId, json);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + playerId);
        } finally {
            server.removeClient(playerId);
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(JSONObject mes) {
        if (output != null) {
            output.println(mes.toString());
        }
    }

    public String getPlayerId() {
        return playerId;
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
