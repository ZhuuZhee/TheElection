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
import javax.swing.Timer;

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
            ZhuzheeGame.CLIENT.getLocalPlayer().useCard();
        }

        city.applyCard(pId, stats);
    }

    @Override
    protected void onDroppedOnGrid(Grid grid) {
        ZhuzheeEngine.Debug.GameLogger.logInfo(name + " was dropped onto Map Grid!");
        City targetCity = grid.getCity();

        Player playercoin = ZhuzheeGame.CLIENT.getLocalPlayer();

        if (playercoin != null && (playercoin.getCoin() + this.coin) < 0) {
            ZhuzheeEngine.Debug.GameLogger.logWarning("เงินไม่พอลงการ์ด! (ขาดอีก $" + Math.abs(playercoin.getCoin() + this.coin) + ")");

            if (ZhuzheeGame.DEVLOPMENT_CARD_HAND != null) {
                ZhuzheeGame.DEVLOPMENT_CARD_HAND.addCard(this);
            }
            return;
        }

        ZhuzheeEngine.Debug.GameLogger.logInfo(name + " was dropped onto Map Grid!");

        if (targetCity != null) {
            // หักเงินผู้เล่นทันทีที่รู้ว่าลงเมืองสำเร็จ
            if (playercoin != null) {
                playercoin.setCoin(playercoin.getCoin() + this.coin);
                System.out.println("playerCoin เหลือ: " + playercoin.getCoin());

                // อัปเดตเงิน
                if (ZhuzheeGame.PLAYER_COIN_UI != null) {
                    ZhuzheeGame.PLAYER_COIN_UI.updateCoinDisplay();
                }
            }

            // คำนวณบัฟจาก Policy Card ที่มีในมือ
            PoliticsStats finalStat = new PoliticsStats(this.stats);
            if (ZhuzheeGame.POLICY_CARD_HAND != null) {
                for (Card card : ZhuzheeGame.POLICY_CARD_HAND.getCards()) {
                    if (card instanceof PolicyCard passive) {
                        if (passive.isActive()) {
                            finalStat.addStats(passive.calculateStats(this, targetCity));
                        }
                    }
                }
            }

            grid.triggerFlash();
            showDropEffectPopup(grid, finalStat);

            // ส่งผลลัพธ์ให้เมือง และทำลายการ์ดทิ้ง
            this.ActionOn(targetCity, finalStat);
            GameObject.Destroy(this);
        }

        // ระบบ Broadcast update ค่าเมืองส่งไปยัง Server (ถ้าเล่นออนไลน์)
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
                hideOnDropPreview();
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

        if (currentGrid != grid) {//new grid
            currentGrid = grid;
            hideOnDropPreview();
            showOnDropPreview(grid);
        } else {
            // อัปเดตตำแหน่ง Tooltip ให้ตามเมาส์ขณะขยับอยู่บน Grid เดิม
            updateTooltipPosition();
        }
    }


    private void hideOnDropPreview() {
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

    private static java.awt.Image ecoImg;
    private static java.awt.Image envImg;
    private static java.awt.Image facilImg;

    static {
        try {
            ecoImg = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/eco.png"));
            envImg = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/env.png"));
            facilImg = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/facil.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            drawSingleStat(g2d, economy, ecoImg, x, currentY, iconSize);
            currentY -= (iconSize + 2);
        }
        if (environment != 0) {
            drawSingleStat(g2d, environment, envImg, x, currentY, iconSize);
            currentY -= (iconSize + 2);
        }
        if (facility != 0) {
            drawSingleStat(g2d, facility, facilImg, x, currentY, iconSize);
        }
    }

    private void drawSingleStat(Graphics2D g2d, int value, java.awt.Image iconImg, int x, int y, int size) {
        g2d.setFont(g2d.getFont().deriveFont(java.awt.Font.BOLD, 12f));

        if (iconImg != null) {
            g2d.drawImage(iconImg, x, y, size, size, null);
        } else {
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillOval(x, y, size, size);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(x, y, size, size);
        }

        String text = String.valueOf(value);
        FontMetrics fm = g2d.getFontMetrics();

        int textX = x + (size - fm.stringWidth(text)) / 2;
        int textY = y + (size - fm.getHeight()) / 2 + fm.getAscent();

        g2d.setColor(Color.BLACK);
        // วาดเงาให้ตัวหนังสืออ่านง่ายขึ้นถ้าทับรูป
        g2d.drawString(text, textX + 1, textY + 1);
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, textX, textY);
    }

    private void showDropEffectPopup(Grid grid, PoliticsStats finalStat) {
        City targetCity = grid.getCity();
        int oldFac = targetCity.stats.getStats(PoliticsStats.FACILITY);
        int oldEnv = targetCity.stats.getStats(PoliticsStats.ENVIRONMENT);
        int oldEco = targetCity.stats.getStats(PoliticsStats.ECONOMY);

        int newFac = oldFac + finalStat.getStats(PoliticsStats.FACILITY);
        int newEnv = oldEnv + finalStat.getStats(PoliticsStats.ENVIRONMENT);
        int newEco = oldEco + finalStat.getStats(PoliticsStats.ECONOMY);

        String desc = "City: " + targetCity.getCityName();

        int[] oldStatsArr = new int[]{oldFac, oldEnv, oldEco};
        int[] newStatsArr = new int[]{newFac, newEnv, newEco};

        SmartTooltipUI dropPopup = new SmartTooltipUI(finalStat, "CARD APPLIED!", desc, true, oldStatsArr, newStatsArr);
        dropPopup.setSize(dropPopup.getPreferredSize());

        // รวบคำสั่งคำนวณพิกัดให้สั้นลง
        Point screenPoint = javax.swing.SwingUtilities.convertPoint(
                grid.getMap(), (int)grid.getX(), (int)grid.getY(), scene);
        dropPopup.setLocation(screenPoint.x + 20, screenPoint.y - 40);

        scene.add(dropPopup);
        scene.setComponentZOrder(dropPopup, 0);
        scene.repaint();

        // ตั้งเวลาลบทิ้ง
        Timer timer = new Timer(1500, _ -> {
            scene.remove(dropPopup);
            scene.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }
}
