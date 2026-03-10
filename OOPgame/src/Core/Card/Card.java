/**
 * @Xynezter 9/3/2026 18:50
 */
package Core.Card;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Scene.*;
import java.awt.*;

public abstract class Card extends GameObject {
    protected String name;
    protected boolean enabled;
    protected boolean isGrabbed = false;
    protected boolean isDraggable = true;
    protected boolean isHovered = false;
    protected Point offset = new Point(0, 0);

    private static final int Z_INDEX_TOP = 999;
    private static final int Z_INDEX_NORMAL = 0;
    private static final int SNAP_MARGIN = 15;
    private static final double ZOOM_OFFSET = 20.0;

    public Card(String name, int x, int y, int width, int height, boolean enabled) {
        super(x, y, width, height, ZhuzheeGame.MAIN_SCENE);
        this.name = name;
        this.enabled = enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setHovered(boolean hovered) {
        this.isHovered = hovered;
    }

    public void setDraggable(boolean draggable) {
        this.isDraggable = draggable;
    }

    // ----------------------------------------
    // ------------  Mouse Events  ------------
    // ----------------------------------------

    public boolean onMousePressed(int mouseX, int mouseY) {
        if (!enabled){
            return false;
        }
        if (isInsideBoundaries(mouseX, mouseY)) {
//            System.out.println("Grabbed");
            if (!isDraggable) {
                return true;
            }
            isGrabbed = true;

            offset.x = mouseX - position.x;
            offset.y = mouseY - position.y;
            setZIndex(Z_INDEX_TOP);
            return true;
        }
        return false;
    }

    public boolean onMouseDragged(int mouseX, int mouseY) {
        if (!enabled || !isGrabbed) {
            return false;
        }
//        System.out.println("Drag");
        position.x = mouseX - offset.x;
        position.y = mouseY - offset.y;
        return true;
    }

    public boolean onMouseReleased() {
        if (!enabled || !isGrabbed) {
            return false;
        }
        isGrabbed = false;
        setZIndex(Z_INDEX_NORMAL);
        snapToSlot();
        return true;
    }

    // --------------------------------------------------
    // ---------- logic about slot suction --------------
    // --------------------------------------------------

    private void snapToSlot() {
        Rectangle cardRect = new Rectangle(position.x, position.y, size.width, size.height);
        // ดึง GameObjects ทั้งหมดจาก Scene เพื่อหา Slot
        for (SceneObject obj : scene.getGameObjects()) {
            if (!(obj instanceof CardSlot)) continue;

            Rectangle slotMagneticField = new Rectangle(
                    obj.getPosition().x - SNAP_MARGIN,
                    obj.getPosition().y - SNAP_MARGIN,
                    obj.getSize().width + (SNAP_MARGIN * 2),
                    obj.getSize().height + (SNAP_MARGIN * 2)
            );

            if (cardRect.intersects(slotMagneticField)) {
                position.setLocation(obj.getPosition().x, obj.getPosition().y);
                // เรียก method when card ทับ กับ Magnetic Field ของ slot
                onDroppedInSlot((CardSlot) obj);
                break;
            }
        }
    }

    protected abstract boolean isDroppable(Object bottom);
    // add method for business logic when card DroppedInSlot
    protected abstract void onDroppedInSlot(CardSlot slot);

    @Override
    public void render(Graphics g) {
        // 1. สร้างก๊อปปี้ของ Graphics เพื่อไม่ให้ Scale ไปกระทบตัวอื่น
        Graphics2D g2d = (Graphics2D) g.create();

        try {
            // --------- Hover Effect --------- //
            if (isHovered && !isGrabbed) {
                // คำนวณจุดศูนย์กลางของ Card
                int cx = position.x + (size.width / 2);
                int cy = position.y + (size.height / 2);

                float scaleX = (float) (size.width + ZOOM_OFFSET) / size.width;
                float scaleY = (float) (size.height + ZOOM_OFFSET) / size.height;

                // Step การขยายจากจุดศูนย์กลาง:
                g2d.translate(cx, cy);           // 1. เลื่อนจุดศูนย์กลาง Card ไปที่ 0,0
                g2d.scale(scaleX, scaleY);       // 2. ขยาย
                g2d.translate(-cx, -cy);         // 3. เลื่อนกลับมาตำแหน่งเดิม
            }

            // --------- Drawing Logic --------- //
            if (!enabled) g2d.setColor(Color.LIGHT_GRAY);
            else if (isGrabbed) g2d.setColor(new Color(255, 165, 0));
            else g2d.setColor(new Color(176, 255, 183));

            g2d.fillRect(position.x, position.y, size.width, size.height);

            g2d.setColor(Color.BLACK);
            g2d.drawRect(position.x, position.y, size.width, size.height);

            FontMetrics fm = g2d.getFontMetrics();
            int textX = position.x + (size.width - fm.stringWidth(name)) / 2;
            int textY = position.y + (size.height - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(name, textX, textY);

        } finally {
            // 2. ทำลายก๊อปปี้ทิ้ง เพื่อคืนค่าเดิมให้ Graphics หลักสำหรับ Card ใบถัดไป
            g2d.dispose();
        }
    }
}
