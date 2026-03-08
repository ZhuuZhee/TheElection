package Core;

import Core.GameScreens.MainMenu;
import Dummy.Tester;
import ZhuzheeEngine.Application;
import ZhuzheeEngine.ApplicationAdapter;
import ZhuzheeEngine.Scene.Scene2D;
import ZhuzheeEngine.ScreenManager;

/// Game Logic Handler
public class ZhuzheeGame implements ApplicationAdapter{
    public static ScreenManager screenManager;
    public static Scene2D MainScene;
    @Override
    public void create() {
        //set Application title
        Application.setMainFrameTitle("Zhuzhee The Game");

        screenManager = new ScreenManager();

        MainScene = new Scene2D();
        //set current screen
       screenManager.ChangeScreen(MainScene);

        //test
        Tester.CardsTestingOnScene(MainScene);
        Tester.MainMenu(screenManager);
//        Tester.AudioManagerTest();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render(){
        screenManager.getCurrentScreen().render();
    }

    @Override
    public void dispose() {

    }

}
