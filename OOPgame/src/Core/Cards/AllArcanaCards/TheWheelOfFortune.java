package Core.Cards.AllArcanaCards;

import Core.Cards.ArcanaCard;

public class TheWheelOfFortune extends ArcanaCard {

    public TheWheelOfFortune(int x, int y) {
        super("The Wheel of Fortune", x, y, 2, "OOPgame/Assets/ImageForCards/Arcana Card/WOF.png");
    }

    @Override
    protected void activateSkill() {
        System.out.println("The Wheel Of Fortune activate!");
        // Business Logic
    }
}
