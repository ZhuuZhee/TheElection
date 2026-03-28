package Core.UI;

import Core.GameScreens.OptionMenu;
import ZhuzheeEngine.Audios.AudioManager;
import ZhuzheeEngine.Scene.Canvas;
import ZhuzheeEngine.Scene.Scene2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameSettingUI extends Canvas {

    public GameSettingUI(Scene2D scene) {
        super(scene);

        setPanelSize(50, 50);
        setAnchors(1, 1); // อยู่มุมขวาบน
        setMargins(20, 100, 20, 20); // เว้นขอบจอ

        setLayout(new BorderLayout());
        setOpaque(false);

        JButton settingButton = new JButton("≡");
        settingButton.setFont(new Font("SansSerif", Font.BOLD, 28));
        settingButton.setForeground(Color.WHITE);
        settingButton.setBackground(new Color(60, 120, 150, 200));
        settingButton.setFocusPainted(false);
        settingButton.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 2));
        settingButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        settingButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                settingButton.setBackground(new Color(80, 140, 170, 220));
                AudioManager.getInstance().playSound("hover");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                settingButton.setBackground(new Color(60, 120, 150, 200));
            }
        });

        settingButton.addActionListener(e -> {
            AudioManager.getInstance().playSound("click");
            ZhuzheeEngine.Screen.ChangeScreen(new OptionMenu(true));
        });

        add(settingButton, BorderLayout.CENTER);

        onResize(scene.getWidth(), scene.getHeight());
        setVisible(true);
    }
}
