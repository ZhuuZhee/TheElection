package ZhuzheeEngine;

import javax.swing.*;
import java.awt.*;

public abstract class Screen extends JPanel implements ApplicationAdapter {
    public static Screen currentScreen;
    
    public static void ChangeScreen(Screen nextScreen){
        // ป้องกันการโหลดหน้าจอเดิมซ้ำ (เช่น ป้องกันบั๊กจากการกดปุ่มเบิ้ล)
        if (currentScreen == nextScreen) return; 
        
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
            // เช็คก่อนว่าไม่ได้ถูกเซ็ตเป็น ContentPane อยู่แล้ว ถึงจะเซ็ตใหม่
            if (frame.getContentPane() != this) {
                frame.setContentPane(this);
            }
            frame.revalidate();
            frame.repaint();
        }
        this.setVisible(true);
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
    public interface ScreenListener{
        public void onScreenEnter(Screen currentScreen);
        public void onScreenExit(Screen currentScreen,Screen nextScreen);

    }
}