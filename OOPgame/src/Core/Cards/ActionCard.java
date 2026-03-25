/**
 * @Xynezter 10/3/2026 19:30
 */
package Core.Cards;

import Core.Cards.Stream.CardBufferObject;
import Core.Player.Player;
import Core.ZhuzheeGame;
import Dummy.*;
import Core.Maps.*;
import ZhuzheeEngine.Scene.GameObject;

import java.awt.*;

// เพิ่ม Attributes List ที่เอาไว้เก็บค่า Effect ของ card
public class ActionCard extends Card {
    private final PoliticsStats stats;

    private final static Player dummyPlayer = null; // for test
    // setup Constructor while card builded add stat in stat

    public ActionCard(CardBufferObject bufferObject, int x, int y) {
        this(bufferObject.getName(), x, y, bufferObject.getStats(), bufferObject.getImgPath(), bufferObject.getCoin());
    }

    public ActionCard(String name, int x, int y, PoliticsStats stats, String imagePath, int coin) {
        super(name, x, y, imagePath); // โยน imagePath ให้ Card จัดการ
        this.stats = stats;
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
    public void ActionOn(City city) {
        if (!getEnable()) return;

        city.applyStats(this.stats);

    }

    // ถ้า card ถูกวางใน slot จะดึงข้อมูล เมื่องที่ slot อยู่ และเรียกใช้ ActionOn โยนค่าเข้าเมือง
    @Override
    protected void onDroppedInSlot(CardSlot slot) {
        System.out.println(name + " was dropped into a slot!");
        City targetCity = slot.getCity();

        // if slot have city stat --> add
        if (targetCity != null) {
            // find card in Scene
            for (GameObject obj : scene.getGameObjects()) {
                // check if obj --> Policy
                if (obj instanceof PolicyCard) {
                    // แปลงกลับเป็น PassiveCard เพื่อเรียกใช้ isInSlot() และ onActionCardPlayed()
                    PolicyCard passive = (PolicyCard) obj;
                    // if passivecard is in slot and passivecard isactivate โยนเข้า business logic onActionCardPlayed()
                    if (passive.isInSlot() && passive.IsActivate()) {
                        passive.onActionCardPlayed(this, targetCity);
                    }
                }
            }
            this.ActionOn(targetCity);
            GameObject.Destroy(this);
        }
    }

    @Override
    protected void onDroppedOnGrid(Grid grid) {
        System.out.println(name + " was dropped onto Map Grid!");
        City targetCity = grid.getCity();

        if (targetCity != null) {
            for (GameObject obj : scene.getGameObjects()) {
                if (obj instanceof PolicyCard) {
                    PolicyCard passive = (PolicyCard) obj;
                    if (passive.isInSlot() && passive.IsActivate()) {
                        passive.onActionCardPlayed(this, targetCity);
                    }
                }
            }
            this.ActionOn(targetCity);
            GameObject.Destroy(this);
        }
        Player playercoin = null;
        if (ZhuzheeGame.CLIENT != null) {
            playercoin = ZhuzheeGame.CLIENT.getLocalPlayer();
        } else {
            // 👉 ใช้กระเป๋าเดียวกันกับ Shop
            playercoin = Dummy.Tester.dummyPlayer;
        }

        playercoin.setCoin(playercoin.getCoin() + this.coin);
        System.out.println("playerCoin: " + playercoin.getCoin());

    }

    @Override
    protected void drawStats(Graphics2D g2d) {
        super.drawStats(g2d); // วาด Coin ก่อน

        if (stats == null) return;

        int margin = 7;
        int iconSize = 16;
        int x = getWidth() - margin * 2 - iconSize;
        int startY = getHeight() - margin * 2 - iconSize; // เริ่มวาดจากล่างขึ้นบน

        // ดึงค่าสเตตัสต่างๆ
        int facility = stats.getStats(PoliticsStats.Facility);
        int environment = stats.getStats(PoliticsStats.Environment);
        int economy = stats.getStats(PoliticsStats.Economy);

        // วาดค่าสเตตัสจากล่างขึ้นบน (Economy อยู่ล่างสุดถ้ามี)
        int currentY = startY;
        if (economy != 0) {
            drawSingleStat(g2d, economy, new Color(100, 100, 255), x, currentY, iconSize);
            currentY -= (iconSize + 2);
        }
        if (environment != 0) {
            drawSingleStat(g2d, environment, new Color(100, 255, 100), x, currentY, iconSize);
            currentY -= (iconSize + 2);
        }
        if (facility != 0) {
            drawSingleStat(g2d, facility, new Color(255, 100, 100), x, currentY, iconSize);
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
        int textX = x + fm.stringWidth(text) - size / 2 - 1;
        int textY = y + (size - fm.getHeight()) / 2 + fm.getAscent();

        g2d.setColor(Color.BLACK);
        g2d.drawString(text, textX, textY);
    }

    @Override
    protected boolean isDroppable(Object bottom) {
        return bottom instanceof City;
    }
}
