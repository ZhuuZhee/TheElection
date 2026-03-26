/**
 * @Munin 11/3/2026 20:33 - move mouse listener to this class
 * @Xynezter 23/3/2026 16:54 - confix onmousepressed onMouseReleased
 */
package Core.Cards;

import Core.Maps.Grid;
import Core.Maps.Map;
import Core.UI.CardHolderUI;
import Core.ZhuzheeGame;
import ZhuzheeEngine.Application;
import ZhuzheeEngine.Scene.*;

import java.awt.*;
import java.awt.event.*;

import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public abstract class Card extends GameObject {
    protected String name;
    private static final int CARD_WIDTH = 100;
    private static final int CARD_HEIGHT = 150;
    protected boolean isGrabbed = false;
    protected boolean isDraggable = true;
    public boolean isHovered = false;
    private static final int MARGIN = 2;
    protected Point offset = new Point(0, 0);
    public static Card CURRENT_GRABBED_CARD;
    private static final int Z_INDEX_TOP = Scene2D.Layer.DRAGGED;
    private static final int Z_INDEX_NORMAL = Scene2D.Layer.DEFAULT;
    private static final int SNAP_MARGIN = 5;
    private static final double DEFAULT_OFFSET = 1.0; // ขนาดตอนปกติ
    private static final double ZOOM_OFFSET = 1.15;
    private static final double GRAB_OFFSET = 0.85; // อัตราส่วนตอนกำลังหยิบการ์ดลาก (ลดเหลือ 85%)
    protected Image cardImage = null;
    protected String imagePath = "";
    protected int coin;
    private int baseWidth;
    private int baseHeight;
    private int baseX;
    private int baseY;

    private double currentScaleOffset = DEFAULT_OFFSET;
    private int customTargetWidth = 0;
    private int customTargetHeight = 0;

    public Card(String name, int x, int y, String imagePath){
        this(name,x,y, CARD_WIDTH, CARD_HEIGHT, imagePath);
    }
    public Card(String name, int x, int y, int width, int height) {
        this(name, x, y, width, height, "");
    }
    public Card(String name, int x, int y, int width, int height, String imagePath) {
        super(x, y, width, height, ZhuzheeGame.MAIN_SCENE);
        this.name = name;
        // Swing Component Setup
        this.setBackground(new Color(0, 0, 0, 0));
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
        this.setImage(imagePath);
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
            public void mouseEntered(MouseEvent e) {
                setHovered(true);
                if (!isGrabbed && getEnable()) {
                    boolean isInHand = (getParent() != null && getParent().getParent() instanceof CardHolderUI);

                    if (!isInHand) {
                        setZIndex(Z_INDEX_TOP);
                        if (getParent() != null) getParent().setComponentZOrder(Card.this, 0);
                    } else {
                        // บังคับอัปเดตขนาดเวลาอยู่ในมือโดยไม่สลับลำดับ Layer ของ FlowLayout
                        setBounds(baseX, baseY, baseWidth, baseHeight);
                        if (getParent() != null) getParent().repaint();
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setHovered(false);
                if (!isGrabbed && getEnable()) {
                    boolean isInHand = (getParent() != null && getParent().getParent() instanceof CardHolderUI);

                    if (!isInHand) {
                        setZIndex(Z_INDEX_NORMAL);
                    } else {
                        setBounds(baseX, baseY, baseWidth, baseHeight);
                        if (getParent() != null) getParent().repaint();
                    }
                }
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

    public String getImagePath() {
        return imagePath;
    }
    // ----------------------------------------
    // ------------  Mouse Events  ------------
    // ----------------------------------------

    public void onMousePressed(int mouseX, int mouseY) {
        if (getEnable()) {
            // No need to check boundaries, event is fired on component
            if (getParent() != null && getParent().getParent() instanceof CardHolderUI holderUI) {
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
                repaint();
            }
        }
    }

    public void onMouseDragged(int mouseX, int mouseY) {
        if (getEnable() && isGrabbed) {
            // 1. Calculate target Screen Position
            Point pos = getLocation();
            int targetScreenX = pos.x + mouseX - offset.x;
            int targetScreenY = pos.y + mouseY - offset.y;

            // 2. Convert Screen to World (Logic is in Scene/Camera)
            Point worldPos = scene.Screen2WorldPoint(new Point(targetScreenX, targetScreenY));

            // 3. Update World Position (Scene will update Swing Location in next paint)
            this.setPosition(worldPos);

            // 4. Update Grid Hover state
            updateGridHover();

            // 5. Force repaint to update layout instantly
            scene.repaint();
        }
    }

    // เอาไว้เช็คว่า Component นั้นมาจาก Map ของ folder Maps
    private void updateGridHover() {
        if (getParent() == null) return;
        Rectangle cardRect = this.getBounds();
        Point cardCenter = new Point(cardRect.x + cardRect.width / 2, cardRect.y + cardRect.height / 2);

        for (Component comp : getParent().getComponents()) {
            if (comp instanceof Map mapComponent) {
                Point mapPos = SwingUtilities.convertPoint(getParent(), cardCenter, mapComponent);
                Grid grid = mapComponent.getGridAtPoint(mapPos);
                mapComponent.setHoveredGrid(grid);
            }
        }
    }

    // เอาไว้ clear hover state ของ Grid ที่อยู่ใน Map
    private void clearMapHover() {
        if (getParent() == null) return;
        for (Component comp : getParent().getComponents()) {
            if (comp instanceof Map mapComponent) {
                mapComponent.clearHoveredGrid();
            }
        }
    }

    public void onMouseReleased() {
        if (getEnable() && isGrabbed) {
            isGrabbed = false;
            CURRENT_GRABBED_CARD = null;

            this.isHovered = false;
            // Clear Map Hover
            clearMapHover();
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

                // บังคับให้ขนาดกลับมาเป็นขนาดฐาน เพื่อให้ CardHolderUI หาร ratio ได้สมบูรณ์ ไม่ติดบั๊กของขนาด 0.85
                this.currentScaleOffset = DEFAULT_OFFSET;
                this.setBounds(0, 0, baseWidth, baseHeight);

                handUI.addCard(this);
                handUI.revalidate();
                handUI.repaint();
                return;
            }

            var grid = getGridOnBottom();
            if (grid != null) {
                snapToGrid(grid);
                onDroppedOnGrid(grid);
                return;
            }
            repaint();
        }
    }

    // check ว่า card ชนกับขอบของ your hand มั้ย " ให้ card เป้นตัวเช็ค "
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

    private Grid getGridOnBottom() {
        if (getParent() == null) return null;
        Rectangle cardRect = this.getBounds();
        Point cardCenter = new Point(cardRect.x + cardRect.width / 2, cardRect.y + cardRect.height / 2);

        for (Component comp : getParent().getComponents()) {
            if (comp instanceof Map mapComponent) {
                Point mapPos = SwingUtilities.convertPoint(getParent(), cardCenter, mapComponent);
                Grid grid = mapComponent.getGridAtPoint(mapPos);
                if (grid != null) {
                    return grid;
                }
            }
        }
        return null;
    }

    private void snapToGrid(Grid grid) {
        for (Component comp : getParent().getComponents()) {
            if (comp instanceof Map mapComponent) {
                // พิกัดจุดกึ่งกลางของ Grid เมื่อเทียบกับมุมซ้ายบนของตัว Map Component
                Point gridCenterLocal = new Point((int) grid.getX(), (int) grid.getY());

                // แปลงพิกัดจากใน Map ไปเป็นพิกัดหน้าจอ (หน้าของ Scene2D)
                Point screenP = SwingUtilities.convertPoint(mapComponent, gridCenterLocal, getParent());

                // แปลงหน้าจอกลับเป็นพิกัดโลก (World Position)
                Point worldP = scene.Screen2WorldPoint(screenP);

                // ชดเชยพิกัดให้เป็นจุดกึ่งกลางของการ์ดพอดี
                int worldX = worldP.x - (this.baseWidth / 2);
                int worldY = worldP.y - (this.baseHeight / 2);

                this.setPosition(new Point(worldX, worldY));
                break;
            }
        }
    }

    /**
     * Xynezter 14/3/2569 14:12 : Update method is non abstract Arcanacards dont need to Override
     **/
    protected boolean isDroppable(Object bottom) {
        return false;
    }
    protected void onDroppedOnGrid(Grid grid) {
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
                    // ปรับสีตอนลากการ์ด แนะนำให้ใช้สีขาวโปร่งแสงเบาๆ ดูพรีเมียมกว่าส้ม/เหลือง
                    g2d.setColor(new Color(255, 255, 255, 80));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            } else {
                // ถ้าไม่มีรูปภาพ ให้ใช้สีพื้นฐานแบบเดิม
                if (!getEnable()) g2d.setColor(Color.LIGHT_GRAY);
                else if (isGrabbed) g2d.setColor(new Color(255, 255, 255)); // ถือใบเปล่าอยู่ ให้ไฮไลต์สว่างๆ
                else g2d.setColor(new Color(176, 255, 183));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }

            // วาดกรอบสีดำ
            g2d.setColor(Color.BLACK);
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

            // วาดชื่อการ์ด
            if (!isGrabbed) {
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(name)) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

                // พื้นหลังตัวหนังสือแบบโปร่งแสง
                g2d.setColor(new Color(255, 255, 255, 180));
                g2d.fillRect(textX - 2, textY - fm.getAscent() - 2, fm.stringWidth(name) + 4, fm.getHeight() + 4);

                g2d.setColor(Color.BLACK);
                g2d.drawString(name, textX, textY);
            }

            // วาดค่า Stat เฉพาะของแต่ละประเภทการ์ด
            drawStats(g2d);
        } finally {
            // 2. ทำลายก๊อปปี้ทิ้ง เพื่อคืนค่าเดิมให้ Graphics หลักสำหรับ Cards ใบถัดไป
            g2d.dispose();
        }
    }

    protected void drawStats(Graphics2D g2d) {
        if (isGrabbed) return;

        // วาดค่า Coin (Cost) ที่มุมบนขวาของการ์ด
        int iconSize = 20;
        int margin = 5;
        int x = getWidth() - margin * 2 - iconSize;
        int y = margin * 2;
        String coinStr;
        // วาดวงกลมสีทองสำหรับเหรียญ

        if (coin > 0) {
            g2d.setColor(new Color(255, 215, 0));
            coinStr = String.valueOf(coin);// Gold
        } else {
            g2d.setColor(new Color(255, 0, 0));
            coinStr = String.valueOf(coin * -1);
        }
        if (coin != 0) {
            g2d.fillOval(x, y, iconSize, iconSize);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(x, y, iconSize, iconSize);

            // วาดค่าตัวเลข coin
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x + (iconSize - fm.stringWidth(coinStr)) / 2;
            int textY = y + (iconSize - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(coinStr, textX, textY);
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
    public void update() {
        super.update();

        double targetScaleOffset = DEFAULT_OFFSET;
        if (isHovered && !isGrabbed && !(getParent() instanceof CardSlot)) {
            targetScaleOffset = ZOOM_OFFSET;
        } else if (isGrabbed) {
            targetScaleOffset = GRAB_OFFSET;
        }

        if (Math.abs(currentScaleOffset - targetScaleOffset) > 0.001) {
            // Lerp แอนิเมชันความเร็ว 15 (ยิ่งเยอะยิ่งไว 15 คือประมาณ 0.2s ease)
            currentScaleOffset += (targetScaleOffset - currentScaleOffset) * 10.0f * Application.getDeltaTime();
            
            // ใช้ Math.max/min แทน Math.clamp เพื่อรองรับ Java ต่ำกว่า 21
            // และใช้ getWidth() ที่เป็นขนาดปัจจุบัน (Scaled Size) เพื่อกันเมาส์หลุดขอบ
            int currentW = Math.max(1, getWidth() - MARGIN); // กัน 0
            int currentH = Math.max(1, getHeight() - MARGIN);
            int maxMouseOffsetX = Math.max(0, Math.min(offset.x + MARGIN, currentW));
            int maxMouseOffsetY = Math.max(0, Math.min(offset.y + MARGIN, currentH));
            offset.setLocation(maxMouseOffsetX, maxMouseOffsetY);

            if (Math.abs(currentScaleOffset - targetScaleOffset) <= 0.001) {
                currentScaleOffset = targetScaleOffset; // Snap ให้เป๊ะตอนจบ
            }

            if (customTargetWidth > 0 && customTargetHeight > 0) {
                int finalWidth = (int) (customTargetWidth * currentScaleOffset);
                int finalHeight = (int) (customTargetHeight * currentScaleOffset);
                int shiftX = (finalWidth - customTargetWidth) / 2;
                int shiftY = (finalHeight - customTargetHeight) / 2;
                super.setBounds(baseX - shiftX, baseY - shiftY, finalWidth, finalHeight);
            }
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        this.baseX = x;
        this.baseY = y;
        this.customTargetWidth = width;
        this.customTargetHeight = height;

        int finalWidth = (int) (width * currentScaleOffset);
        int finalHeight = (int) (height * currentScaleOffset);
        int shiftX = (finalWidth - width) / 2;
        int shiftY = (finalHeight - height) / 2;

        super.setBounds(x - shiftX, y - shiftY, finalWidth, finalHeight);
    }

}
