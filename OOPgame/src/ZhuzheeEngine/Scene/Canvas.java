package ZhuzheeEngine.Scene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public abstract class Canvas extends JPanel {
    public Canvas(Scene2D scene){
        scene.add(this);
        scene.setComponentZOrder(this, 0);
        scene.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                onResize(scene.getWidth(),scene.getHeight());
            }
        });
    }
    protected abstract void onResize(int width,int height);
}
