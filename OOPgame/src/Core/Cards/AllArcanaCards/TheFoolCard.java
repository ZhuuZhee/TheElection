package Core.Cards.AllArcanaCards;

import Core.Cards.ArcanaCard;
import Core.Cards.CardSlot;

public class TheFoolCard extends ArcanaCard {

    public TheFoolCard(CardSlot targetSlot) {
        super("The Fool", targetSlot, 3);
    }

    @Override
    protected void activateSkill() {
        System.out.println("The Fool activate!");
        // Business Logic
    }

}
