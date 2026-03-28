package Core.Cards.AllArcanaCards;

import Core.Cards.ArcanaCard;

public class Death extends ArcanaCard {
    public Death(int x, int y) {
        super("Death", x, y, 1, "OOPgame/Assets/ImageForCards/Arcana Card/Death.png");
    }

    @Override
    protected void activateSkill() {
        System.out.println("Death activate!");
        // Business Logic
    }
}
