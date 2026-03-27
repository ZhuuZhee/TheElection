package Core.Cards.AllArcanaCards;

import Core.Cards.ArcanaCard;

public class TheFoolCard extends ArcanaCard {

    public TheFoolCard(int x, int y) {
        // สามารถกำหนด maxCooldown ได้จาก contractor ตรงนี้ได้เลย
        super("TheFoolCard", x, y, 3, "OOPgame/Assets/ImageForCards/Arcana Card/TheFool.png");
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
        System.out.println("The Fool activate!");
        // Business Logic
    }
}
