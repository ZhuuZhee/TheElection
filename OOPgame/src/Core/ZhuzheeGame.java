package Core;

import Core.Cards.Card;
import Core.GameScreens.CreditUI;
import Core.GameScreens.MainMenu;
import Core.GameScreens.OptionMenu;
import Core.Maps.Map;
import Core.Player.Player;
import Core.UI.*;
import Dummy.Tester;
import ZhuzheeEngine.Application;
import ZhuzheeEngine.ApplicationAdapter;
import ZhuzheeEngine.Audios.AudioManager;
import ZhuzheeEngine.Scene.Camera2D;
import ZhuzheeEngine.Scene.Scene2D;
import ZhuzheeEngine.Screen;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;

/// Game Logic Handler
public class ZhuzheeGame implements ApplicationAdapter {
    public static Scene2D MAIN_SCENE;
    public static MainMenu MAIN_MENU;

    public static Core.GameScreens.LobbyMenu LOBBY_MENU;
    public static Core.GameScreens.CreateRoomMenu CREATE_ROOM_MENU;
    public static Core.GameScreens.JoinRoomMenu JOIN_ROOM_MENU;
    public static Core.GameScreens.WaitingRoomMenu WAITING_ROOM_MENU;
    public static CreditUI CREDIT_UI;
    public static OptionMenu OPTION_MENU;
//    public static CharacterSelectMenu CHARACTER_SELECT_MENU;

    public static Core.Network.Server.GameServerManager SERVER;
    public static Core.Network.Client.GameClientManager CLIENT;

    public static Core.Maps.Map MAP;
    public static long MAP_SEED = new java.util.Random().nextLong();

    public static CardHolderUI DEVLOPMENT_CARD_HAND;
    public static PolicyCardHolderUI POLICY_CARD_HAND;
    public static ArcanaCardHolderUI ARCANA_CARD_UI;
    public static PlayerListUI PLAYER_LIST_UI;
    public static PlayerProfile PLAYER_PROFILE_UI;
    public static Core.UI.PlayerCoinUI PLAYER_COIN_UI;
    public static GameSettingUI SETTINGS_UI;
    public static Core.UI.TurnUI TURN_UI;

    public static List<Player> CURRENT_PLAYERS = new ArrayList<>();

    public static final String PROFILE_FILE_PATH = "OOPgame/Assets/ImageForProfile";
    public static final String CARD_IMAGES_FILE_PATH = "OOPgame/Assets/ImageForCards/Arcana Card";

    /// ตั้งเป็น true เพื่อ Run test ทันที, ตั้ง false เพื่อ Run Main
    public static final boolean DEV_MODE = false;

