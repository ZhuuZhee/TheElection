/**
 * @Xynezter 10/3/2026 19:30
 */
package Core.Cards;

import Core.Cards.Stream.CardBufferObject;
import Core.Player.Player;
import Core.ZhuzheeGame;
import Core.Maps.*;
import ZhuzheeEngine.Scene.GameObject;

import java.awt.*;
import java.util.ArrayList;

// เพิ่ม Attributes List ที่เอาไว้เก็บค่า Effect ของ card
public class ActionCard extends Card {
    //    private final PoliticsStats stats;
    private SmartTooltipUI onDropPreviewUI;
    private Grid currentGrid;
    public ActionCard(CardBufferObject bufferObject, int x, int y) {
        this(bufferObject.getName(), x, y, bufferObject.getStats(), bufferObject.getImgPath(), bufferObject.getCoin());
    }

    public ActionCard(String name, int x, int y, PoliticsStats stats, String imagePath, int coin) {
        super(name, x, y, imagePath); // โยน imagePath ให้ Card จัดการ
        if (stats != null) {
            this.stats = new PoliticsStats(
                    stats.getStats(PoliticsStats.FACILITY),
                    stats.getStats(PoliticsStats.ENVIRONMENT),
                    stats.getStats(PoliticsStats.ECONOMY)
            );
        } else {
            this.stats = null;
        }
        this.coin = coin;
    }

    // getter stat
    public PoliticsStats getStats() {
        return this.stats;
    }

    public String getName() {
        return this.name;
    }

    // โยนให้ city จัดการ stat
    public void ActionOn(City city, PoliticsStats stats) {
        if (!getEnable()) return;

        // ใน City.java มี applyStats(PoliticsStats cardStats) อยู่แล้ว
        String pId = "";
        if (ZhuzheeGame.CLIENT != null && ZhuzheeGame.CLIENT.getLocalPlayer() != null) {
            pId = ZhuzheeGame.CLIENT.getLocalPlayer().getPlayerId();
        }

        city.applyCard(pId, stats);
    }

    @Override
    protected void onDroppedOnGrid(Grid grid) {
        ZhuzheeEngine.Debug.GameLogger.logInfo(name + " was dropped onto Map Grid!");
        City targetCity = grid.getCity();

        if (targetCity != null) {
            // วิ่งไปหาการ์ดนโยบายจาก UI โดยตรงเลย (ไม่ต้องหาจาก scene แล้ว)
            // แก้ไขจาก POLICY_CARD_UI เป็น POLICY_CARD_HAND ตามที่มีใน ZhuzheeGame.java
            PoliticsStats finalStat = new PoliticsStats(this.stats);
            if (ZhuzheeGame.POLICY_CARD_HAND != null) {
                for (Card card : ZhuzheeGame.POLICY_CARD_HAND.getCards()) {
                    if (card instanceof PolicyCard passive) {
                        // เอา isInSlot() ออก เหลือแค่เช็ค IsActivate() อย่างเดียว
                        if (passive.isActive()) {
                            finalStat.addStats(passive.calculateStats(this, targetCity));
                        }
                    }
                }
            }
            grid.triggerFlash();
            showDropEffectPopup(grid, finalStat);

            this.ActionOn(targetCity, finalStat);
            GameObject.Destroy(this);

            this.ActionOn(targetCity, finalStat);
            GameObject.Destroy(this);
        }
        Player playercoin = null;
        if (ZhuzheeGame.CLIENT != null) {
            playercoin = ZhuzheeGame.CLIENT.getLocalPlayer();
        } else {
            // 👉 ใช้กระเป๋าเดียวกันกับ Shop
            playercoin = Dummy.Tester.dummyPlayer;
        }

        if (playercoin != null) {
            playercoin.setCoin(playercoin.getCoin() + this.coin);
            System.out.println("playerCoin: " + playercoin.getCoin());
        }

        // ระบบ Broadcast update ค่าเเมืองส่งไปยัง Server
        if (ZhuzheeGame.CLIENT != null && targetCity != null) {
            ZhuzheeGame.CLIENT.useCard(targetCity);
        }
    }


    @Override
    protected void onHoverGrid(Grid grid, Map mapComponent) {
        super.onHoverGrid(grid, mapComponent);
        if (grid == null) {
            if (currentGrid != null) {
                currentGrid = null;
                hideOnDropPreview(null);
                if (ZhuzheeGame.POLICY_CARD_HAND != null) {
                    ZhuzheeGame.POLICY_CARD_HAND.hideActiveCards();
                }
            }
            return;
        }

        System.out.println("Hover Grid "  + grid.getCity().getCityName());
        if (ZhuzheeGame.POLICY_CARD_HAND != null) {
            ZhuzheeGame.POLICY_CARD_HAND.showActiveCards();
        }

        if(currentGrid != grid){//new grid
            currentGrid = grid;
            hideOnDropPreview(grid);
            showOnDropPreview(grid);
        } else {
            // อัปเดตตำแหน่ง Tooltip ให้ตามเมาส์ขณะขยับอยู่บน Grid เดิม
            updateTooltipPosition();
        }
    }


    private void hideOnDropPreview(Grid grid){
        // --- ปิด Tooltip เมื่อเมาส์ออก ---
        if (onDropPreviewUI != null) {
            System.out.println("hideOnDropPreview");
            scene.remove(onDropPreviewUI); // ถอดออกจากหน้าจอ
            onDropPreviewUI = null;
            scene.repaint();
        }
        // ----------------------------
    }

