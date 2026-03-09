package Core.Card;
import Dummy.*;

import java.util.List;

public class ActionCard extends Card {
    private static final int CARD_WIDTH = 100;
    private static final int CARD_HEIGHT = 150;
    private List<Integer> stat;

    public ActionCard(String name, int x, int y, boolean enabled, List<Integer> stat) {
        super(name, x, y, CARD_WIDTH, CARD_HEIGHT, enabled);
        this.stat = stat;
    }

    public void ActionOn(Citybanna city) {
        if (!enabled) return;

        city.applyStats(this.stat);

    }

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
