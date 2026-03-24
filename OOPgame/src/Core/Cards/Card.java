/**
 * @Munin 11/3/2026 20:33 - move mouse listener to this class
 * @Xynezter 23/3/2026 16:54 - confix onmousepressed onMouseReleased
 */
package Core.Cards;

import Core.UI.CardHolderUI;
import Core.ZhuzheeGame;
import ZhuzheeEngine.Scene.*;

import java.awt.*;
import java.awt.event.*;

import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;

public abstract class Card extends GameObject {
    protected String name;
    protected boolean isGrabbed = false;
    protected boolean isDraggable = true;
    public boolean isHovered = false;
    protected Point offset = new Point(0, 0);
    public static Card CURRENT_GRABBED_CARD;
    private static final int Z_INDEX_TOP = Scene2D.Layer.DRAGGED;
    private static final int Z_INDEX_NORMAL = Scene2D.Layer.DEFAULT;
    private static final int SNAP_MARGIN = 15;
    private static final double ZOOM_OFFSET = 1.15;
    protected Image cardImage = null;
    protected String imagePath = "";
    protected int coin;
    private int baseWidth;
    private int baseHeight;
    private int baseX;
    private int baseY;
    public Card(String name, int x, int y, int width, int height) {
        super(x, y, width, height, ZhuzheeGame.MAIN_SCENE);
        this.name = name;
        // Swing Component Setup
        this.setBackground(new Color(0,0,0,0));
        this.setOpaque(true);
        //set size forever
        this.setPreferredSize(new Dimension(width, height));
//        System.out.println("--------------------");
//        System.out.println(name + " : enable : " + getEnable());
//        System.out.println("--------------------");
        this.baseWidth = width;
        this.baseHeight = height;
        this.baseX = x;
        this.baseY = y;

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

//            @Override
//            public void mouseClicked(MouseEvent e) {
//                onMouseClick();
//                scene.getCamera().LerpCameraTo(getPosition(),1);
//                scene.getCamera().LerpZoom(2,1);
//            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setHovered(true);
                if (!isGrabbed && getEnable() && !(getParent() instanceof CardHolderUI)) {
                    setZIndex(Z_INDEX_TOP);
                    if (getParent() != null) getParent().setComponentZOrder(Card.this, 0);

                    setBounds(baseX, baseY, baseWidth, baseHeight);
                }
//                repaint(); // Swing needs repaint trigger
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setHovered(false);
                if (!isGrabbed && getEnable() && !(getParent() instanceof CardHolderUI)) {
                    setZIndex(Z_INDEX_NORMAL);

                    setBounds(baseX, baseY, baseWidth, baseHeight);
                }
//                repaint();
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                onMouseDragged(e.getX(), e.getY());
            }
        });
    }

    public Card(String name, int x, int y, int width, int height, String imagePath) {
        this(name, x, y, width, height);
        this.setImage(imagePath);
    }

    //------------ setter getter -------------
    public void setHovered(boolean hovered) {
        this.isHovered = hovered;
    }

    public void setDraggable(boolean draggable) {
        this.isDraggable = draggable;
    }

    public int getCoin() {
        return this.coin;
    }

    public void setCoin(int cost) {
        this.coin = cost;
    }

    public void setImage(String imagePath) {
        try {
            this.cardImage = ImageIO.read(new File(imagePath));
            this.imagePath = imagePath;
            repaint(); // สั่งให้วาดใหม่เมื่อโหลดรูปเสร็จ
        } catch (Exception e) {
            System.err.println("ไม่สามารถโหลดรูปภาพได้จาก path: " + imagePath);
            e.printStackTrace();
        }
    }
    public String getImagePath(){
        return imagePath;
    }
    // ----------------------------------------
    // ------------  Mouse Events  ------------
    // ----------------------------------------

    public void onMousePressed(int mouseX, int mouseY) {
        if (getEnable()) {
            // No need to check boundaries, event is fired on component
            if(getParent() != null && getParent().getParent() instanceof CardHolderUI holderUI){
                holderUI.removeCard(this);
            }
            System.out.println(name + ": Grabbed");
            if (isDraggable) {
                isGrabbed = true;
                CURRENT_GRABBED_CARD = this;
                // Store the click point relative to the card's top-left
                offset.x = mouseX;
                offset.y = mouseY;

                setZIndex(Z_INDEX_TOP);
//                scene.setComponentZOrder(this,0);
            }
        }
    }

    public void onMouseClick() {
    }

    public void onMouseDragged(int mouseX, int mouseY) {
        if (getEnable() && isGrabbed) {
            // 1. Calculate target Screen Position
            Point pos = getLocationOnScreen();
            int targetScreenX = pos.x + mouseX - offset.x;
            int targetScreenY = pos.y + mouseY - offset.y;

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

            this.isHovered = false;
            // เพื่อให้เมธอด setBounds ที่เรา Override ไว้ทำงานในโหมดปกติ
            this.setBounds(baseX, baseY, baseWidth, baseHeight);
            setZIndex(Z_INDEX_NORMAL);
            if (getParent() != null && !(getParent().getLayout() instanceof FlowLayout)) {
                setZIndex(Z_INDEX_NORMAL);
            }

            CardHolderUI handUI = getHandUIOnBottom();
            if (handUI != null) {
                // ล้างพิกัดก่อนเข้ามือ เพื่อให้ FlowLayout จัดเรียงใหม่ได้ถูกต้อง
                this.setLocation(0, 0);
                handUI.addCard(this);
                handUI.revalidate();
                handUI.repaint();
                return;
            }

            // handle when drop card on slot
            var slot = getCardSlotOnBottom();
            if (slot != null) {
                snapToSlot(slot);
                onDroppedInSlot(slot);
            }
        }
    }
    // check ว่า card ชนกับขอบของ yourhand มั้ย " ให้ card เป้นตัวเช็ค "
    private CardHolderUI getHandUIOnBottom() {
        if (getParent() == null) return null;
        Rectangle cardRect = this.getBounds();

        // วนหา Component ใน Scene
        for (Component comp : getParent().getComponents()) {
            if (comp instanceof CardHolderUI) {
                Rectangle handRect = comp.getBounds();
                // ถ้ากล่องของการ์ด ตัดกับ(ทับ) กล่องของ Hand UI
                if (cardRect.intersects(handRect)) {
                    return (CardHolderUI) comp;
                }
            }
        }
        return null;
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

            // --------- Drawing Logic --------- //
            if (cardImage != null) {
                // ถ้ามีรูปภาพ ให้วาดรูปลงไปให้เต็มขนาดการ์ด
                g2d.drawImage(cardImage, 0, 0, getWidth(), getHeight(), null);

                // สร้าง Overlay สีใสๆ เพื่อบอกสถานะของการ์ด (ทับบนรูปอีกที)
                if (!getEnable()) {
                    g2d.setColor(new Color(128, 128, 128, 150)); // สีเทาโปร่งแสง
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                } else if (isGrabbed) {
                    g2d.setColor(new Color(255, 165, 0, 100)); // สีส้มโปร่งแสง
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            } else {
                // ถ้าไม่มีรูปภาพ ให้ใช้สีพื้นฐานแบบเดิม
                if (!getEnable()) g2d.setColor(Color.LIGHT_GRAY);
                else if (isGrabbed) g2d.setColor(new Color(255, 165, 0));
                else g2d.setColor(new Color(176, 255, 183));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }

            // วาดกรอบสีดำ
            g2d.setColor(Color.BLACK);
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

            // วาดชื่อการ์ด
            FontMetrics fm = g2d.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(name)) / 2;
            int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

            // พื้นหลังตัวหนังสือแบบโปร่งแสง
            g2d.setColor(new Color(255, 255, 255, 180));
            g2d.fillRect(textX - 2, textY - fm.getAscent() - 2, fm.stringWidth(name) + 4, fm.getHeight() + 4);

            g2d.setColor(Color.BLACK);
            g2d.drawString(name, textX, textY);
        } finally {
            // 2. ทำลายก๊อปปี้ทิ้ง เพื่อคืนค่าเดิมให้ Graphics หลักสำหรับ Cards ใบถัดไป
            g2d.dispose();
        }
    }

    public String getName() {
        return this.name;
    }

    public boolean isHovered() {
        return this.isHovered;
    }

    public boolean isGrabbed() {
        return this.isGrabbed;
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        this.baseX = x;
        this.baseY = y;
        // ถ้าระบบ Engine พยายามจะรีเซ็ตขนาดการ์ดขณะที่เราเอาเมาส์ชี้อยู่ (และไม่ได้อยู่ในมือ)
        if (isHovered && !isGrabbed && !(getParent() instanceof CardSlot)) {
            int zoomedWidth = (int) (baseWidth * ZOOM_OFFSET);
            int zoomedHeight = (int) (baseHeight * ZOOM_OFFSET);
            int shiftX = (zoomedWidth - baseWidth) / 2;
            int shiftY = (zoomedHeight - baseHeight) / 2;

            super.setBounds(x - shiftX, y - shiftY, zoomedWidth, zoomedHeight);
        } else {
            // ถ้าอยู่ใน CardSlot หรือไม่ได้ Hover ให้ใช้ขนาดปกติ
            super.setBounds(x, y, width, height);
        }
    }

    public void setBaseWidth(int width)
    {
        baseWidth = width;
    }
    public void setBaseHeight(int height){
        baseHeight = height;
    }
}
