package Core.Network.Server;

import org.json.JSONObject;
import java.util.*;

public class GameState {
//    private Map
//    private List<Player> players = new ArrayList<>();
    private int phaseCounter = 1;
    private String hostId;

    public void reorderPlayers() {
        // players.sort ???
    }

    public JSONObject generateSyncData() {
        return null;
    }

    public String getHostId() {
        return hostId;
    }
}
