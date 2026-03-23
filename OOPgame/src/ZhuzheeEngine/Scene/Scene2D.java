package ZhuzheeEngine.Scene;

import ZhuzheeEngine.Screen;

import java.awt.*;
import java.util.ArrayList;

public class Scene2D extends Screen {
    protected ArrayList<GameObject> gameObjects;
    protected ArrayList<IZIndex> zOrderedObjects; // รายการวัตถุทั้งหมดที่จะนำมาเรียง Z-Index (รวม Canvas)

    // กำหนด Layer มาตรฐานเพื่อป้องกันการทับกันมั่ว
    public static class Layer {
        public static final int BACKGROUND = -100;
        public static final int SLOT = -10;
        public static final int DEFAULT = 0;      // สำหรับ GameObject ทั่วไป
        public static final int UI = 100;         // UI ควรอยู่เหนือ Game Object เสมอ
        public static final int DRAGGED = 999;    // ของที่กำลังลาก ต้องอยู่บนสุด
    }

    /// can access this object by using Scene.Instance (this is called Singleton)
    public Scene2D() {
        super();
        gameObjects = new ArrayList<>();
        zOrderedObjects = new ArrayList<>();
        setLayout(null); // เปลี่ยนเป็น Absolute Layout เพื่อให้กำหนดพิกัด GameObject ได้เอง
    }

    public void register(IZIndex obj) {
        zOrderedObjects.add(obj);
        // ถ้าเป็น GameObject ให้เพิ่มเข้า list สำหรับ update loop ด้วย
        if (obj instanceof GameObject) {
            gameObjects.add((GameObject) obj);
        }
        sortZOrderObjects(); // จัดเรียงทันทีเมื่อมีวัตถุใหม่
    }

    @Override
    public void remove(Component comp) {
        super.remove(comp);
        if(comp instanceof IZIndex z)
            remove(z);
    }
    public void remove(GameObject gameObject){
        remove((Component) gameObject);
    }
    public void remove(IZIndex obj) {
        zOrderedObjects.remove(obj);
        if (obj instanceof GameObject) {
            gameObjects.remove((GameObject) obj);
        }
    }

    /// Getter เพื่อให้ Core.Cards สามารถเข้าถึง List ไปเช็ค Slot ได้
    public ArrayList<GameObject> getGameObjects() {
        return gameObjects;
    }
    /// sorting rendering squences of gameObjects by z index
    public void sortZOrderObjects() {
        // 1. เรียงลำดับใน List ตามค่า Z-Index (น้อย -> มาก)
        zOrderedObjects.sort((o1, o2) -> Integer.compare(o1.getZIndex(),o2.getZIndex()));

        // 2. ปรับลำดับใน Swing (Component Z-Order)
        // เทคนิค: วนลูปจากตัวที่ Z-Index มากสุด (ท้าย List) แล้วสั่ง setComponentZOrder ให้ไปอยู่ "ล่างสุด" (Last Index)
        // ผลลัพธ์: GameObjects จะเรียงซ้อนกันถูกต้องตาม Z-Index แต่ทั้งหมดจะอยู่ "ใต้" UI (ซึ่ง UI มักจะจอง Index 0 ไว้)
        for (int i = zOrderedObjects.size() - 1; i >= 0; i--) {
            try {
                setComponentZOrder(zOrderedObjects.get(i).asComponent(), getComponentCount() - 1);
            } catch (IllegalArgumentException ignored) {
            }
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
    public void create() {
        super.create();
        sortZOrderObjects();
    }

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
