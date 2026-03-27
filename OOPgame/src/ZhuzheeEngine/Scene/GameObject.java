package ZhuzheeEngine.Scene;

import ZhuzheeEngine.Screen;

import javax.swing.*;
import java.awt.*;

public class GameObject extends JPanel implements IZIndex {
    protected Point worldPosition;
    protected Scene2D scene;
    protected Dimension baseSize;
    private boolean isEnable = true;
    private int zIndex;

    public GameObject(int x, int y, int width, int height, Scene2D scene) {
        super();
        // Setup Swing Component
        this.setLayout(null); // รองรับ Child Component แบบอิสระ
        this.setOpaque(false); // พื้นหลังใส เพื่อให้เห็น Scene ด้านหลัง
        this.baseSize = new Dimension(width, height);
        this.setSize(width, height); // กำหนดขนาด (ตำแหน่งจะถูกจัดการโดย Scene)

        this.worldPosition = new Point(x, y);
        this.scene = scene;
        scene.register(this);
        scene.add(this); // เพิ่มตัวเองลงใน Scene (ที่เป็น Container)

        start();

    }

    @Override
    public int getZIndex() {
        return zIndex;
    }
    @Override
    public void setZIndex(int index){
        zIndex = index;
        if (scene != null) scene.sortZOrderObjects();
    }
    @Override
    public Component asComponent() {
        return this;
    }
    public Scene2D getScene() {
        return scene;
    }

    public Point getPosition() {
        return worldPosition;
    }

    public Dimension getBaseSize() {
        return baseSize;
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    public void update() {

    }

    //is position inside bounds of this object
    public boolean isInsideBoundaries(int x, int y) {
        // ใช้ความสามารถของ Swing ตรวจสอบขอบเขต (x,y ต้องเป็นพิกัดเทียบกับ Parent/Scene)
        return super.contains(x, y);
    }

    //destroying game object
    public static void Destroy(GameObject gameObject) {
        if (gameObject == null) return;
        
        Scene2D scene = gameObject.getScene();
        
        // 1. เรียก Lifecycle hook ให้ Object เคลียร์ตัวเองก่อน (หยุด Thread, เคลียร์ Listeners)
        gameObject.onDestroy();
        
        if (scene != null) {
            // 2. ลบออกจาก Swing Tree และ Engine Lists (เรียก Scene2D.remove)
            scene.remove(gameObject);
            
            // 3. บังคับให้หน้าจอวาดใหม่ทันทีเพื่อลบภาพค้าง
            scene.revalidate();
            scene.repaint();
        }
        
        // 4. ตั้งค่าเป็นกากบาทเพื่อให้ระบุได้ว่าถูกทำลายแล้ว (Optional)
        gameObject.setVisible(false);
        gameObject.setEnable(false);
    }

    public void onDestroy() {
        // แนะนำ: ลบ Listeners เพื่อป้องกัน Memory Leak
        for (java.awt.event.MouseListener l : getMouseListeners()) removeMouseListener(l);
        for (java.awt.event.MouseMotionListener l : getMouseMotionListeners()) removeMouseMotionListener(l);
        
        // บันทึก: หลังจากเรียก Destroy(obj) แล้ว 
        // อย่าลืมตั้งค่าตัวแปรที่ถือ obj นั้นให้เป็น null ด้วย (เช่น player = null;)
    }
}
