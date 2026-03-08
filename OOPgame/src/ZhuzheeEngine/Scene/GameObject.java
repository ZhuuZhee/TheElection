package ZhuzheeEngine.Scene;

import java.awt.*;

public class GameObject implements SceneObject {
    protected Point position;
    protected Dimension size;
    protected int zIndex;

    public GameObject(int x, int y, int width, int height) {
        this.position = new Point(x, y);
        this.size = new Dimension(width, height);
        this.zIndex = 0;
        Scene2D.register(this);
        start();
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    @Override
    public Dimension getSize() {
        return size;
    }

    @Override
    public void setSize(Dimension size) {
        this.size = size;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public void setPosition(Point position) {
        this.position = position;
    }

    // lifecycle hooks
    public void start() {
    }

    public void render(Graphics g) {
    }

    //is position inside bounds of this object
    public boolean isInsideBoundaries(int x, int y) {
        return x >= position.x && x <= position.x + size.width &&
                y >= position.y && y <= position.y + size.height;
    }

    //destroying game object
    public static void Destroy(GameObject gameObject) {
        gameObject.onDestroy();
        Scene2D.remove(gameObject);
    }

    public void onDestroy() {
    }
}
