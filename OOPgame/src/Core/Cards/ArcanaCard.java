/**
 * @Xynezter 10/3/2026 19:30
 */
/// just create class do nothing
package Core.Cards;

public abstract class ArcanaCard extends PolicyCard {

    public ArcanaCard(String name, int x, int y, boolean enabled) {
        super(name, x, y, enabled);
    }

    @Override
    protected boolean isDroppable(Object bottom) {
        return false;
    }

    @Override
    protected void onDroppedInSlot(CardSlot slot) {
        return;
    }
}
