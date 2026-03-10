/**
 * @Xynezter 10/3/2026 19:30
 */
/// just create class do nothing
package Core.Cards;

public abstract class ArcanaCard extends Card {

    public ArcanaCard(String name, int x, int y, int width, int height, boolean enabled) {
        super(name, x, y, 100, 150, enabled);
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
