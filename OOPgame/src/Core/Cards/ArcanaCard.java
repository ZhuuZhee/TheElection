/**
 * @Xynezter 14/3/2026 15:03
 */
package Core.Cards;

import Core.UI.ArcanaCardHolderUI;

public abstract class ArcanaCard extends Card {
    protected final int maxCooldown;
    protected int currentCooldown;

    public ArcanaCard(String name, int x, int y, int maxCooldown, String imagePath) {
        super(name, x, y, 100, 150, imagePath);
        this.maxCooldown = maxCooldown;
        this.currentCooldown = 0;
    }

    protected abstract void activateSkill();

    @Override
    public abstract void onMousePressed(int mouseX, int mouseY);

    // รอ นับเทริน เรียกใช้
    public void decreaseCooldown() {
        if (this.currentCooldown > 0) {
            this.currentCooldown--;
        }
    }
}