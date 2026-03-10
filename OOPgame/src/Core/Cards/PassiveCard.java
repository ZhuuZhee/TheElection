/**
 * @Xynezter 10/3/2026 19:30
 */
package Core.Cards;

public abstract class PassiveCard extends Card {
    protected boolean isInSlot = false;

    public PassiveCard(String name, int x, int y, boolean enabled) {
        super(name, x, y, 100, 150, enabled);
    }

    public abstract boolean IsActivate();

    public boolean isInSlot() {
        return isInSlot;
    }

    public abstract void onActionCardPlayed(ActionCard playedCard, Dummy.Citybanna city);

    @Override
    protected void onDroppedInSlot(CardSlot slot) {
        System.out.println(name + " was dropped into a PASSIVE slot!");
        this.isInSlot = true;
        this.enabled = false;
        this.isDraggable = false;
    }
}
