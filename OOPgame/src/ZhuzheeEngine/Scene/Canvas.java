package ZhuzheeEngine.Scene;

import javax.swing.*;
import java.awt.*;

/** @Muninthon 8/3/2569 - 22:36
 * class for Making UI on Scene2D
 */
public abstract class Canvas extends JPanel implements SceneObject {
    public static final int CANVAS_Z_INDEX = 1000;
    private int zIndex = 0;
    boolean isAttachWithPanel;
    public Canvas(){
        Scene2D.register(this);
        start();
    }
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
        return Scene2D.Screen2WorldPoint(super.getLocation());
    }

    /// @param position must be Scene2D's World Position
    @Override
    public void setPosition(Point position) {
        if(isAttachWithPanel) return;
        super.setLocation(Scene2D.World2ScreenPoint(position));
    }
}