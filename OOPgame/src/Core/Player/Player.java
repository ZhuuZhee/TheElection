package Core.Player;

import Core.Cards.*;
import Core.Cards.Stream.CardBufferObject;
import Core.Cards.Stream.CardReader;
import Core.UI.CardHolderUI;
import Core.ZhuzheeGame;
import ZhuzheeEngine.Application;
import org.json.JSONObject;

import java.awt.*;
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
    private Color color;
    private String profileImagePath;

    public static final int DEFAULT_DRAW_DEV_CARD_AMOUNT = 4;

    public Player(String playerId, String playerName, boolean isLocal, String color, String profileImagePath, ArcanaCard arcanaCard) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.isLocal = isLocal;
        this.coin = 100; // setไว้ 100 ก่อน
        this.actionCards = new ArrayList<>();
        this.policyCards = new ArrayList<>();
        this.cityOwn = new String[0];
        this.color = Color.decode(color);
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

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public Color getColor() { return color; }

    public void setColor(Color color) { this.color = color; }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void OnStartTurn() {
        DrawCard();
    }

    private void DrawCard() {
        CardBufferObject[] cardBufferObjects = DrawActionCardBufferObjects(DEFAULT_DRAW_DEV_CARD_AMOUNT);

        new Thread(() -> {
            while (true) {
                if (ZhuzheeGame.MAIN_SCENE != null && ZhuzheeGame.DEVLOPMENT_CARD_HAND != null) {
                    CardHolderUI cardHolderUI = ZhuzheeGame.DEVLOPMENT_CARD_HAND;
                    for (CardBufferObject cardBuffer : cardBufferObjects) {
                        cardHolderUI.addCard(new ActionCard(cardBuffer, 0, 0));
                    }
                    break;
                } else {
                    try {
                        Thread.sleep(Application.DELTA_TIME_MS);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    private CardBufferObject[] DrawActionCardBufferObjects(int amount) {
        ArrayList<CardBufferObject> cards = new ArrayList<>();
        List<CardBufferObject> loadedCars = CardReader.getLoadedCards();
        Random random = new Random();
        for (int i = 0; i < amount; i++) {
            CardBufferObject cardBuffer = loadedCars.get(random.nextInt(loadedCars.size()));
            cards.add(cardBuffer);
        }
        return cards.toArray(new CardBufferObject[0]);
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
            this.color = Color.decode(data.getString("color"));
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
