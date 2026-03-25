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

// เพิ่ม Attributes List ที่เอาไว้เก็บค่า Effect ของ card
public class ActionCard extends Card {
    private PoliticsStats stats;

    private static Player dummyPlayer = null; // for test
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
    protected boolean isDroppable(Object bottom) {
        return bottom instanceof City;
    }
}
