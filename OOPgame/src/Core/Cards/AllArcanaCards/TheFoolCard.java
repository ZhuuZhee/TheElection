package Core.Cards.AllArcanaCards;

import Core.Cards.ArcanaCard;

public class TheFoolCard extends ArcanaCard {

    public TheFoolCard(int x, int y) {
        // สามารถกำหนด maxCooldown ได้จาก contractor ตรงนี้ได้เลย
        super("TheFoolCard", x, y, 3, "OOPgame/Assets/ImageForCards/Arcana Card/TheFool.png");
    }

    @Override
    protected void activateSkill() {
        System.out.println("The Fool activate!");
        // Business Logic
    }
}
