package Core.Network.Server;

import Core.Maps.City;
import Core.Network.NetworkProtocol;
import Core.Player.Player;
import org.json.JSONObject;

import java.util.*;

public class GameState {
    //    private Map
    private List<Player> players = new ArrayList<>();
    private int turnCounter = 1;
    private String hostId;
    private Player currentPlayer;
    private long mapSeed = new Random().nextLong();

    public long getMapSeed() {
        return mapSeed;
    }

    public void incrementPhaseCounter() {
        this.turnCounter++;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public int getTurnCounter() {
        return turnCounter;
    }

    public void setCurrentPlayer(Player player) {
        currentPlayer = player;
        System.out.println("Server : Current Player is " + player.getPlayerName() + "\n id : " + player.getPlayerId());
        playersLog();
    }

    public void reorderPlayers() {
        // players.sort ???
    }

    /// game state data as JSON for socket
    public JSONObject generateSyncData() {
        JSONObject data = new JSONObject();
        data.put("type", NetworkProtocol.SYNC_STATE.name());
        data.put("turnCounter", turnCounter);
        if (hostId != null) {
            data.put("hostId", hostId);
        }
        if (currentPlayer != null) {
            data.put("currentPlayerId", currentPlayer.getPlayerId());
        }

        org.json.JSONArray playersArray = new org.json.JSONArray();
        for (Player p : players) {
            playersArray.put(p.toJSON());
        }
        data.put("players", playersArray);

        return data;
    }

    public String getHostId() {
        return hostId;
    }


    //----------------------------------------

    /// --------   game state events method
    //-----------------------------------------
    public synchronized void onStartGame() {
        Player startPlayer = players.getFirst();
        setCurrentPlayer(startPlayer);
        // ไม่ต้องเรียก OnStartTurn ที่นี่ เพราะ Client จะเริ่มเทิร์นเองเมื่อ SYNC_STATE มาถึง
    }

    public synchronized void nextTurn() {
        int index = players.indexOf(currentPlayer);
        setCurrentPlayer(players.get((index + 1) % players.size()));
    }

    public void playersLog() {
        System.out.println("------ All Players -------");
        for (Player p : players) {
            System.out.println("Player : " + p.getPlayerName() + (p.isLocal() ? " this is Local" : ""));
        }
        System.out.println("Current Player is " + currentPlayer.getPlayerName());
        System.out.println("------ ----------- -------");
    }
}
