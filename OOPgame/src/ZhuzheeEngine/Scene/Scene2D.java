package ZhuzheeEngine.Scene;

import ZhuzheeEngine.Screen;

import java.awt.*;
import java.util.ArrayList;

public class Scene2D extends Screen {
    protected ArrayList<GameObject> gameObjects;
    /// can access this object by using Scene.Instance (this is called Singleton)

    public Scene2D() {
        gameObjects = new ArrayList<>();
        setLayout(new BorderLayout());
    }

    public void register(GameObject gameObject) {
        gameObjects.add(gameObject);
    }
    public void remove(GameObject gameObject){
        gameObjects.remove(gameObject);
    }
    /// Getter เพื่อให้ Core.Cards สามารถเข้าถึง List ไปเช็ค Slot ได้
    public ArrayList<GameObject> getGameObjects() {
        return gameObjects;
    }

    /// sorting rendering squences of gameObjects by z index
    public void sortGameObjects() {
        gameObjects.sort((o1, o2) -> Float.compare(o1.getZIndex(), o2.getZIndex()));
    }

    private Camera2D camera = new Camera2D();

    public Camera2D getCamera() { return camera; }

    public Point Screen2WorldPoint(Point screenPos) {
        return camera.screenToWorld(screenPos, getWidth(), getHeight());
    }

    public Point World2ScreenPoint(Point worldPos) {
        return camera.worldToScreen(worldPos, getWidth(), getHeight());
    }

    //--------------------------------------------------------------------
    //------- this is the main update method for scene! (DO NOT CHANGE) ------------
    //--------------------------------------------------------------------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!(g instanceof Graphics2D)) return;

        Graphics2D g2d = (Graphics2D) g.create();
        try {
            // 1. Move to center of screen
            g2d.translate(getWidth() / 2, getHeight() / 2);

            // 2. Apply Zoom
            g2d.scale(camera.getZoom(), camera.getZoom());

            // 3. Apply Camera Position (Inverse because if camera moves right, world moves left)
            g2d.translate(-camera.getPosition().x, -camera.getPosition().y);

            sortGameObjects();
            ArrayList<GameObject> objectsCopy = new ArrayList<>(gameObjects);
            for (GameObject obj : objectsCopy) {

                // วาด Object ทั่วไป
                if (obj.getVisible()) obj.render(g2d);
            }
        } finally {
            g2d.dispose();
        }
    }
}
