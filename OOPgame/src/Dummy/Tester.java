package Dummy;

import Card.ActionCard;
import Card.CardSlot;
import ZhuzheeEngine.Scene2D.Scene;

public class Tester {
    public void CardsTestingOnScene(Scene scene){
        CardSlot cardSlot = new CardSlot(50, 150, 100, 150);
        ActionCard card1 = new ActionCard("Red Dragon", 0, 0, true);
        ActionCard card2 = new ActionCard("Blue Eyes", 150, 0, true);
        card2.setDraggable(false); // <--- setDraggable # default true
    }
}
