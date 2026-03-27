package Core.Cards.AllArcanaCards;

import Core.Cards.ArcanaCard;

public class TheWheelOfFortune extends ArcanaCard {

    public TheWheelOfFortune(int x, int y) {
        super("The Wheel of Fortune", x, y, 2, "OOPgame/Assets/ImageForCards/Arcana Card/WOF.png");
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
