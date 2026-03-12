package Dummy;

import Core.Cards.ActionCard;
import Core.Cards.CardSlot;
import Core.Cards.PolicyCard;
import Core.GameScreens.MainMenu;
import Core.ZhuzheeGame;
import Dummy.Maps.City;
import Core.UI.Shop;
import ZhuzheeEngine.Scene.Scene2D;
import ZhuzheeEngine.Screen;
import Core.Cards.PolicyCardA;
import Dummy.Maps.Map;

import java.util.ArrayList;
import java.util.List;
import Dummy.Maps.PoliticsStats;

public class Tester {
    public static void CardsTestingOnScene(Scene2D scene2D){
//        Citybanna Bkk = new Citybanna("Bangkok", 50, 50, 50);
        City KuyJang = new City("Kuy_Jeng", 50, 50, 50, 100);
        CardSlot cardSlot = new CardSlot(0, 0, 100, 150, KuyJang);
        CardSlot policySlot = new CardSlot(150, 0, 100, 150, KuyJang);
        ActionCard card1 = new ActionCard("Red Dragon", -100, 200, new PoliticsStats(10, 20, 30));
        ActionCard card2 = new ActionCard("Blue Eyes", 100, 200, new PoliticsStats(0, 10, 0));
        PolicyCardA policyCard = new PolicyCardA("Kuy Sega", 250, 200);
//        Citybanna myCity = new Citybanna("Bangkok", 50, 50, 50);
        card2.setDraggable(false); // <--- setDraggable # default true
//        KuyJang.printStats();
//        System.out.println("Put 'kuy sega' in to right slot");
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

    public static void MapTest() {
        new Map();
    }

    public static void ShopTest() {
        List<PolicyCard> cards = new ArrayList<>();
        cards.add(new PolicyCardA("Kuy Sega", 0, 0));
        cards.add(new PolicyCardA("Red Policy", 0, 0));
        cards.add(new PolicyCardA("Blue Policy", 0, 0));
        new Shop(ZhuzheeGame.MAIN_SCENE,cards,100);
    }
}
