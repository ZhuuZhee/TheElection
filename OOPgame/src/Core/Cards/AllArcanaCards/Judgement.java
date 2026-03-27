package Core.Cards.AllArcanaCards;

import Core.Cards.ArcanaCard;

public class Judgement extends ArcanaCard {

    public Judgement(int x, int y) {
        super("Judgement", x, y, 5, "OOPgame/Assets/ImageForCards/Arcana Card/Judgemant.png");
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
