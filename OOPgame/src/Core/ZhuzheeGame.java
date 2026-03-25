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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

    /// ตั้งเป็น true เพื่อ Run test ทันที, ตั้ง false เพื่อ Run Main
    public static final boolean DEV_MODE = true;

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

        Tester.MapTest();
        new Tester().TestingCamera(MAIN_SCENE);
        CardHolderUI holderUI = Tester.CardHolderUITest(MAIN_SCENE);
        Tester.DrawCardTest(MAIN_SCENE, holderUI);
        Tester.ShopTest();
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
