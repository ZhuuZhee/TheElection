/**
 * @Xynezter 10/3/2026 19:30
 */
package Core.Cards;

import Dummy.Maps.City;

public abstract class PolicyCard extends Card {
    protected boolean isInSlot = false;

    public PolicyCard(String name, int x, int y) {
        super(name, x, y, 100, 150);
    }

    public abstract boolean IsActivate();

    public boolean isInSlot() {
        return isInSlot;
    }

    public abstract void onActionCardPlayed(ActionCard playedCard, City city);

    @Override
    protected void onDroppedInSlot(CardSlot slot) {
        System.out.println(name + " was dropped into a PASSIVE slot!");
        this.isInSlot = true;
        this.isDraggable = false;
    }
}
