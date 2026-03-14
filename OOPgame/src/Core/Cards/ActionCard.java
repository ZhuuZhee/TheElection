/**
 * @Xynezter 10/3/2026 19:30
 */
package Core.Cards;
import Dummy.*;
import Dummy.Maps.*;
import ZhuzheeEngine.Scene.GameObject;

// เพิ่ม Attributes List ที่เอาไว้เก็บค่า Effect ของ card
public class ActionCard extends Card {
    private static final int CARD_WIDTH = 100;
    private static final int CARD_HEIGHT = 150;
    private PoliticsStats stats;
    // setup Constructor while card builded add stat in stat
    public ActionCard(String name, int x, int y, PoliticsStats stats) {
        super(name, x, y, CARD_WIDTH, CARD_HEIGHT);
        this.stats = stats;
    }
    // getter stat
    public PoliticsStats getStats() {
        return this.stats;
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
            this.setEnable(false);
            this.isDraggable = false;
        }
    }

    @Override
    protected boolean isDroppable(Object bottom) {
        return bottom instanceof Citybanna;
    }
}
