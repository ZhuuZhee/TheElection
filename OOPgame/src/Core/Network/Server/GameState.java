package Core.Network.Server;

import Core.Player.Player;
import org.json.JSONObject;
import java.util.*;

public class GameState {
//    private Map
    private List<Player> players = new ArrayList<>();
    private int phaseCounter = 1;
    private String hostId;
    private String currentPlayerId;

    public void incrementPhaseCounter() {
        this.phaseCounter++;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public String getCurrentPlayerId() {
        return currentPlayerId;
    }

    public void setCurrentPlayerId(String currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }

    public void reorderPlayers() {
        // players.sort ???
    }

    public JSONObject generateSyncData() {
        JSONObject data = new JSONObject();
        data.put("type", "SYNC_STATE");
        data.put("phaseCounter", phaseCounter);
        if (hostId != null) {
            data.put("hostId", hostId);
        }
        if (currentPlayerId != null) {
            data.put("currentPlayerId", currentPlayerId);
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
}
