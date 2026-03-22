package ZhuzheeEngine.Scene;

import ZhuzheeEngine.Screen;

import java.awt.*;
import java.util.ArrayList;

public class Scene2D extends Screen {
    protected ArrayList<GameObject> gameObjects;

    /// can access this object by using Scene.Instance (this is called Singleton)
    public Scene2D() {
        super();
        gameObjects = new ArrayList<>();
        setLayout(null); // เปลี่ยนเป็น Absolute Layout เพื่อให้กำหนดพิกัด GameObject ได้เอง
    }

    public void register(GameObject gameObject) {
        gameObjects.add(gameObject);
    }

    public void remove(GameObject gameObject) {
        gameObjects.remove(gameObject);
    }

    /// Getter เพื่อให้ Core.Cards สามารถเข้าถึง List ไปเช็ค Slot ได้
    public ArrayList<GameObject> getGameObjects() {
        return gameObjects;
    }

    /// sorting rendering squences of gameObjects by z index
    public void sortGameObjects() {
        // 1. เรียงลำดับใน List ตามค่า Z-Index (น้อย -> มาก)
        gameObjects.sort((o1, o2) -> Integer.compare(o1.getZIndex(), o2.getZIndex()));

        // 2. ปรับลำดับใน Swing (Component Z-Order)
        // เทคนิค: วนลูปจากตัวที่ Z-Index มากสุด (ท้าย List) แล้วสั่ง setComponentZOrder ให้ไปอยู่ "ล่างสุด" (Last Index)
        // ผลลัพธ์: GameObjects จะเรียงซ้อนกันถูกต้องตาม Z-Index แต่ทั้งหมดจะอยู่ "ใต้" UI (ซึ่ง UI มักจะจอง Index 0 ไว้)
        for (int i = gameObjects.size() - 1; i >= 0; i--) {
            try {
                setComponentZOrder(gameObjects.get(i), getComponentCount() - 1);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    private Camera2D camera = new Camera2D();

    public Camera2D getCamera() {
        return camera;
    }

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
        // Update positions of all GameObjects based on Camera and WorldPosition
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        Point camPos = camera.getPosition();
        double zoom = camera.getZoom();
        for (GameObject obj : gameObjects) {

            Point wp = obj.getPosition(); // This is WorldPosition

            // Calculate Screen X, Y based on World Position
            // Screen = (World - Camera) * Zoom + Center
            int screenX = (int) ((wp.x - camPos.x) * zoom + centerX);
            int screenY = (int) ((wp.y - camPos.y) * zoom + centerY);

            // Update Swing Component Location
            obj.setLocation(screenX, screenY);
        }
    }
}
