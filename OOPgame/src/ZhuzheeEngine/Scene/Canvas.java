package ZhuzheeEngine.Scene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public abstract class Canvas extends JPanel implements IZIndex {
    private int zIndex = Scene2D.Layer.UI; // Default UI ให้สูงกว่า GameObject
    private Scene2D scene;
    public Canvas(Scene2D scene){
        this.scene = scene;
        scene.register(this); // ลงทะเบียนกับ Scene เพื่อให้จัด Z-Index ได้
        scene.add(this);
        scene.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                onResize(scene.getWidth(),scene.getHeight());
            }
        });
    }
    @Override
    public int getZIndex() {
        return zIndex;
    }
    @Override
    public void setZIndex(int index){
        zIndex = index;
        if (scene != null) scene.sortZOrderObjects(); // เรียกจัดเรียงใหม่เมื่อเปลี่ยนค่า
    }
    @Override
    public Component asComponent() {
        return this;
    }
    protected abstract void onResize(int width,int height);
}
