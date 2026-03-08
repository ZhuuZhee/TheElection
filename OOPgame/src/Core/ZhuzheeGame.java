package Core;

import Core.GameScreens.MainMenu;
import Dummy.Tester;
import ZhuzheeEngine.Application;
import ZhuzheeEngine.ApplicationAdapter;
import ZhuzheeEngine.Scene.Scene2D;
import ZhuzheeEngine.ScreenManager;

/// Game Logic Handler
public class ZhuzheeGame implements ApplicationAdapter{
    public ScreenManager screenManager;

    @Override
    public void create() {
        //set Application title
        Application.setMainFrameTitle("Zhuzhee The Game");

        screenManager = new ScreenManager();

        Scene2D scene2D = new Scene2D();
        //set current screen
       screenManager.ChangeScreen(scene2D);

        //test
//        Tester.CardsTestingOnScene(scene2D);
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
