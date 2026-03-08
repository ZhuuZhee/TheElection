package ZhuzheeEngine.Scene;

import java.awt.*;

public class GameObject {
    protected Point position;
    protected Point size;
    private float zIndex;

    public GameObject(int x, int y, int width, int height) {
        this.position = new Point(x, y);
        this.size = new Point(width, height);
        this.zIndex = 0f;
        Scene2D.register(this);
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

    // lifecycle hooks
    public void start(){}
    public void render(Graphics g) {
    }

    //is position inside bounds of this object
    public boolean isInsideBoundaries(int x, int y) {
        return x >= position.x && x <= position.x + getSize().x &&
                y >= position.y && y <= position.y + getSize().y;
    }

    //destroying game object
    public static void Destroy(GameObject gameObject){
        gameObject.onDestroy();
        Scene2D.remove(gameObject);
    }
    public void onDestroy(){}
}
