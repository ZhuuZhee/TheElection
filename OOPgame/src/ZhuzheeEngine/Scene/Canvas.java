package ZhuzheeEngine.Scene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public abstract class Canvas extends JPanel implements IZIndex {
    private int zIndex = Scene2D.Layer.UI; // Default UI ให้สูงกว่า GameObject
    private Scene2D scene;

    // Positioning Attributes
    protected int panelWidth = 100;
    protected int panelHeight = 100;
    protected int marginTop = 0, marginBottom = 0, marginLeft = 0, marginRight = 0;
    protected boolean strechToFit = false;
    
    /** Vertical: 1 = Top, -1 = Bottom, 0 = Use ScreenPos Ratio */
    protected int anchorVertical = 0; 
    /** Horizontal: -1 = Left, 1 = Right, 0 = Use ScreenPos Ratio */
    protected int anchorHorizontal = 0;
    protected Point screenPos = new Point(0, 0);

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

    /**
     * คำนวณตำแหน่งและขนาดอัตโนมัติตาม Anchor และ Margin
     */
    protected void updateBounds(int width, int height) {
        int w = panelWidth;
        int h = panelHeight;
        int x, y;

        // Horizontal Calculation
        if (strechToFit) {
            w = width - marginLeft - marginRight;
            x = marginLeft;
        } else {
            if (anchorHorizontal == -1) x = marginLeft; // Left
            else if (anchorHorizontal == 1) x = width - w - marginRight; // Right
            else x = (screenPos.x != 0) ? (int) (width / (float) screenPos.x) : 0;
        }

        // Vertical Calculation
        if (anchorVertical == 1) y = marginTop; // Top
        else if (anchorVertical == -1) y = height - h - marginBottom; // Bottom
        else y = (screenPos.y != 0) ? (int) (height / (float) screenPos.y) : 0;

        setBounds(x, y, w, h);
        revalidate();
    }

    protected void onResize(int width, int height) {
        updateBounds(width, height);
    }
    public void setAnchorTop(boolean anchorTop) {
        setAnchors(this.anchorHorizontal, anchorTop ? 1 : -1);
    }
    public void setAnchorLeft(boolean anchorLeft){
        setAnchors(anchorLeft ? -1 : 1, this.anchorVertical);
    }
    public void setAnchorRight(boolean anchorRight) { setAnchors(anchorRight ? 1 : -1, this.anchorVertical); }

    // --- Setters สำหรับจัดการ Layout ---
    public void setMargins(int left, int right, int top, int bottom) {
        this.marginLeft = left; this.marginRight = right;
        this.marginTop = top; this.marginBottom = bottom;
        onResize(scene.getWidth(), scene.getHeight());
    }

    public void setAnchors(int horizontal, int vertical) {
        this.anchorHorizontal = horizontal;
        this.anchorVertical = vertical;
        onResize(scene.getWidth(), scene.getHeight());
    }

    public void setPanelSize(int width, int height) {
        this.panelWidth = width;
        this.panelHeight = height;
        onResize(scene.getWidth(), scene.getHeight());
    }

    public void setStrechToFit(boolean enable) {
        this.strechToFit = enable;
        onResize(scene.getWidth(), scene.getHeight());
    }

    public void setScreenPos(int x, int y) { this.screenPos.setLocation(x, y); }
}
