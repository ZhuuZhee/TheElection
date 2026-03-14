package ZhuzheeEngine.Scene;

import java.awt.*;

public class GameObject {
    protected Point position;
    protected Dimension size;
    protected int zIndex;
    protected Scene2D scene;
    private boolean isVisible = true;
    private boolean isEnable = true;

    public GameObject(int x, int y, int width, int height, Scene2D scene) {
        this.position = new Point(x, y);
        this.size = new Dimension(width, height);
        this.zIndex = 0;
        this.scene = scene;
        scene.register(this);
        start();

    }

    public Scene2D getScene() {
        return scene;
    }

    public int getZIndex() {
        return zIndex;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public Dimension getSize() {
        return size;
    }

    public void setSize(Dimension size) {
        this.size = size;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void setVisible(boolean v) {
        isVisible = v;
    }

    public boolean getVisible() {
        return isVisible;
    }

    public void setEnable(boolean e){
        isEnable = e;
    }
    public boolean getEnable(){
        return isEnable;
    }
    // lifecycle hooks
    public void start() {
    }

    public void update() {

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
        gameObject.getScene().remove(gameObject);
    }

    public void onDestroy() {
    }
}
