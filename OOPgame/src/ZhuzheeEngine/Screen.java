package ZhuzheeEngine;

import javax.swing.*;

public abstract class Screen extends JPanel implements ApplicationAdapter {
    public static Screen currentScreen;
    public static void ChangeScreen(Screen nextScreen){
        nextScreen.onScreenEnter();
        currentScreen.onScreenExit();
        currentScreen = nextScreen;
    }
    public void onScreenEnter(){

    }
    public void onScreenExit(){

    }
}