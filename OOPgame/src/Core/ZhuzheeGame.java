package Core;

import Core.GameScreens.MainGameScreen;
import ZhuzheeEngine.Application;
import ZhuzheeEngine.Screen;

public class ZhuzheeGame extends Application {
    public ScreenManager screenManager;

    private MainGameScreen gameScreen;
    @Override
    protected void Create() {
        super.Create();

        screenManager = new ScreenManager();
        gameScreen = new MainGameScreen();
        screenManager.ChangeScreen(gameScreen);
    }

    @Override
    protected void Render(){
        super.Render();
        screenManager.currentScreen.render();
    }



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
            currentScreen.onScreenExit();
            currentScreen = next;
            lastScreen = currentScreen;
            currentScreen.onScreenEnter();
        }
    }
}
