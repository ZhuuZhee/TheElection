package ZhuzheeEngine.Scene;

import Core.Cards.Card;
import ZhuzheeEngine.Application;
import ZhuzheeEngine.Screen;

import java.awt.*;
import java.util.ArrayList;

public class Scene2D extends Screen {
    protected ArrayList<SceneObject> sceneObjects;
    private Point origin = new Point(0,0);
    /// can access this object by using Scene.Instance (this is called Singleton)

    public Scene2D() {
        sceneObjects = new ArrayList<>();
        setLayout(new BorderLayout());
    }

    public void register(SceneObject sceneObject) {
        sceneObjects.add(sceneObject);
    }
    public void remove(SceneObject sceneObject){
        sceneObjects.remove(sceneObject);
    }
    public void SetSceneOrigin(Point newPoint){
        origin = newPoint;
    }
    /// Getter เพื่อให้ Core.Cards สามารถเข้าถึง List ไปเช็ค Slot ได้
    public ArrayList<SceneObject> getGameObjects() {
        return sceneObjects;
    }

    /// sorting rendering squences of gameObjects by z index
    public void sortGameObjects() {
        sceneObjects.sort((o1, o2) -> Float.compare(o1.getZIndex(), o2.getZIndex()));
    }

    private Camera2D camera = new Camera2D();

    public Camera2D getCamera() { return camera; }

    public Point Screen2WorldPoint(Point screenPos) {
        return camera.screenToWorld(screenPos, getWidth(), getHeight());
    }

    public Point World2ScreenPoint(Point worldPos) {
        return camera.worldToScreen(worldPos, getWidth(), getHeight());
    }

    @Override
    public Component add(Component comp){
        super.add(comp);

        if(comp instanceof Canvas){
            ((Canvas) comp).isAttachWithPanel = true;
        }
        return comp;
    }
    @Override
    public void remove(Component comp){
        super.remove(comp);
        if(comp instanceof Canvas){
            ((Canvas) comp).isAttachWithPanel = false;
        }
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
            ArrayList<SceneObject> objectsCopy = new ArrayList<>(sceneObjects);
            for (SceneObject obj : objectsCopy) {
                if(obj.getEnable()) obj.update();
                if(obj.getVisible()) obj.render(g2d);
            }
        } finally {
            g2d.dispose();
        }
    }
}
