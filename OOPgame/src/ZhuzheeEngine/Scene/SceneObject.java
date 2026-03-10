package ZhuzheeEngine.Scene;

import java.awt.*;

public interface SceneObject {
    public Scene2D getScene();
    public int getZIndex();
    public void setZIndex(int zIndex);

    public Dimension getSize();
    public void setSize(Dimension size);
    public Point getPosition();
    public void setPosition(Point position);

    // lifecycle hooks
    public void start();
    public void render(Graphics g);

    public void onDestroy();
}
