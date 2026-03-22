/**
 * @Munin 11/3/2026 20:33 - move mouse listener to this class
 * @Xynezter 9/3/2026 18:50
 */
package Core.Cards;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Application;
import ZhuzheeEngine.Scene.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class Card extends GameObject {
    protected String name;
    protected boolean isGrabbed = false;
    protected boolean isDraggable = true;
    protected boolean isHovered = false;
    protected Point offset = new Point(0, 0);
    public static Card CURRENT_GRABBED_CARD;
    private static final int Z_INDEX_TOP = 0;
    private static final int Z_INDEX_NORMAL = 999;
    private static final int SNAP_MARGIN = 15;
    private static final double ZOOM_OFFSET = 20.0;

    public Card(String name, int x, int y, int width, int height) {
        super(x, y, width, height, ZhuzheeGame.MAIN_SCENE);
        this.name = name;
        // Swing Component Setup
        this.setBackground(Color.CYAN);
        this.setOpaque(true); // ให้พื้นหลังใส เพื่อให้เห็น Scene หรือ Card ที่ซ้อนกัน

//        System.out.println("--------------------");
//        System.out.println(name + " : enable : " + getEnable());
//        System.out.println("--------------------");

        // @Munin 11/3/2026 20:33 - move mouse listener to this class
        // Mouse Interactions on THIS Component
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // e.getPoint() is local to the card (e.g., 0-100, 0-150)
                onMousePressed(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                onMouseReleased();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                onMouseClick();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setHovered(true);
                repaint(); // Swing needs repaint trigger
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setHovered(false);
                repaint();
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                onMouseDragged(e.getX(), e.getY());
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
        if (getEnable()) {
            // No need to check boundaries, event is fired on component
            System.out.println(name + ": Grabbed");
            if (isDraggable) {
                isGrabbed = true;
                CURRENT_GRABBED_CARD = this;
                // Store the click point relative to the card's top-left
                offset.x = mouseX;
                offset.y = mouseY;

                // Swing Z-Order: 0 is Top
                setZIndex(Z_INDEX_TOP);
            }
        }
    }

    public void onMouseClick() {
    }

    public void onMouseDragged(int mouseX, int mouseY) {
        if (getEnable() && isGrabbed) {
            // 1. Calculate target Screen Position
            int targetScreenX = getX() + mouseX - offset.x;
            int targetScreenY = getY() + mouseY - offset.y;

            // 2. Convert Screen to World (Logic is in Scene/Camera)
            Point worldPos = scene.Screen2WorldPoint(new Point(targetScreenX, targetScreenY));

            // 3. Update World Position (Scene will update Swing Location in next paint)
            this.setPosition(worldPos);

            // 4. Force repaint to update layout instantly
            scene.repaint();
        }
    }

    public void onMouseReleased() {
        if (getEnable() && isGrabbed) {
            isGrabbed = false;
            CURRENT_GRABBED_CARD = null;
            setZIndex(Z_INDEX_NORMAL); // Optional: Reorder logic if needed

            // handle when drop card on slot
            var slot = getCardSlotOnBottom();
            if (slot != null) {
                snapToSlot(slot);
                onDroppedInSlot(slot);
            }
        }
    }

    // --------------------------------------------------
    // ---------- logic about slot suction --------------
    // --------------------------------------------------
    private CardSlot getCardSlotOnBottom() {
        if (getParent() == null) return null;
        Rectangle cardRect = this.getBounds();

        // Iterate through Swing Components in the parent Container (Scene)
        for (Component comp : getParent().getComponents()) {
            if (comp == this) continue;
            if (!(comp instanceof GameObject)) continue; // Assume GameObject extends Component

            GameObject obj = (GameObject) comp;

            if (!(obj instanceof CardSlot)) continue;

            Rectangle slotMagneticField = new Rectangle(
                    obj.getX() - SNAP_MARGIN,
                    obj.getY() - SNAP_MARGIN,
                    obj.getWidth() + (SNAP_MARGIN * 2),
                    obj.getHeight() + (SNAP_MARGIN * 2)
            );

            if (cardRect.intersects(slotMagneticField)) {
                return (CardSlot) obj;
            }
        }
        return null;
    }

    private void snapToSlot(CardSlot slot) {
        // Set world position to match slot's world position
        this.setPosition(new Point(slot.getPosition()));
        // เรียก method when card ทับ กับ Magnetic Field ของ slot
    }

    /**
     * Xynezter 14/3/2569 14:12 : Update method is non abstract Arcanacards dont need to Override
     **/
    protected boolean isDroppable(Object bottom) {
        return false;
    }

    // add method for business logic when card DroppedInSlot
    protected void onDroppedInSlot(CardSlot slot) {
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Call JPanel paint
        // 1. สร้างก๊อปปี้ของ Graphics เพื่อไม่ให้ Scale ไปกระทบตัวอื่น
        Graphics2D g2d = (Graphics2D) g.create();

        try {
            // --------- Hover Effect --------- //
            if (isHovered && !isGrabbed) {
                // คำนวณจุดศูนย์กลางของ Cards (Local Coordinates)
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;

                float scaleX = (float) (getWidth() + ZOOM_OFFSET) / getWidth();
                float scaleY = (float) (getHeight() + ZOOM_OFFSET) / getHeight();

                // Step การขยายจากจุดศูนย์กลาง:
                g2d.translate(cx, cy);           // 1. เลื่อนจุดศูนย์กลาง Cards ไปที่ 0,0
                g2d.scale(scaleX, scaleY);       // 2. ขยาย
                g2d.translate(-cx, -cy);         // 3. เลื่อนกลับมาตำแหน่งเดิม
            }

            // --------- Drawing Logic --------- //
            if (!getEnable()) g2d.setColor(Color.LIGHT_GRAY);
            else if (isGrabbed) g2d.setColor(new Color(255, 165, 0));
            else g2d.setColor(new Color(176, 255, 183));

            g2d.fillRect(0, 0, getWidth(), getHeight()); // Draw at 0,0

            g2d.setColor(Color.BLACK);
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1); // -1 to see borders

            FontMetrics fm = g2d.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(name)) / 2;
            int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
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
