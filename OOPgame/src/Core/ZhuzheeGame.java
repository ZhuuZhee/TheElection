package Core;

import Core.GameScreens.CharacterSelectMenu;
import Core.GameScreens.CreditUI;
import Core.GameScreens.MainMenu;
import Core.GameScreens.OptionMenu;
import Core.Maps.Grid;
import Core.Player.Player;
import Core.UI.CardHolderUI;
import Core.UI.PlayerListUI;
import Core.UI.PolicyCardHolderUI;
import Core.UI.ArcanaCardHolderUI;
import Dummy.Tester;
import ZhuzheeEngine.Application;
import ZhuzheeEngine.ApplicationAdapter;
import ZhuzheeEngine.Audios.AudioManager;
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
    public static CharacterSelectMenu CHARACTER_SELECT_MENU;

    public static Core.Network.Server.GameServerManager SERVER;
    public static Core.Network.Client.GameClientManager CLIENT;

    public static Core.Maps.Map MAP;
    public static long MAP_SEED = new java.util.Random().nextLong();

    public static CardHolderUI DEVLOPMENT_CARD_HAND;
    public static PolicyCardHolderUI POLICY_CARD_HAND;
    public static ArcanaCardHolderUI ARCANA_CARD_UI;
    public static PlayerListUI PLAYER_LIST_UI;

    /// ตั้งเป็น true เพื่อ Run test ทันที, ตั้ง false เพื่อ Run Main
    public static final boolean DEV_MODE = false;

    public static MouseAdapter MOUSE_HOVER_SFX = new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            super.mouseEntered(e);
            AudioManager.getInstance().playSound("hover");
        }
    };

    @Override
    public void create() {
        // set Application title
        Application.setMainFrameTitle("Zhuzhee The Game");

        MAIN_SCENE = new Scene2D();
        MAIN_MENU = new MainMenu();
        LOBBY_MENU = new Core.GameScreens.LobbyMenu();
        CREATE_ROOM_MENU = new Core.GameScreens.CreateRoomMenu();
        JOIN_ROOM_MENU = new Core.GameScreens.JoinRoomMenu();
        WAITING_ROOM_MENU = new Core.GameScreens.WaitingRoomMenu();
        CREDIT_UI = new CreditUI();
        OPTION_MENU = new OptionMenu();
        CHARACTER_SELECT_MENU = new CharacterSelectMenu();

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

        MAP = new Core.Maps.Map(MAP_SEED);
        Tester.CardsTestingOnScene(MAIN_SCENE);
        // Tester.MapTest(); // ลบออกเพราะมันสร้าง Map ซ้ำซ้อนกัน 2 รอบ ทำให้มี Map สองตัวทับกันแล้ว Seed ไม่ตรงกัน
        DEVLOPMENT_CARD_HAND = Tester.CardHolderUITest(MAIN_SCENE);

        Tester.PolicyCardHolderUITest(MAIN_SCENE);
        Tester.ArcanaCardHolderUITest(MAIN_SCENE);

        // Player List UI
        List<Player> actualPlayers = new ArrayList<>();
        if (CLIENT != null && !CLIENT.getConnectedPlayers().isEmpty()) {
            actualPlayers = CLIENT.getConnectedPlayers();
        } else if (DEV_MODE) {
            actualPlayers.add(new Player("1", "P'Few", true));
            actualPlayers.add(new Player("2", "Xynezter", false));
            actualPlayers.add(new Player("3", "Thana", false));
            actualPlayers.add(new Player("4", "KUY", false));
        }

        PLAYER_LIST_UI = new PlayerListUI(MAIN_SCENE, actualPlayers);

        Tester.CardTesterUI(MAIN_SCENE);

        Tester.ShopTest();
        CameraControlEvent(MAIN_SCENE);
    }

    private static final float MAX_ZOOM = 2, MIN_ZOOM = 0.25f;
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
                zoom = Math.clamp(zoom, MIN_ZOOM, MAX_ZOOM);
                cam.setZoom(zoom);
            }
            // แล้วค่อยเช็ค MouseEvent ธรรมดา
            else if (event instanceof MouseEvent e) {
                if (e.getID() == MouseEvent.MOUSE_CLICKED && javax.swing.SwingUtilities.isLeftMouseButton(e)) {
                    // แปลงพิกัดจาก Screen เป็น Map Point
                    Point scenePoint = javax.swing.SwingUtilities.convertPoint((Component)e.getSource(), e.getPoint(), MAIN_SCENE);
                    Point mapPoint = javax.swing.SwingUtilities.convertPoint(MAIN_SCENE, scenePoint, MAP);

                    Grid clickedGrid = MAP.getGridAtPoint(mapPoint);
                    if (clickedGrid != null) {
                        // ใส่ sout ตรงนี้เพื่อดูว่า Global Click ทำงานที่ช่องไหน
                        System.out.println("[Global Click] Detected at City: " + clickedGrid.getCity().getCityName());
                    } else {
                        System.out.println("[Global Click] No Grid at: " + mapPoint);
                    }
                }
                // อัปเดตตำแหน่งตั้งต้นตอนเริ่มกดเมาส์กลาง
                if (e.getID() == MouseEvent.MOUSE_PRESSED && javax.swing.SwingUtilities.isRightMouseButton(e)) {
                    mousePoint = e.getLocationOnScreen();
                }
                // คอยขยับกล้องเวลาลากเมาส์กลาง
                else if (e.getID() == MouseEvent.MOUSE_DRAGGED && javax.swing.SwingUtilities.isRightMouseButton(e)) {
                    int dx = e.getLocationOnScreen().x - mousePoint.x;
                    int dy = e.getLocationOnScreen().y - mousePoint.y;
                    var cam = MAIN_SCENE.getCamera();
                    MAIN_SCENE.getCamera().translate((int) (-dx / cam.getZoom()),
                            (int) (-dy / (float) cam.getZoom()));
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

    }

    @Override
    public void dispose() {

    }
}
