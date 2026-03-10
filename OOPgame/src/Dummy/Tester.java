package Dummy;

import Core.Card.ActionCard;
import Core.Card.CardSlot;
import Core.GameScreens.MainMenu;
import Core.Player.MouseHandler;
import ZhuzheeEngine.Scene.Scene2D;
import ZhuzheeEngine.Screen;

import java.util.Arrays;

public class Tester {
    public static void CardsTestingOnScene(Scene2D scene2D){
        Citybanna Bkk = new Citybanna("Bangkok", 50, 50, 50, 50);
        CardSlot cardSlot = new CardSlot(0, 0, 100, 150, Bkk);
        ActionCard card1 = new ActionCard("Red Dragon", -100, 200, true, Arrays.asList(10, 0, -5, 0));
        ActionCard card2 = new ActionCard("Blue Eyes", 100, 200, true, Arrays.asList(0, 10, 0, 0));
        Citybanna myCity = new Citybanna("Bangkok", 50, 50, 50, 50);
        card2.setDraggable(false); // <--- setDraggable # default true
        new MouseHandler(scene2D);
    }
    public static void SampleCanvasTest(Scene2D scene2D){
        new SampleCanvasUI(scene2D);
    }

    public static AudioManagerTester audioManagerTester;
    public static void AudioManagerTesterInitialize(){
        audioManagerTester = new AudioManagerTester();
    }

    public static void MainMenuTest() {
        Screen.ChangeScreen(new MainMenu());
    }
}
