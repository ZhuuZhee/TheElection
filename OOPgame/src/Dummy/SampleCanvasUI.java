package Dummy;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Scene.Canvas;
import ZhuzheeEngine.Scene.Scene2D;
import ZhuzheeEngine.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SampleCanvasUI extends Canvas {

    public JButton MainMenuBtn;

    public SampleCanvasUI(Scene2D scene) {
        super(scene);
    }

    @Override
    public void start() {
        //System.out.println("SampleScene start " + getPosition().toString());
        setLayout(new FlowLayout(FlowLayout.CENTER));
        MainMenuBtn = new JButton("main menu");
        //back to menu screen on click
        MainMenuBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Screen.ChangeScreen(ZhuzheeGame.MAIN_MENU);
            }
        });
        add(MainMenuBtn);
        setPreferredSize(new Dimension(400, 84));
        scene.add(this, BorderLayout.NORTH);
        setBackground(Color.CYAN);
        setVisible(true);
    }

    @Override
    public void render(Graphics g) {
//        repaint();
       // System.out.println("SampleScene render " + getPosition().toString());
    }

    @Override
    public void onDestroy() {

    }
}
