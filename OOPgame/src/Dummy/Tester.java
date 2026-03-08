package Dummy;

import Core.Card.ActionCard;
import Core.Card.CardSlot;
import Core.GameScreens.MainMenu;
import Core.Player.MouseHandler;
import ZhuzheeEngine.Scene.Scene2D;
import ZhuzheeEngine.Screen;

public class Tester {
    public static void CardsTestingOnScene(Scene2D scene2D){
        CardSlot cardSlot = new CardSlot(0, 0, 150, 150);
        ActionCard card1 = new ActionCard("Red Dragon", -100, 200, true);
        ActionCard card2 = new ActionCard("Blue Eyes", 100, 200, true);
        card2.setDraggable(false); // <--- setDraggable # default true
        new MouseHandler(scene2D);
    }
    public static void SampleCanvasTest(){
        new SampleCanvasUI();
    }

    public static AudioManagerTester audioManagerTester;
    public static void AudioManagerTesterInitialize(){
        audioManagerTester = new AudioManagerTester();
    }

    public static void MainMenuTest() {
        Screen.ChangeScreen(new MainMenu());
    }

    public static void main() {
        Map map = new Map();
    }
}
