package Core;

import Dummy.Tester;
import ZhuzheeEngine.Application;
import ZhuzheeEngine.Scene.Scene2D;
import ZhuzheeEngine.Screen;

/// Game Logic Handler
public class ZhuzheeGame extends Application {
    public ScreenManager screenManager;

    @Override
    protected void create() {
        super.create();

        screenManager = new ScreenManager();

        Scene2D scene2D = new Scene2D();
        //set current screen
        screenManager.ChangeScreen(scene2D);

        //test
        Tester.CardsTestingOnScene(scene2D);
    }

    @Override
    protected void render(){
        super.render();
        screenManager.currentScreen.render();
    }

    //---------------------------------------------
    /// use to manage screen changing.
    public class ScreenManager{
        private Screen currentScreen;
        private Screen lastScreen;

        public Screen getCurrentScreen() {
            return currentScreen;
        }

        public Screen getLastScreen() {
            return lastScreen;
        }

        public void ChangeScreen(Screen next){
            if(currentScreen != null) currentScreen.onScreenExit();
            if(next != null) next.onScreenEnter();
            currentScreen = next;
            lastScreen = currentScreen;
        }
    }
}
