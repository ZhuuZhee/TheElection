package Core;

import Core.GameScreens.MainMenu;
import Core.GameScreens.OptionMenu;
import Dummy.Tester;
import ZhuzheeEngine.Application;
import ZhuzheeEngine.ApplicationAdapter;
import ZhuzheeEngine.Scene.Scene2D;
import ZhuzheeEngine.Screen;

/// Game Logic Handler
public class ZhuzheeGame implements ApplicationAdapter{
    public static Scene2D MAIN_SCENE;
    public static MainMenu MAIN_MENU;
    public static OptionMenu OPTION_MENU;

    @Override
    public void create() {
        //set Application title
        Application.setMainFrameTitle("Zhuzhee The Game");

        MAIN_SCENE = new Scene2D();
        MAIN_MENU = new MainMenu();
        OPTION_MENU = new OptionMenu();
        //set current screen
        Screen.ChangeScreen(MAIN_MENU);
//        Screen.ChangeScreen(MAIN_SCENE);

        //test
//        Tester.MainMenu(screenManager);
//        Tester.SampleCanvasTest(MAIN_SCENE);
        Tester.CardsTestingOnScene(MAIN_SCENE);
//        Tester.MapTest();
//        Tester.ShopTest();
//        Tester.AudioManagerTesterInitialize();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render(){
        Screen.currentScreen.render();
    }

    @Override
    public void dispose() {

    }

}
