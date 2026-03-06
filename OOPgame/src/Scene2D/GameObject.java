package Scene2D;

import java.awt.*;

public class GameObject {
    protected Point position;
    protected Point size;
    private float zIndex;

    public GameObject(int x, int y, int width, int height) {
        this.position = new Point(x, y);
        this.size = new Point(width, height);
        this.zIndex = 0f;
//        Scene.initialize(this);
    }

    public float getzIndex() {
        return this.zIndex;
    }

    public void setzIndex(float zIndex) {
        this.zIndex = zIndex;
    }

    public Point getPosition() {
        return this.position;
    }

    public Point getSize() {
        return this.size;
    }

    //called by scene
    public void draw(Graphics g) {
    }

    //is position inside bounds of this object
    public boolean isInsideBoundaries(int x, int y) {
        return isInsideBoundaries(x, y, this);
    }

    public static boolean isInsideBoundaries(int x, int y, GameObject obj) {
        return x >= obj.getPosition().x && x <= obj.getPosition().x + obj.getSize().x &&
                y >= obj.getPosition().y && y <= obj.getPosition().y + obj.getSize().y;
    }
}