    /**
     * อัปเดตตำแหน่ง Tooltip ให้แสดงผลตามตำแหน่งเมาส์ปัจจุบันบน Scene
     */
    private void updateTooltipPosition() {
        if (onDropPreviewUI == null || scene == null) return;

        // ดึงตำแหน่งเมาส์สัมพันธ์กับ Scene (JPanel หลักของเกม)
        Point mousePos = scene.getMousePosition();
        
        if (mousePos != null) {
            int x = mousePos.x + 25;
            int y = mousePos.y + 25;

            // ป้องกัน UI ล้นขอบจอขวาและล่าง
            if (x + onDropPreviewUI.getWidth() > scene.getWidth()) {
                x = mousePos.x - onDropPreviewUI.getWidth() - 25;
            }
            if (y + onDropPreviewUI.getHeight() > scene.getHeight()) {
                y = mousePos.y - onDropPreviewUI.getHeight() - 25;
            }

            onDropPreviewUI.setLocation(x, y);
        }
    }

    private void showOnDropPreview(Grid grid) {

        System.out.println("showOnDropPreview");
        PoliticsStats finalStats = new PoliticsStats(stats);
        String description = "";
        if (ZhuzheeGame.POLICY_CARD_HAND == null) return;
        
        for (Card card : new ArrayList<>(ZhuzheeGame.POLICY_CARD_HAND.getCards())) {
            PolicyCard policyCard = (PolicyCard) card;
            if (policyCard.isActive()) {
                PoliticsStats effectStats = policyCard.calculateStats(this, grid.getCity());
                if (effectStats != null) {
                    finalStats.addStats(effectStats);
                    description += "Effect By [%s] : ".formatted(policyCard.getName());

                    int facility = effectStats.getStats(PoliticsStats.FACILITY);
                    if (facility != 0) description += "[+ %d FACILITY]".formatted(facility);
                    int environ = effectStats.getStats(PoliticsStats.ENVIRONMENT);
                    if (environ != 0) description += "[+ %d ENVIRONMENT]".formatted(environ);
                    int eco = effectStats.getStats(PoliticsStats.ECONOMY);
                    if (eco != 0) description += "[+ %d ECONOMY]".formatted(eco);
                }

            }
        }
        onDropPreviewUI = new SmartTooltipUI(finalStats, "On Drop Card", description, true);

        // กำหนดขนาดและตำแหน่งเริ่มต้นอ้างอิงจากตำแหน่งเมาส์
        onDropPreviewUI.setSize(onDropPreviewUI.getPreferredSize());
        updateTooltipPosition();

        // เอาไปแปะบนจอ แล้วดันขึ้นเลเยอร์บนสุด (0)
        onDropPreviewUI.setVisible(true);
        scene.add(onDropPreviewUI);
        scene.setComponentZOrder(onDropPreviewUI, 0);
        scene.repaint();
    }

    @Override
    protected void drawStats(Graphics2D g2d) {
        if (isGrabbed()) return;
        super.drawStats(g2d); // วาด Coin ก่อน

        if (stats == null) return;

        int margin = 7;
        int iconSize = 16;
        int x = getWidth() - margin * 2 - iconSize;
        int startY = getHeight() - margin * 2 - iconSize; // เริ่มวาดจากล่างขึ้นบน

        // ดึงค่าสเตตัสต่างๆ
        int facility = stats.getStats(PoliticsStats.FACILITY);
        int environment = stats.getStats(PoliticsStats.ENVIRONMENT);
        int economy = stats.getStats(PoliticsStats.ECONOMY);

        // วาดค่าสเตตัสจากล่างขึ้นบน (Economy อยู่ล่างสุดถ้ามี)
        int currentY = startY;
        if (economy != 0) {
            drawSingleStat(g2d, economy, new Color(255, 100, 100), x, currentY, iconSize);
            currentY -= (iconSize + 2);
        }
        if (environment != 0) {
            drawSingleStat(g2d, environment, new Color(100, 255, 100), x, currentY, iconSize);
            currentY -= (iconSize + 2);
        }
        if (facility != 0) {
            drawSingleStat(g2d, facility, new Color(100, 100, 255), x, currentY, iconSize);
        }
    }

    private void drawSingleStat(Graphics2D g2d, int value, Color color, int x, int y, int size) {
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.setColor(color);
        g2d.fillOval(x, y, size, size);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(x, y, size, size);

        String text = String.valueOf(value);
        FontMetrics fm = g2d.getFontMetrics();
        // วาดตัวเลขไว้ทางซ้ายของไอคอนเพื่อให้เห็นชัดเจน (หรือจะวาดทับไอคอนก็ได้ถ้าตัวเลขสั้น)
        // เพื่อความสวยงามในแนวตั้ง ให้วาดทับหรือวาดชิดซ้าย

        int textX = x + (size - fm.stringWidth(text)) / 2;
        int textY = y + (size - fm.getHeight()) / 2 + fm.getAscent();

        g2d.setColor(Color.BLACK);
        g2d.drawString(text, textX, textY);
    }

    private void showDropEffectPopup(Grid grid, PoliticsStats finalStat) {
        String desc = "City: " + grid.getCity().getCityName();
        SmartTooltipUI dropPopup = new SmartTooltipUI(finalStat, "CARD APPLIED!", desc, true);
        dropPopup.setSize(dropPopup.getPreferredSize());

        // รวบคำสั่งคำนวณพิกัดให้สั้นลง
        Point screenPoint = javax.swing.SwingUtilities.convertPoint(
                grid.getMap(), (int)grid.getX(), (int)grid.getY(), scene);
        dropPopup.setLocation(screenPoint.x + 20, screenPoint.y - 40);

        scene.add(dropPopup);
        scene.setComponentZOrder(dropPopup, 0);
        scene.repaint();

        // ตั้งเวลาลบทิ้ง
        javax.swing.Timer timer = new javax.swing.Timer(500, e -> {
            scene.remove(dropPopup);
            scene.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }
}
