package ZhuzheeEngine.Scene;

import Core.Cards.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public abstract class Canvas extends JPanel implements IZIndex {
    private int zIndex = Scene2D.Layer.UI; // Default UI ให้สูงกว่า GameObject
    private Scene2D scene;

    public Canvas(Scene2D scene) {
        this.scene = scene;
        scene.add(this);
        scene.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                onResize(scene.getWidth(), scene.getHeight());
            }
        });
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public void setZIndex(int index) {
        zIndex = index;
        if (scene != null) scene.sortZOrderObjects(); // เรียกจัดเรียงใหม่เมื่อเปลี่ยนค่า
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
    @Override
    public Component asComponent() {
        return this;
    }

    protected abstract void onResize(int width, int height);
}
