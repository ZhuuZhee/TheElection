package Dummy;

import Core.Card.ActionCard;
import Core.Card.CardSlot;
import Core.Player.MouseHandler;
import ZhuzheeEngine.Scene.Scene2D;

public class Tester {
    public static void CardsTestingOnScene(Scene2D scene2D){
        CardSlot cardSlot = new CardSlot(0, 0, 150, 150);
        ActionCard card1 = new ActionCard("Red Dragon", -100, 200, true);
        ActionCard card2 = new ActionCard("Blue Eyes", 100, 200, true);
        card2.setDraggable(false); // <--- setDraggable # default true
        new MouseHandler(scene2D);
    }

    public static void AudioManagerTest(){
        AudioManagerTester Test = new AudioManagerTester();
    }
}
