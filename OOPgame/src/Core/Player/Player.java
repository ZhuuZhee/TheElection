package Core.Player;

import Core.Cards.*;
import Core.Cards.Stream.ArcanaCardName;
import Core.Cards.Stream.CardBufferObject;
import Core.Cards.Stream.CardReader;
import Core.Network.PacketBuilder;
import Core.UI.CardHolderUI;
import Core.ZhuzheeGame;
import ZhuzheeEngine.Application;
import ZhuzheeEngine.Scene.GameObject;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Player {
    private final String playerId;
    private String playerName;
    private final boolean isLocal;
    private int coin;
    private ArrayList<ActionCard> actionCards;
    private ArrayList<PolicyCard> policyCards;
    private ArcanaCard arcanaCard;
    private String arcanaCardName = "";
    private String[] cityOwn;
    private Color color = Color.RED;
    private String colorName = "Red";
    private String profileImagePath;
    private boolean isLose;//แพ้ป่าว
    private boolean skipDrawNextTurn = false; // ตัวแปรสำหรับสถานะห้ามจั่วการ์ด
    private float score;

    // เพิ่มตัวแปรสำหรับสกิล Judgement
    private boolean isPolicySilenced = false;
    private ArrayList<Core.Cards.Card> silencedPolicyCards = new ArrayList<>();

    public static final String DEFAULT_PROFILE_FILE = "1Pro.png";

    public static final int DEFAULT_DRAW_DEV_CARD_AMOUNT = 4;
    public static final Map<String, Color> COLOR_MAP = new HashMap<>();
    static {
        COLOR_MAP.put("Pink", Color.PINK);
        COLOR_MAP.put("Red", Color.RED);
        COLOR_MAP.put("Blue", Color.BLUE);
        COLOR_MAP.put("Green", Color.GREEN);
        COLOR_MAP.put("Yellow", Color.YELLOW);
        COLOR_MAP.put("Orange", Color.ORANGE);
        COLOR_MAP.put("Purple", Color.MAGENTA);
        COLOR_MAP.put("Cyan", Color.CYAN);
        COLOR_MAP.put("Black", Color.BLACK);
    }

    private Player(String playerId, String playerName, boolean isLocal, String color,
                  String profileImagePath, String arcanaCardName) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.isLocal = isLocal;
        this.coin = 10; // setไว้ 100 ก่อน
        this.actionCards = new ArrayList<>();
        this.policyCards = new ArrayList<>();
        this.cityOwn = new String[0];
        this.profileImagePath = profileImagePath;
        this.arcanaCardName = arcanaCardName;
    }

    public Player(String playerId, String playerName, boolean isLocal) {
        this(playerId, playerName, isLocal, "Red", DEFAULT_PROFILE_FILE, ArcanaCardName.THE_FOOL);
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getProfileImagePath() {return profileImagePath;}
    public File getProfileImageFile(){
        File file = new File(ZhuzheeGame.PROFILE_FILE_PATH,profileImagePath);
        if(!file.exists()){
            IOException io = new IOException("player profile filepath{%s} not found!".formatted(file.getAbsolutePath()));
            System.err.println(io);
        }
        return file;
    }

    public String getArcanaCardName() {
        return arcanaCardName;
    }

    public void setArcanaCard(ArcanaCard arcanaCard) {
        this.arcanaCard = arcanaCard;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
        if (isLocal) {
            if (ZhuzheeGame.PLAYER_COIN_UI != null) {
                ZhuzheeGame.PLAYER_COIN_UI.updateCoinDisplay();
            }
            if (ZhuzheeGame.PLAYER_PROFILE_UI != null) {
                ZhuzheeGame.PLAYER_PROFILE_UI.updateProfile();
            }
            if (ZhuzheeGame.PLAYER_LIST_UI != null) {
                ZhuzheeGame.PLAYER_LIST_UI.updatePlayerList();
            }
            if (ZhuzheeGame.CLIENT != null) {
                ZhuzheeGame.CLIENT.sendAction(Core.Network.PacketBuilder.createPlayerDataPacket(
                        playerId, playerName, coin, colorName, profileImagePath, arcanaCardName
                ));
            }
        }
    }

    public Color getColor() { return color; }

    public void setColor(Color color) { this.color = color; }
    public void setColor(String colorName){
        this.color = getColor(colorName);
        this.colorName = colorName;
    }
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean isLose() {
        return isLose;
    }

    public void setLose(boolean lose) {
        isLose = lose;
        if(lose){
            onLoose();
        }
    }

    private void onLoose(){
        //ปิด card holder ทั้งหมด และ ทำลายการ์ด
        //เปลี่ยน playerList UI เป็นสีเทา
        Core.UI.UINotificationToast.showNotification("You lost the election! Better luck next time.", 5000);

        ArrayList<Card> removedCards = new ArrayList<>();
        removedCards.addAll(ZhuzheeGame.ARCANA_CARD_UI.removeAllCards());
        ZhuzheeGame.ARCANA_CARD_UI.setVisible(false);

        removedCards.addAll(ZhuzheeGame.POLICY_CARD_HAND.removeAllCards());
        ZhuzheeGame.POLICY_CARD_HAND.setVisible(false);

        removedCards.addAll(ZhuzheeGame.DEVLOPMENT_CARD_HAND.removeAllCards());
        ZhuzheeGame.DEVLOPMENT_CARD_HAND.setVisible(false);

        ZhuzheeGame.END_TURN_UI.setVisible(false);
        ZhuzheeGame.CLIENT.sendAction(PacketBuilder.createUpdatePlayerPacket(this));

        for(Card card : removedCards){
            GameObject.Destroy(card);
        }
    }

    public void setSkipDrawNextTurn(boolean skip) {
        this.skipDrawNextTurn = skip;
    }

    public void onStartTurn() {
        if (isLocal) {
            if (skipDrawNextTurn) {
                System.out.println("⚡ [The Tower Effect] You cannot draw cards this turn!");
                skipDrawNextTurn = false; // เคลียร์สถานะหลังจากโดนข้ามไปแล้ว 1 เทิร์น
            } else {
                drawCard();
            }
        }
    }

    public void onEndTurn(){

    }

    public void applyJudgementPenalty() {
        if (!isLocal) return;

        // 1. ลบการ์ด Action ออกครึ่งมือ (ปัดเศษลง)
        if (ZhuzheeGame.DEVLOPMENT_CARD_HAND != null) {
            java.util.List<Core.Cards.Card> devCards = new ArrayList<>(ZhuzheeGame.DEVLOPMENT_CARD_HAND.getCards());
            int cardsToRemove = devCards.size() / 2;
            Random random = new Random();
            for (int i = 0; i < cardsToRemove; i++) {
                if (devCards.isEmpty()) break;
                int index = random.nextInt(devCards.size());
                Core.Cards.Card card = devCards.remove(index);
                ZhuzheeGame.DEVLOPMENT_CARD_HAND.removeCard(card);
                ZhuzheeEngine.Scene.GameObject.Destroy(card);
            }
        }

        // 2. ห้ามจั่วเทิร์นหน้า
        setSkipDrawNextTurn(true);

        // 3. ดูด Policy Card ออกจากมือไปเก็บไว้ชั่วคราว
        if (ZhuzheeGame.POLICY_CARD_HAND != null) {
            silencedPolicyCards.clear();
            java.util.List<Core.Cards.Card> policies = new ArrayList<>(ZhuzheeGame.POLICY_CARD_HAND.getCards());
            for (Core.Cards.Card p : policies) {
                silencedPolicyCards.add(p);
                ZhuzheeGame.POLICY_CARD_HAND.removeCard(p);
            }
            ZhuzheeGame.POLICY_CARD_HAND.revalidate();
            ZhuzheeGame.POLICY_CARD_HAND.repaint();
        }
        
        isPolicySilenced = true;
    }

    public void drawCard() {
        // เช็ค
        if (ZhuzheeGame.DEVLOPMENT_CARD_HAND != null && ZhuzheeGame.DEVLOPMENT_CARD_HAND.isFull()) {
            System.out.println("Hand is full, skipping draw.");
            return;
        }

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

    public void useCard() {
        score = ZhuzheeGame.MAP.getPlayerScore(playerId);
        //update player to server for sync data
        JSONObject packet = PacketBuilder.createUpdatePlayerPacket(this);
        packet.put("debug","Update Player Score");
        ZhuzheeGame.CLIENT.sendAction(packet);
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("playerId", playerId);
        json.put("playerName", playerName);
        json.put("coin", coin);
        json.put("score", score);
        json.put("color", colorName);
        json.put("profileImagePath", profileImagePath);
        json.put("isLose", isLose);
        if (arcanaCard != null) {
            json.put("arcanaCard", arcanaCard.getName());
        } else if (arcanaCardName != null && !arcanaCardName.isEmpty()) {
            json.put("arcanaCard", arcanaCardName);
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
        if (data.has("score")) {
            this.score = (float) data.getDouble("score");
        }
        if (data.has("color")) {
            String colorName = data.getString("color");
            setColor(colorName);
        }
        if (data.has("profileImagePath")) {
            this.profileImagePath = data.getString("profileImagePath");
        }
        if (data.has("cityOwn")) {
            org.json.JSONArray cityArray = data.getJSONArray("cityOwn");
            this.cityOwn = new String[cityArray.length()];
            // ค่อยๆ
        }
        if (data.has("arcanaCard")) {
            this.arcanaCardName = data.getString("arcanaCard");
        }
        if(data.has("isLose")){
            isLose = data.getBoolean("isLose");
        }

        if (ZhuzheeGame.PLAYER_LIST_UI != null) {
            ZhuzheeGame.PLAYER_LIST_UI.updatePlayerList();
        }

        System.out.println("Player{%s} : update data form json successfully!\n%s".formatted(playerName, toString()));
    }

    @Override
    public String toString() {
        return "Player{%s} : Color(%s), ProfileImagePath(%s), score(%f), isLose(%b)".formatted(playerName,colorName,profileImagePath,score,isLose);
    }

    public boolean isLocal() {
        return isLocal;
    }

    private Color getColor(String colorName) {
        return COLOR_MAP.getOrDefault(colorName, Color.BLACK);
    }

    public float getScore() {
        return score;
    }
}
