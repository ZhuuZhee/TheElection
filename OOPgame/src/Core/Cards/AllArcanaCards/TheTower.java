package Core.Cards.AllArcanaCards;

import Core.Cards.ArcanaCard;

public class TheTower extends ArcanaCard {

    public TheTower(int x, int y) {
        super("The Tower", x, y, 4, "OOPgame/Assets/ImageForCards/Arcana Card/Tower.png");
    }

    @Override
    public void onMousePressed(int mouseX, int mouseY) {
        // รอ นับเทริน
        if (this.currentCooldown == 0) {
            System.out.println("Used skill : " + this.name);
            activateSkill();

            // Reset cooldown
            this.currentCooldown = this.maxCooldown;
        } else {
            System.out.println(this.name + " Can't use! wait for " + this.currentCooldown + " turn");
        }
    }

    @Override
    protected void activateSkill() {
        System.out.println("The Wheel Of Fortune activate!");
        // Business Logic
    }
}
