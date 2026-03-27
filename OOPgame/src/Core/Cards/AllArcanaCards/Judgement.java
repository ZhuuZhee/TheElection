package Core.Cards.AllArcanaCards;

import Core.Cards.ArcanaCard;

public class Judgement extends ArcanaCard {

    public Judgement(int x, int y) {
        super("Judgement", x, y, 5, "OOPgame/Assets/ImageForCards/Arcana Card/Judgement.png");
    }

    @Override
    protected void activateSkill() {
        System.out.println("Judgement activate!");
        // Business Logic
    }
}
