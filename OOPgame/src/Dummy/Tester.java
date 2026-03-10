package Dummy;


import Core.Cards.ActionCard;
import Core.Cards.CardSlot;
import Core.Cards.PolicyCard;
import Core.GameScreens.MainMenu;
import Core.Player.MouseHandler;
import Core.ZhuzheeGame;
import UI.Shop;
import ZhuzheeEngine.Scene.Scene2D;
import ZhuzheeEngine.Screen;
import Core.Cards.PolicyCardA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tester {
    public static void CardsTestingOnScene(Scene2D scene2D){
        Citybanna Bkk = new Citybanna("Bangkok", 50, 50, 50, 50);
        CardSlot cardSlot = new CardSlot(0, 0, 100, 150, Bkk);
        CardSlot policySlot = new CardSlot(150, 0, 100, 150, Bkk);
        ActionCard card1 = new ActionCard("Red Dragon", -100, 200, true, Arrays.asList(10, 0, -5, 0));
        ActionCard card2 = new ActionCard("Blue Eyes", 100, 200, true, Arrays.asList(0, 10, 0, 0));
        PolicyCardA policyCard = new PolicyCardA("Kuy Sega", 250, 200, true);
        Citybanna myCity = new Citybanna("Bangkok", 50, 50, 50, 50);
        card2.setDraggable(false); // <--- setDraggable # default true
        new MouseHandler(scene2D);
        myCity.printStats();
        System.out.println("Put 'kuy sega' in to right slot");
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


    public static void ShopTest() {
        System.out.println("ShopTest called");
        System.out.println("Scene width: " + ZhuzheeGame.MAIN_SCENE.getWidth());
        System.out.println("Scene height: " + ZhuzheeGame.MAIN_SCENE.getHeight());

        List<PolicyCard> cards = new ArrayList<>();
        cards.add(new PolicyCardA("Kuy Sega", 0, 0, true));
        cards.add(new PolicyCardA("Red Policy", 0, 0, true));
        cards.add(new PolicyCardA("Blue Policy", 0, 0, true));

        Shop shopTest = new Shop(ZhuzheeGame.MAIN_SCENE, cards, 500, null);
        System.out.println("Shop created, visible: " + shopTest.isVisible());
        System.out.println("Shop bounds: " + shopTest.getBounds());
    }

}
