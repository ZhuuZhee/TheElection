package Core;

import Core.GameScreens.MainMenu;
import Core.GameScreens.OptionMenu;
import Core.UI.CardHolderUI;
import Dummy.Tester;
import ZhuzheeEngine.Application;
import ZhuzheeEngine.ApplicationAdapter;
import ZhuzheeEngine.Scene.Scene2D;
import ZhuzheeEngine.Screen;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

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

    @Override
    public void create() {
        //set Application title
        Application.setMainFrameTitle("Zhuzhee The Game");

        MAIN_SCENE = new Scene2D();
        MAIN_MENU = new MainMenu();
        LOBBY_MENU = new Core.GameScreens.LobbyMenu();
        CREATE_ROOM_MENU = new Core.GameScreens.CreateRoomMenu();
        JOIN_ROOM_MENU = new Core.GameScreens.JoinRoomMenu();
        WAITING_ROOM_MENU = new Core.GameScreens.WaitingRoomMenu();
        OPTION_MENU = new OptionMenu();
        //set current screen
//        Screen.ChangeScreen(MAIN_MENU);
        Screen.ChangeScreen(MAIN_MENU);

        //test
//        Tester.MainMenu(screenManager);
        Tester.CardsTestingOnScene(MAIN_SCENE);
//        Tester.TestCardStream();
        Tester.MapTest();
//        Tester.ShopTest();
        CardHolderUI holderUI = Tester.CardHolderUITest(MAIN_SCENE);
//        Tester.AudioManagerTesterInitialize();
        new Tester().TestingCamera(MAIN_SCENE);
        Tester.DrawCardTest(MAIN_SCENE, holderUI);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        Screen.currentScreen.render();
    }

    @Override
    public void dispose() {

    }
}
