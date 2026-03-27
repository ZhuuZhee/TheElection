package Core.Cards.AllArcanaCards;

import Core.Cards.ArcanaCard;

public class TheTower extends ArcanaCard {

    public TheTower(int x, int y) {
        super("The Tower", x, y, 4, "OOPgame/Assets/ImageForCards/Arcana Card/Tower.png");
    }

    @Override
    protected void activateSkill() {
        System.out.println("The Tower activate!");
        // Business Logic
    }
}
