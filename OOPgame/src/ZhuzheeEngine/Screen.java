package ZhuzheeEngine;

import javax.swing.*;
import java.awt.*;

public abstract class Screen extends JPanel implements ApplicationAdapter {
    public static Screen currentScreen;
    public static void ChangeScreen(Screen nextScreen){
        if (currentScreen != null) {
            currentScreen.onScreenExit();
        }
        currentScreen = nextScreen;
        if (currentScreen != null) {
            currentScreen.onScreenEnter();
        }
    }
    public void onScreenEnter(){
        JFrame frame = Application.getMainFrame();
        if (frame != null) {
            frame.setContentPane(this);
            frame.revalidate();
            frame.repaint();
        }
    }
    public void onScreenExit(){

    }

    @Override
    public void create() {

    }

    @Override
    public void render() {

    }

    @Override
    public void dispose() {

    }
}