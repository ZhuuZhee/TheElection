package ZhuzheeEngine.Scene;

import javax.swing.*;
import java.awt.*;

public class Canvas extends JPanel implements SceneObject {
    public static final int CANVAS_Z_INDEX = 1000;
    private int zIndex = 0;
    @Override
    public int getZIndex() {
        return zIndex + CANVAS_Z_INDEX;
    }

    @Override
    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }


    @Override
    public Point getPosition() {
        return null;
    }

    @Override
    public void setPosition(Point position) {

    }

    @Override
    public void start() {

    }

    @Override
    public void render(Graphics g) {

    }

    @Override
    public void onDestroy() {

    }
}
