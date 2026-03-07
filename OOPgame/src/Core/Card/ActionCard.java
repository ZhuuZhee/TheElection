package Core.Card;
import Dummy.*;
public class ActionCard extends Card {
    private static final int CARD_WIDTH = 100;
    private static final int CARD_HEIGHT = 150;

    public ActionCard(String name, int x, int y, boolean enabled) {
        super(name, x, y, CARD_WIDTH, CARD_HEIGHT, enabled);
    }

    public void ActionOn(Citybanna city) {
        if (!enabled) return;
    }

    @Override
    protected boolean isDroppable(Object bottom) {
        if (bottom instanceof Citybanna) {
            return true;
        }
        return false;
    }
}
