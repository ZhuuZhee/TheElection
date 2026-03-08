package Core.Card;

import ZhuzheeEngine.Scene.*;

import java.awt.*;
import java.awt.geom.AffineTransform;

public abstract class Card extends GameObject {
    protected String name;
    protected boolean enabled;
    protected boolean isGrabbed = false;
    protected boolean isDraggable = true;
    protected boolean isHovered = false;
    protected Point offset = new Point(0, 0);

    private static final float Z_INDEX_TOP = 999f;
    private static final float Z_INDEX_NORMAL = 0f;
    private static final int SNAP_MARGIN = 15;
    private static final double ZOOM_OFFSET = 20.0;

    public Card(String name, int x, int y, int width, int height, boolean enabled) {
        super(x, y, width, height);
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
            System.out.println("Grabbed");
            if (!isDraggable) {
                return true;
            }
            isGrabbed = true;

            offset.x = mouseX - position.x;
            offset.y = mouseY - position.y;
            setzIndex(Z_INDEX_TOP);
            return true;
        }
        return false;
    }

    public boolean onMouseDragged(int mouseX, int mouseY) {
        if (!enabled || !isGrabbed) {
            return false;
        }
        System.out.println("Drag");
        position.x = mouseX - offset.x;
        position.y = mouseY - offset.y;
        return true;
    }

    public boolean onMouseReleased() {
        if (!enabled || !isGrabbed) {
            return false;
        }
        isGrabbed = false;
        setzIndex(Z_INDEX_NORMAL);
        snapToSlot();
        return true;
    }

    // --------------------------------------------------
    // ---------- logic about slot suction --------------
    // --------------------------------------------------

    private void snapToSlot() {
        Rectangle cardRect = new Rectangle(position.x, position.y, size.x, size.y);
        // ดึง GameObjects ทั้งหมดจาก Scene เพื่อหา Slot
        for (GameObject obj : Scene2D.Instance.getGameObjects()) {
            if (!(obj instanceof CardSlot)) continue;

            Rectangle slotMagneticField = new Rectangle(
                    obj.getPosition().x - SNAP_MARGIN,
                    obj.getPosition().y - SNAP_MARGIN,
                    obj.getSize().x + (SNAP_MARGIN * 2),
                    obj.getSize().y + (SNAP_MARGIN * 2)
            );

            if (cardRect.intersects(slotMagneticField)) {
                position.setLocation(obj.getPosition().x, obj.getPosition().y);
                break;
            }
        }
    }

    protected abstract boolean isDroppable(Object bottom);

    @Override
    public void render(Graphics g) {
        // 1. สร้างก๊อปปี้ของ Graphics เพื่อไม่ให้ Scale ไปกระทบตัวอื่น
        Graphics2D g2d = (Graphics2D) g.create();

        try {
            // --------- Hover Effect --------- //
            if (isHovered && !isGrabbed) {
                // คำนวณจุดศูนย์กลางของ Card
                int cx = position.x + (size.x / 2);
                int cy = position.y + (size.y / 2);

                float scaleX = (float) (size.x + ZOOM_OFFSET) / size.x;
                float scaleY = (float) (size.y + ZOOM_OFFSET) / size.y;

                // Step การขยายจากจุดศูนย์กลาง:
                g2d.translate(cx, cy);           // 1. เลื่อนจุดศูนย์กลาง Card ไปที่ 0,0
                g2d.scale(scaleX, scaleY);       // 2. ขยาย
                g2d.translate(-cx, -cy);         // 3. เลื่อนกลับมาตำแหน่งเดิม
            }

            // --------- Drawing Logic --------- //
            if (!enabled) g2d.setColor(Color.LIGHT_GRAY);
            else if (isGrabbed) g2d.setColor(new Color(255, 165, 0));
            else g2d.setColor(new Color(176, 255, 183));

            g2d.fillRect(position.x, position.y, size.x, size.y);

            g2d.setColor(Color.BLACK);
            g2d.drawRect(position.x, position.y, size.x, size.y);

            FontMetrics fm = g2d.getFontMetrics();
            int textX = position.x + (size.x - fm.stringWidth(name)) / 2;
            int textY = position.y + (size.y - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(name, textX, textY);

        } finally {
            // 2. ทำลายก๊อปปี้ทิ้ง เพื่อคืนค่าเดิมให้ Graphics หลักสำหรับ Card ใบถัดไป
            g2d.dispose();
        }
    }
}
