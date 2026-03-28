package Core.Network.Server;

import Core.Maps.City;
import Core.Network.NetworkProtocol;
import Core.Player.Player;
import org.json.JSONObject;

import java.util.*;

public class GameState {
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
    public void updateFormJSON(JSONObject data) {
        if (data == null) return;

        if (data.has("turnCounter")) {
            this.turnCounter = data.getInt("turnCounter");
        }

        if (data.has("hostId")) {
            this.hostId = data.getString("hostId");
        }

        if (data.has("players")) {
            org.json.JSONArray playersArray = data.getJSONArray("players");
            for (int i = 0; i < playersArray.length(); i++) {
                JSONObject pData = playersArray.getJSONObject(i);
                String pId = pData.getString("playerId");
                float score = pData.getFloat("player score");

                // ค้นหาผู้เล่นเดิมหรือสร้างใหม่ถ้าไม่พบ
                Player player = players.stream()
                        .filter(p -> p.getPlayerId().equals(pId))
                        .findFirst()
                        .orElse(null);

                if (player == null) {
                    player = new Player(pId, pData.optString("playerName", "Unknown"), false);
                    players.add(player);
                }
                player.updateFromJSON(pData);
            }
        }

        if (data.has("currentPlayerId")) {
            String cId = data.getString("currentPlayerId");
            this.currentPlayer = players.stream()
                    .filter(p -> p.getPlayerId().equals(cId))
                    .findFirst()
                    .orElse(null);
        }
    }
    public String getHostId() {
        return hostId;
    }


    //----------------------------------------
    // --------   game state events method
    //-----------------------------------------

    public synchronized void onStartTurn() {
        if (players.isEmpty()) return;

        // ค้นหาผู้เล่นคนแรกที่ยังไม่แพ้
        Player startPlayer = players.stream()
                .filter(p -> !p.isLose())
                .findFirst()
                .orElse(players.getFirst());

        setCurrentPlayer(startPlayer);
        // ไม่ต้องเรียก OnStartTurn ที่นี่ เพราะ Client จะเริ่มเทิร์นเองเมื่อ SYNC_STATE มาถึง
    }

    public synchronized void nextTurn() {
        if (players.isEmpty() || currentPlayer == null) return;

        int currentIndex = players.indexOf(currentPlayer);
        for (int i = 1; i <= players.size(); i++) {
            int nextIndex = (currentIndex + i) % players.size();
            Player candidate = players.get(nextIndex);
            
            // ถ้าผู้เล่นคนนี้ยังไม่แพ้ ให้เป็นคนเล่นคนต่อไป
            if (!candidate.isLose()) {
                setCurrentPlayer(candidate);
                return;
            }
        }
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
