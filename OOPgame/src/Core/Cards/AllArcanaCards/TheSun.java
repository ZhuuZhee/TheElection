package Core.Cards.AllArcanaCards;

import Core.Cards.ArcanaCard;

public class TheSun extends ArcanaCard {

    public TheSun(int x, int y) {
        super("The Sun", x, y, 0, "OOPgame/Assets/ImageForCards/Arcana Card/Sun.png");
    }

    @Override
    protected void activateSkill() {
        System.out.println("The sun activate!");
        // Business Logic
    }
}