    public static MouseAdapter MOUSE_HOVER_SFX = new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            super.mouseEntered(e);
            AudioManager.getInstance().playSound("hover");
        }
    };

    public static boolean isMyTurn(){
        if(CLIENT != null){
            return CLIENT.getCurrentPlayerId().equals(CLIENT.getLocalPlayer().getPlayerId());
        }
        return false;
    }
    @Override
    public void create() {
        // set Application title
        Application.setMainFrameTitle("Zhuzhee The Game");

        // ตั้งค่า default font ของ Swing เป็น pixelfont
        try {
            Font pixelFont = Font.createFont(Font.TRUETYPE_FONT, new java.io.File("OOPgame/Assets/Fonts/pixelfont.ttf")).deriveFont(Font.PLAIN, 16f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(pixelFont);
            javax.swing.plaf.FontUIResource fontRes = new javax.swing.plaf.FontUIResource(pixelFont);
            for (Object key : javax.swing.UIManager.getLookAndFeelDefaults().keySet()) {
                if (key != null && key.toString().toLowerCase().contains("font")) {
                    javax.swing.UIManager.put(key, fontRes);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        MAIN_SCENE = new Scene2D();
        new Core.UI.GameLogUI(MAIN_SCENE); // เพิ่ม GameLogUI เข้าไปในซีนหลัก

        MAIN_MENU = new MainMenu();
        LOBBY_MENU = new Core.GameScreens.LobbyMenu();
        CREATE_ROOM_MENU = new Core.GameScreens.CreateRoomMenu();
        JOIN_ROOM_MENU = new Core.GameScreens.JoinRoomMenu();
        WAITING_ROOM_MENU = new Core.GameScreens.WaitingRoomMenu();
        CREDIT_UI = new CreditUI();
        OPTION_MENU = new OptionMenu();
//        CHARACTER_SELECT_MENU = new CharacterSelectMenu();

        if (DEV_MODE) {
            startMainScene(); // Run test ทันที
        } else {
            Screen.ChangeScreen(MAIN_MENU); // Run Main จริง
        }
        AudioManager.getInstance().loadSound("bgm", "UaH.WAV");
        // AudioManager.getInstance().playLoop("bgm");
    }

    /// MAIN_SCENE
    public static void startMainScene() {
        Screen.ChangeScreen(MAIN_SCENE);

        int playerCountForMap = 4;
        if (CLIENT != null && !CLIENT.getConnectedPlayers().isEmpty()) {
            playerCountForMap = CLIENT.getConnectedPlayers().size();
        }
        MAP = new Map(MAP_SEED, playerCountForMap);
        Tester.CardsTestingOnScene(MAIN_SCENE);
        CardHolderUI holderUI = PlayerUI.CardHolderUITest(MAIN_SCENE);
        DEVLOPMENT_CARD_HAND = holderUI;
        PlayerUI.PlayerCoinUITest(MAIN_SCENE);
        PlayerUI.PolicyCardHolderUITest(MAIN_SCENE);
        PlayerUI.ArcanaCardHolderUITest(MAIN_SCENE);
        PlayerUI.GameSettingUI(MAIN_SCENE);
        PlayerUI.TurnUITest(MAIN_SCENE);

        // Player List UI
        List<Player> actualPlayers = new ArrayList<>();
        if (CLIENT != null && !CLIENT.getConnectedPlayers().isEmpty()) {
            actualPlayers = CLIENT.getConnectedPlayers().stream().toList();
        } else if (DEV_MODE) {
            actualPlayers.add(new Player("1", "P'Few", true));
            actualPlayers.add(new Player("2", "Xynezter", false));
            actualPlayers.add(new Player("3", "Thana", false));
            actualPlayers.add(new Player("4", "KUY", false));
        }

        PLAYER_LIST_UI = new PlayerListUI(MAIN_SCENE, actualPlayers);
        CURRENT_PLAYERS = actualPlayers;
        PlayerUI.PlayerCoinUITest(MAIN_SCENE);
        Tester.CardTesterUI(MAIN_SCENE);

        Tester.ShopTest();
        CameraControlEvent(MAIN_SCENE);

        Player localPlayer = (CLIENT != null) ? CLIENT.getLocalPlayer() : null;

        if (localPlayer != null) {
            String cardName = localPlayer.getArcanaCardName();
            System.out.println("Get Player ArcanaCard{%s}".formatted(cardName));
            if (cardName != null && !cardName.isEmpty()) {
                Core.Cards.ArcanaCard card = Core.Cards.Stream.ArcanaCardRegistry.createCard(cardName);
                localPlayer.setArcanaCard(card); // เก็บไพ่จริงใส่ตัว Player
                ARCANA_CARD_UI.addCard(card);    // ยัดไพ่ลงกระดาน UI
            }
            // เอาไว้เปิด PlayerProfileUI
            PlayerUI.PlayerProfileUITest(MAIN_SCENE, localPlayer);
        }
        localPlayer.DrawCard();
    }

    private static final float MAX_ZOOM = 1.25f, MIN_ZOOM = 0.75f, NORMAL_ZOOM = 1;
    private static final Dimension CAMERA_BOUND = new Dimension(500, 500);
    private static Point mousePoint;

    public static void CameraControlEvent(Scene2D scene) {
        // ใช้ AWTEventListener เพื่อดักจับ Input ของเมาส์แบบ Global ไม่ว่าจะชี้อยู่บน
        // Map หรือ UI ตัวไหนก็จะขยับกล้องได้
        long eventMask = AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK;

        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            // เช็ค MouseWheel ก่อน เพราะมันสืบทอดมาจาก MouseEvent
            if (event instanceof MouseWheelEvent e) {
                var cam = MAIN_SCENE.getCamera();
                float zoom = cam.getZoom() - e.getWheelRotation() * ZhuzheeEngine.Application.getDeltaTime();
//                float zoomSpeed = 0.1f;
//                float zoom = cam.getZoom() - e.getWheelRotation() * zoomSpeed;
                zoom = Math.clamp(zoom, MIN_ZOOM, MAX_ZOOM);
                cam.setZoom(zoom);
            }
            // แล้วค่อยเช็ค MouseEvent ธรรมดา
            else if (event instanceof MouseEvent e) {
                // อัปเดตตำแหน่งตั้งต้นตอนเริ่มกดเมาส์กลาง
                if (e.getID() == MouseEvent.MOUSE_PRESSED && javax.swing.SwingUtilities.isRightMouseButton(e)) {
                    mousePoint = e.getLocationOnScreen();
                }
                // คอยขยับกล้องเวลาลากเมาส์กลาง
                else if (e.getID() == MouseEvent.MOUSE_DRAGGED && javax.swing.SwingUtilities.isRightMouseButton(e)) {
                    int dx = e.getLocationOnScreen().x - mousePoint.x;
                    int dy = e.getLocationOnScreen().y - mousePoint.y;
                    var cam = MAIN_SCENE.getCamera();

                    Point pos = cam.getPosition();
                    pos.x -= (int) (dx/ cam.getZoom());
                    pos.y -= (int) (dy/ cam.getZoom());

                    // camera bounding
                    int minX = -CAMERA_BOUND.width;
                    int maxX = CAMERA_BOUND.width;
                    int minY = -CAMERA_BOUND.height;
                    int maxY = CAMERA_BOUND.height;

                    pos.x = Math.clamp(pos.x, minX, maxX);
                    pos.y = Math.clamp(pos.y, minY, maxY);


                    cam.setPosition(pos);

                    mousePoint = e.getLocationOnScreen(); // ใช้ LocationOnScreen
                    // เพื่อป้องกันหน้าจอกระตุกเมื่อเมาส์ลากข้ามระหว่าง Component
                    MAIN_SCENE.repaint();
                }
            }
        }, eventMask);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
        if (MAIN_SCENE != null) sceneUpdate();
    }

    public void sceneUpdate() {
        checkRoundAndShop();
        if (TURN_UI != null) {
            TURN_UI.updateTurnDisplay();
        }
    }
    private static int lastShopOpenedRound = -1;

    public static void checkRoundAndShop() {
        if (CLIENT == null) return;

        int currentTurn = CLIENT.getTurnCounter();
        int playerCount = Math.max(1, CURRENT_PLAYERS.size());
        int turnsPerRound = playerCount * 4;

        int currentRound = ((currentTurn - 1) / turnsPerRound) + 1;

        if (currentTurn > 1 && currentTurn % turnsPerRound == 1) {

            if (currentRound != lastShopOpenedRound) {
                ZhuzheeEngine.Debug.GameLogger.logInfo("====== START OF ROUND " + currentRound + "! OPENING SHOP ======");

                Dummy.Tester.ShopTest();

                lastShopOpenedRound = currentRound;
            }
        }
    }

    private static void applyRandomEvent(int round) {
        if (MAP == null) return;
        List<Core.Maps.City> cities = MAP.getAllCities();
        if (cities.isEmpty()) return;

        // Use round number and map seed for deterministic random across all clients
        java.util.Random rand = new java.util.Random(MAP_SEED + round);

        // Pick a random city to hit with an event
        Core.Maps.City targetCity = cities.get(rand.nextInt(cities.size()));

        int eventType = rand.nextInt(3);
        int decayAmount = -(10 + rand.nextInt(15)); // -10 to -24 stats

        String eventName = "";
        long statType = 0;

        if (eventType == 0) {
            eventName = "Economic Recession";
            statType = Core.Maps.PoliticsStats.ECONOMY;
        } else if (eventType == 1) {
            eventName = "Infrastructure Decay";
            statType = Core.Maps.PoliticsStats.FACILITY;
        } else {
            eventName = "Pollution Crisis";
            statType = Core.Maps.PoliticsStats.ENVIRONMENT;
        }

        // Don't let it go below 1
        int currentStat = targetCity.stats.getStats(statType);
        if (currentStat + decayAmount < 1) {
            decayAmount = 1 - currentStat;
        }

        if (decayAmount < 0) {
            targetCity.stats.addStats(statType, decayAmount);
            ZhuzheeEngine.Debug.GameLogger.logWarning("RANDOM EVENT: " + eventName + " hit " + targetCity.getCityName() + "! Stat changed by " + decayAmount);
            // Re-calculate scores or just let the next card take advantage of lower stats
        }
    }
    @Override
    public void dispose() {

    }

    public static class PlayerUI{
        public static GameSettingUI GameSettingUI(Scene2D scene2D) {
            ZhuzheeGame.SETTINGS_UI = new GameSettingUI(scene2D);
            return  ZhuzheeGame.SETTINGS_UI;
        }
        public static PolicyCardHolderUI PolicyCardHolderUITest(Scene2D scene2D){
            ZhuzheeGame.POLICY_CARD_HAND = new PolicyCardHolderUI(scene2D);
            return ZhuzheeGame.POLICY_CARD_HAND;
        }
        public static ArcanaCardHolderUI ArcanaCardHolderUITest(Scene2D scene2D){
            ZhuzheeGame.ARCANA_CARD_UI = new ArcanaCardHolderUI(scene2D);
            return ZhuzheeGame.ARCANA_CARD_UI;
        }
        public static PlayerCoinUI PlayerCoinUITest(Scene2D scene2D) {
            ZhuzheeGame.PLAYER_COIN_UI = new Core.UI.PlayerCoinUI(scene2D);
            return ZhuzheeGame.PLAYER_COIN_UI;
        }
        public static PlayerProfile PlayerProfileUITest(Scene2D scene2D, Player player) {
            ZhuzheeGame.PLAYER_PROFILE_UI = new PlayerProfile(scene2D, player);
            return ZhuzheeGame.PLAYER_PROFILE_UI;
        }
        public static TurnUI TurnUITest(Scene2D scene2D) {
            ZhuzheeGame.TURN_UI = new TurnUI(scene2D);
            return ZhuzheeGame.TURN_UI;
        }
        public static CardHolderUI CardHolderUITest(Scene2D scene2D){
            CardHolderUI ui = new CardHolderUI(scene2D);
            ui.setStrechToFit(true);
            ui.setPanelSize(100,224);
            ui.setMargins(224,224,16,16);
            ui.setAnchorTop(false);
            Color color = ui.getBackground();
            color = new Color(color.getRed(),color.getGreen(),color.getBlue(),100);
            ui.setBackground(color);
            return ui;
        }
    }
}
