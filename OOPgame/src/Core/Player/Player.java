package Core.Player;

import Core.Cards.*;
import org.json.JSONObject;

import java.util.*;

public class Player {
    private String playerId;
    private boolean isLocal;
    private int coin;
    private ArrayList<ActionCard> actionCards;
    private ArrayList<PassiveCard> passiveCards;
    private String[] cityOwn;
//    private ActionCardHolderUI: CardHolderUI  ???

    public Player(String playerId, boolean isLocal) {
        this.playerId = playerId;
        this.isLocal = isLocal;
        this.coin = 100; // setไว้ 100 ก่อน
        this.actionCards = new ArrayList<>();
        this.passiveCards = new ArrayList<>();
        this.cityOwn = new String[0];
    }

//    method
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
        json.put("coin", coin);
//        json.put("cityOwn", Arrays.asList(cityOwn));
        return json;
    }

    public void updateFromJSON() {
    }
}
