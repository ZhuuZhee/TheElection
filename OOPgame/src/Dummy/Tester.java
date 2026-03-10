package Dummy;

import Core.Cards.ActionCard;
import Core.Cards.CardSlot;
import Core.Cards.PolicyCard;
import Core.GameScreens.MainMenu;
import Core.Player.MouseHandler;
import Core.ZhuzheeGame;
import Dummy.Maps.City;
import UI.Shop;
import ZhuzheeEngine.Scene.Scene2D;
import ZhuzheeEngine.Screen;
import Core.Cards.PolicyCardA;
import Dummy.Maps.Map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Dummy.Maps.PoliticsStats;
import Dummy.Maps.*;
public class Tester {
    public static void CardsTestingOnScene(Scene2D scene2D){
//        Citybanna Bkk = new Citybanna("Bangkok", 50, 50, 50);
        City KuyJang = new City("Kuy_Jeng", 50, 50, 50, 100,10);
        CardSlot cardSlot = new CardSlot(0, 0, 100, 150, KuyJang);
        CardSlot policySlot = new CardSlot(150, 0, 100, 150, KuyJang);
        ActionCard card1 = new ActionCard("Red Dragon", -100, 200, true, new PoliticsStats(10, 20, 30));
        ActionCard card2 = new ActionCard("Blue Eyes", 100, 200, true, new PoliticsStats(0, 10, 0));
        PolicyCardA policyCard = new PolicyCardA("Kuy Sega", 250, 200, true);
//        Citybanna myCity = new Citybanna("Bangkok", 50, 50, 50);
        card2.setDraggable(false); // <--- setDraggable # default true
        new MouseHandler(scene2D);
        KuyJang.printStats();
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

    public static void MapTest() {
        new Map();
    }


    public static void ShopTest() {
        List<PolicyCard> cards = new ArrayList<>();
        cards.add(new PolicyCardA("Kuy Sega", 0, 0, true));
        cards.add(new PolicyCardA("Red Policy", 0, 0, true));
        cards.add(new PolicyCardA("Blue Policy", 0, 0, true));

        new Shop(ZhuzheeGame.MAIN_SCENE, cards, 500, new Shop.ShopListener() {
            @Override
            public void onCardPurchased(PolicyCard card, int remainingMoney) {
                System.out.println("Bought: " + card.getName() + " | money left: " + remainingMoney);
            }
            @Override
            public void onShopClosed() {
                System.out.println("Shop closed");
            }
        });

        // debug: ดู position ของการ์ดแต่ละใบ
        for (PolicyCard card : cards) {
            System.out.println(card.getName() + " position: " + card.getPosition());
        }
    }

}
