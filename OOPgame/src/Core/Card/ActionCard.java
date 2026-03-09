/**
 * @Xynezter 9/3/2026 18:50
 */
package Core.Card;
import Dummy.*;

import java.util.List;
// เพิ่ม Attributes List ที่เอาไว้เก็บค่า Effect ของ card
public class ActionCard extends Card {
    private static final int CARD_WIDTH = 100;
    private static final int CARD_HEIGHT = 150;
    private List<Integer> stat;
    // setup Constructor while card builded add stat in stat
    public ActionCard(String name, int x, int y, boolean enabled, List<Integer> stat) {
        super(name, x, y, CARD_WIDTH, CARD_HEIGHT, enabled);
        this.stat = stat;
    }
    // โยนให้ city จัดการ stat
    public void ActionOn(Citybanna city) {
        if (!enabled) return;

        city.applyStats(this.stat);

    }
    // ถ้า card ถูกวางใน slot จะดึงข้อมูล เมื่องที่ slot อยู่ และเรียกใช้ ActionOn โยนค่าเข้าเมือง
    @Override
    protected void onDroppedInSlot(CardSlot slot) {
        System.out.println(name + " was dropped into a slot!");
        Citybanna targetCity = slot.getCity();

        // if slot have city stat --> add
        if (targetCity != null) {
            this.ActionOn(targetCity);
            this.enabled = false;
            this.isDraggable = false;
        }
    }

    @Override
    protected boolean isDroppable(Object bottom) {
        if (bottom instanceof Citybanna) {
            return true;
        }
        return false;
    }
}
