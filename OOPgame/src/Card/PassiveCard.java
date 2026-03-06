package Card;

public abstract class PassiveCard extends Card {

    public PassiveCard(String name, int x, int y, boolean enabled) {
        super(name, x, y, 100, 150, enabled);
    }

    public boolean IsActivate() {
        return this.enabled;
    }
}
