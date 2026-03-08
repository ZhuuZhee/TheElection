package ZhuzheeEngine;

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