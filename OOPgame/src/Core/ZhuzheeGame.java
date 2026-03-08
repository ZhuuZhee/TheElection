package Core;

import Core.GameScreens.MainMenu;
import Dummy.AudioManagerTester;
import Dummy.Tester;
import ZhuzheeEngine.Application;
import ZhuzheeEngine.ApplicationAdapter;
import ZhuzheeEngine.Scene.Scene2D;
import ZhuzheeEngine.ScreenManager;

/// Game Logic Handler
public class ZhuzheeGame implements ApplicationAdapter{
    public static ScreenManager screenManager;
    public static Scene2D MainScene;

    public static AudioManagerTester audioManagerTester;
    @Override
    public void create() {
        //set Application title
        Application.setMainFrameTitle("Zhuzhee The Game");

        screenManager = new ScreenManager();

        MainScene = new Scene2D();
        //set current screen
       screenManager.ChangeScreen(MainScene);

        //test
//        Tester.CardsTestingOnScene(MainScene);
//        Tester.MainMenu(screenManager);
//        audioManagerTester = new AudioManagerTester();
//        Tester.AudioManagerTest();
        Tester.main();
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
