/**
 * @Munin 11/3/2026 20:33 - move mouse listener to this class
 * @Xynezter 9/3/2026 18:50
 */
package Core.Cards;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Application;
import ZhuzheeEngine.Scene.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public abstract class Card extends GameObject {
    protected String name;
    protected boolean isGrabbed = false;
    protected boolean isDraggable = true;
    protected boolean isHovered = false;
    protected Point offset = new Point(0, 0);

    private static final int Z_INDEX_TOP = 999;
    private static final int Z_INDEX_NORMAL = 0;
    private static final int SNAP_MARGIN = 15;
    private static final double ZOOM_OFFSET = 20.0;

    public Card(String name, int x, int y, int width, int height) {
        super(x, y, width, height, ZhuzheeGame.MAIN_SCENE);
        this.name = name;
//        System.out.println("--------------------");
//        System.out.println(name + " : enable : " + getEnable());
//        System.out.println("--------------------");

        // @Munin 11/3/2026 20:33 - move mouse listener to this class
        //mouse button interactions
        scene.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                Point wolrdPoint = scene.Screen2WorldPoint(e.getPoint());
                onMousePressed(wolrdPoint.x,wolrdPoint.y);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                onMouseReleased();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Point wolrdPoint = scene.Screen2WorldPoint(e.getPoint());
                if(isInsideBoundaries(wolrdPoint.x,wolrdPoint.y))
                    onMouseClick();
            }
        });
        //mouse motion interactions
        scene.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                Point wolrdPoint = scene.Screen2WorldPoint(e.getPoint());
                onMouseDragged(wolrdPoint.x,wolrdPoint.y);
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                Point wolrdPoint = scene.Screen2WorldPoint(e.getPoint());

                if(isInsideBoundaries(wolrdPoint.x,wolrdPoint.y))
                    setHovered(true);
                else if (isHovered)
                    setHovered(false);
            }

        });
    }

    //------------ setter getter -------------
    public void setHovered(boolean hovered) {
        this.isHovered = hovered;
    }

    public void setDraggable(boolean draggable) {
        this.isDraggable = draggable;
    }

    // ----------------------------------------
    // ------------  Mouse Events  ------------
    // ----------------------------------------

    public void onMousePressed(int mouseX, int mouseY) {
        if ( getEnable()) {
            if (isInsideBoundaries(mouseX, mouseY)) {
                System.out.println(name +": Grabbed");
                if (isDraggable) {
                    isGrabbed = true;

                    offset.x = mouseX - position.x;
                    offset.y = mouseY - position.y;
                    setZIndex(Z_INDEX_TOP);
                }
            }
        }
    }
    public void onMouseClick(){}

    public void onMouseDragged(int mouseX, int mouseY) {
        if ( getEnable() && isGrabbed) {
            position.setLocation(mouseX - offset.x, mouseY - offset.y);
        }
    }

    public void onMouseReleased() {
        if ( getEnable() && isGrabbed) {
            isGrabbed = false;
            setZIndex(Z_INDEX_NORMAL);

            // handle when drop card on slot
            var slot = getCardSlotOnBottom();
            if(slot != null){
                snapToSlot(slot);
                onDroppedInSlot(slot);
            }
        }
    }

    // --------------------------------------------------
    // ---------- logic about slot suction --------------
    // --------------------------------------------------
    private CardSlot getCardSlotOnBottom(){
        Rectangle cardRect = new Rectangle(position.x, position.y, size.width, size.height);
        // ดึง GameObjects ทั้งหมดจาก Scene เพื่อหา Slot
        for (GameObject obj : scene.getGameObjects()) {
            if (!(obj instanceof CardSlot)) continue;

            Rectangle slotMagneticField = new Rectangle(
                    obj.getPosition().x - SNAP_MARGIN,
                    obj.getPosition().y - SNAP_MARGIN,
                    obj.getSize().width + (SNAP_MARGIN * 2),
                    obj.getSize().height + (SNAP_MARGIN * 2)
            );

            if (cardRect.intersects(slotMagneticField)) {
                return (CardSlot)obj;
            }
        }
        return null;
    }
    private void snapToSlot(CardSlot slot) {
        position.setLocation(slot.getPosition().x, slot.getPosition().y);
        // เรียก method when card ทับ กับ Magnetic Field ของ slot
    }
    /**Xynezter 14/3/2569 14:12 : Update method is non abstract Arcanacards dont need to Override**/
    protected boolean isDroppable(Object bottom) {
        return false;
    }
    // add method for business logic when card DroppedInSlot
    protected void onDroppedInSlot(CardSlot slot) {}


    @Override
    public void render(Graphics g) {
        // 1. สร้างก๊อปปี้ของ Graphics เพื่อไม่ให้ Scale ไปกระทบตัวอื่น
        Graphics2D g2d = (Graphics2D) g.create();

        try {
            // --------- Hover Effect --------- //
            if (isHovered && !isGrabbed) {
                // คำนวณจุดศูนย์กลางของ Cards
                int cx = position.x + (size.width / 2);
                int cy = position.y + (size.height / 2);

                float scaleX = (float) (size.width + ZOOM_OFFSET) / size.width;
                float scaleY = (float) (size.height + ZOOM_OFFSET) / size.height;

                // Step การขยายจากจุดศูนย์กลาง:
                g2d.translate(cx, cy);           // 1. เลื่อนจุดศูนย์กลาง Cards ไปที่ 0,0
                g2d.scale(scaleX, scaleY);       // 2. ขยาย
                g2d.translate(-cx, -cy);         // 3. เลื่อนกลับมาตำแหน่งเดิม
            }

            // --------- Drawing Logic --------- //
            if (! getEnable()) g2d.setColor(Color.LIGHT_GRAY);
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
            // 2. ทำลายก๊อปปี้ทิ้ง เพื่อคืนค่าเดิมให้ Graphics หลักสำหรับ Cards ใบถัดไป
            g2d.dispose();
        }
    }

    public String getName() {
        return this.name;
    }
}
