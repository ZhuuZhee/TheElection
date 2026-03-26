package Core.Player;

import Core.Cards.*;
import org.json.JSONObject;

import java.util.*;

public class Player {
    private String playerId;
    private String playerName;
    private boolean isLocal;
    private int coin;
    private ArrayList<ActionCard> actionCards;
    private ArrayList<PolicyCard> policyCards;
    private ArcanaCard arcanaCard;
    private String[] cityOwn;
    private String color;
    private String profileImagePath;

    public Player(String playerId, String playerName, boolean isLocal, String color, String profileImagePath, ArcanaCard arcanaCard) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.isLocal = isLocal;
        this.coin = 100; // setไว้ 100 ก่อน
        this.actionCards = new ArrayList<>();
        this.policyCards = new ArrayList<>();
        this.cityOwn = new String[0];
        this.color = color;
        this.profileImagePath = profileImagePath;
        this.arcanaCard = arcanaCard;
    }

    public Player(String playerId, String playerName, boolean isLocal) {
        this(playerId, playerName, isLocal, "Red", "default_profile.png", null);
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getCoin() { return coin; }

    public void setCoin(int coin) { this.coin = coin; }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void DrawCard() {
    }

    public void UseCard() {
    }

    public void getCityOwn() {
    }

    public void setCityOwn() {
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("playerId", playerId);
        json.put("playerName", playerName);
        json.put("coin", coin);
        json.put("color", color);
        json.put("profileImagePath", profileImagePath);
        if (arcanaCard != null) {
            json.put("arcanaCard", arcanaCard.getName());
        }

        org.json.JSONArray cityArray = new org.json.JSONArray();
        for (String city : cityOwn) {
            cityArray.put(city);
        }
        json.put("cityOwn", cityArray);

        org.json.JSONArray actionArray = new org.json.JSONArray();
        for (ActionCard card : actionCards) {
            actionArray.put(card.getName());
        }
        json.put("actionCards", actionArray);

        org.json.JSONArray policyArray = new org.json.JSONArray();
        for (PolicyCard card : policyCards) {
            policyArray.put(card.getName());
        }
        json.put("policyCards", policyArray);

        return json;
    }

    public void updateFromJSON(JSONObject data) {
        if (data.has("playerName")) {
            this.playerName = data.getString("playerName");
        }
        if (data.has("coin")) {
            this.coin = data.getInt("coin");
        }
        if (data.has("color")) {
            this.color = data.getString("color");
        }
        if (data.has("profileImagePath")) {
            this.profileImagePath = data.getString("profileImagePath");
        }
        if (data.has("cityOwn")) {
            org.json.JSONArray cityArray = data.getJSONArray("cityOwn");
            this.cityOwn = new String[cityArray.length()];
            // ค่อยๆ
        }
    }
}
