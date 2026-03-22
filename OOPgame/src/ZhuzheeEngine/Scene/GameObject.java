package ZhuzheeEngine.Scene;

import javax.swing.*;
import java.awt.*;

public class GameObject extends JPanel {
    protected Point worldPosition;
    protected Scene2D scene;
    private boolean isEnable = true;
    private int zIndex;

    public GameObject(int x, int y, int width, int height, Scene2D scene) {
        super();
        // Setup Swing Component
        this.setLayout(null); // รองรับ Child Component แบบอิสระ
        this.setOpaque(false); // พื้นหลังใส เพื่อให้เห็น Scene ด้านหลัง
        this.setSize(width, height); // กำหนดขนาด (ตำแหน่งจะถูกจัดการโดย Scene)

        this.worldPosition = new Point(x, y);
        this.scene = scene;
        scene.register(this);
        scene.add(this); // เพิ่มตัวเองลงใน Scene (ที่เป็น Container)

        start();

    }

    public int getZIndex() {
        return zIndex;
    }
    public void setZIndex(int index){
        zIndex = index;
        if (scene != null) scene.sortGameObjects();
    }
    public Scene2D getScene() {
        return scene;
    }

    public Point getPosition() {
        return worldPosition;
    }

    public void setWorldPosition(Point pos) {
        this.worldPosition = pos;
    }

    public void setPosition(Point position) {
        this.worldPosition = position;
    }

    public void setPosition(int x, int y) {
        this.worldPosition.setLocation(x, y);
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // ให้ Swing เตรียมการวาดพื้นฐาน
        if (isEnable) {
            update();// เรียกใช้ render เดิม (โดย g จะเป็นพิกัด Local 0,0)
        }
    }

    //is position inside bounds of this object
    public boolean isInsideBoundaries(int x, int y) {
        // ใช้ความสามารถของ Swing ตรวจสอบขอบเขต (x,y ต้องเป็นพิกัดเทียบกับ Parent/Scene)
        return super.contains(x, y);
    }

    //destroying game object
    public static void Destroy(GameObject gameObject) {
        gameObject.onDestroy();
        gameObject.getScene().remove(gameObject);
        // ลบออกจาก Swing Container
        gameObject.getScene().remove((Component) gameObject);
        gameObject.getScene().repaint();
    }

    public void onDestroy() {
    }
}
