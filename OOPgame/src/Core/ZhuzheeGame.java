package Core;

import Core.GameScreens.MainMenu;
import Core.GameScreens.OptionMenu;
import Core.UI.CardHolderUI;
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

/// Game Logic Handler
public class ZhuzheeGame implements ApplicationAdapter {
    public static Scene2D MAIN_SCENE;
    public static MainMenu MAIN_MENU;

    public static Core.GameScreens.LobbyMenu LOBBY_MENU;
    public static Core.GameScreens.CreateRoomMenu CREATE_ROOM_MENU;
    public static Core.GameScreens.JoinRoomMenu JOIN_ROOM_MENU;
    public static Core.GameScreens.WaitingRoomMenu WAITING_ROOM_MENU;
    public static OptionMenu OPTION_MENU;

    public static Core.Network.Server.GameServerManager SERVER;
    public static Core.Network.Client.GameClientManager CLIENT;
    
    public static Core.Maps.Map MAP;
    public static long MAP_SEED = new java.util.Random().nextLong();

    public static CardHolderUI PLAYER_HAND_DEV_CARDS;

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
        OPTION_MENU = new OptionMenu();

        if (DEV_MODE) {
            startMainScene(); // Run test ทันที
        } else {
            Screen.ChangeScreen(MAIN_MENU); // Run Main จริง
        }
        AudioManager.getInstance().loadSound("bgm","UaH.WAV");
//        AudioManager.getInstance().playLoop("bgm");
    }

    /// MAIN_SCENE
    public static void startMainScene() {
        Screen.ChangeScreen(MAIN_SCENE);

        MAP = new Core.Maps.Map(MAP_SEED);
        CardHolderUI holderUI = Tester.CardHolderUITest(MAIN_SCENE);
        PLAYER_HAND_DEV_CARDS = holderUI;
        Tester.DrawCardTest(MAIN_SCENE, holderUI);
        Tester.ShopTest();
        CameraControlEvent(MAIN_SCENE);
    }

    private static final float MAX_ZOOM = 2, MIN_ZOOM = 0.25f;
    private static Point mousePoint;
    public static void CameraControlEvent(Scene2D scene){
        // ใช้ AWTEventListener เพื่อดักจับ Input ของเมาส์แบบ Global ไม่ว่าจะชี้อยู่บน Map หรือ UI ตัวไหนก็จะขยับกล้องได้
        long eventMask = AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK;

        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            // เช็ค MouseWheel ก่อน เพราะมันสืบทอดมาจาก MouseEvent
            if (event instanceof MouseWheelEvent e) {
                var cam = MAIN_SCENE.getCamera();
                float zoom = cam.getZoom() - e.getWheelRotation() * ZhuzheeEngine.Application.getDeltaTime();
                zoom = Math.clamp(zoom,MIN_ZOOM,MAX_ZOOM);
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
                    MAIN_SCENE.getCamera().translate((int)(-dx / (float)cam.getZoom()), (int)(-dy /(float)cam.getZoom()));
                    mousePoint = e.getLocationOnScreen(); // ใช้ LocationOnScreen เพื่อป้องกันหน้าจอกระตุกเมื่อเมาส์ลากข้ามระหว่าง Component
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
