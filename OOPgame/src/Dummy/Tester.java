package Dummy;

import Core.Cards.ActionCard;
import Core.Cards.Card;
import Core.Cards.Stream.CardBufferObject;
import Core.Cards.Stream.CardReader;
import Core.Cards.Stream.CardWriter;
import Core.GameScreens.MainMenu;
import Core.Player.Player;
import Core.UI.CardHolderUI;
import Core.UI.PolicyCardHolderUI;
import Core.UI.ArcanaCardHolderUI;
import Core.ZhuzheeGame;
import Core.UI.Shop;
import ZhuzheeEngine.Audios.AudioManager;
import ZhuzheeEngine.Scene.Canvas;
import ZhuzheeEngine.Scene.GameObject;
import ZhuzheeEngine.Scene.Scene2D;
import ZhuzheeEngine.Screen;
import Core.Maps.Map;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Core.Maps.PoliticsStats;

import javax.swing.*;

public class Tester {
    public static Player dummyPlayer = new Player("dummy_01", "Test Player", true);
    public static void CardsTestingOnScene(Scene2D scene2D){
        // Removed hardcoded dummy CardSlots and ActionCards
        // The player should use DrawCardUI to get cards and play them on the Map.
    }
    public static void DrawCardTest(Scene2D scene, CardHolderUI handUI){
        DrawCardUI ui = new DrawCardUI(scene, handUI);
    }
    public static class DrawCardUI extends Canvas{
        CardHolderUI hand;
        public DrawCardUI(Scene2D scene, CardHolderUI handUi){
            super(scene);
            hand = handUi;

            setLayout(new BorderLayout());
            JButton button = new JButton("Draw Card");
            AudioManager.getInstance().loadSound("draw","draw.WAV");
            String filePath = "OOPgame/Assets/cards_test_data.json";
            ArrayList<CardBufferObject> cards = (ArrayList<CardBufferObject>) CardReader.readActionCards(filePath);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int index = new Random().nextInt(cards.size());
                    Card card = new ActionCard(cards.getFirst(),0,0);
                    if(!hand.addCard(card))
                        GameObject.Destroy(card);
                    AudioManager.getInstance().playSound("draw");
                }
            });
            add(button);
            // scene.add(this);

            onResize(scene.getWidth(),scene.getHeight());
            setVisible(true);
            scene.revalidate();
        }
        @Override
        protected void onResize(int width, int height) {
            // ยึดตำแหน่งไว้ที่ด้านล่างของหน้าจอเสมอ
            setBounds(24, height - ZhuzheeGame.PLAYER_HAND_DEV_CARDS.getHeight(), 164, 24);
            revalidate();
        }
    }

    public static AudioManagerTester audioManagerTester;
    public static void AudioManagerTesterInitialize(){
        audioManagerTester = new AudioManagerTester();
    }

    public static void MainMenuTest() {
        Screen.ChangeScreen(new MainMenu());
    }
    public static CardHolderUI CardHolderUITest(Scene2D scene2D){
        return new CardHolderUI(scene2D);
    }
    public static PolicyCardHolderUI PolicyCardHolderUITest(Scene2D scene2D){
        ZhuzheeGame.POLICY_CARD_UI = new PolicyCardHolderUI(scene2D);
        return ZhuzheeGame.POLICY_CARD_UI;
    }
    public static ArcanaCardHolderUI ArcanaCardHolderUITest(Scene2D scene2D){
        ZhuzheeGame.ARCANA_CARD_UI = new ArcanaCardHolderUI(scene2D);
        return ZhuzheeGame.ARCANA_CARD_UI;
    }
    public static void MapTest() {
        new Map();
    }

    public static void ShopTest() {
        new Shop(ZhuzheeGame.MAIN_SCENE);
    }

    public static void TestCardStream() {
        System.out.println("--- Testing Card Stream ---");
        String filePath = "OOPgame/Assets/cards_test_data.json";
        String imageFolder = "OOPgame/Assets/ImageForCards/";
        // 1. Create Mock Data
        List<ActionCard> originalCards = new ArrayList<>();
        // PoliticsStats(Facility, Environment, Economy)
        ActionCard card1 = new ActionCard("Red Dragon", -100, 200, new PoliticsStats(10, 20, 30),imageFolder + "red_dragon.png", 10);
        ActionCard card2 = new ActionCard("Blue Eyes", 100, 200, new PoliticsStats(0, 10, 0),imageFolder + "blue_dragon.png", 2);
        ZhuzheeGame.MAIN_SCENE.remove(card1);
        ZhuzheeGame.MAIN_SCENE.remove(card2);
        originalCards.add(card1);
        originalCards.add(card2);

        // 2. Write
        System.out.println("Writing " + originalCards.size() + " cards to " + filePath);
        CardWriter.writeActionCards(originalCards, filePath);

        // 3. Read
        System.out.println("Reading back from " + filePath);
        List<CardBufferObject> readCards = CardReader.readActionCards(filePath);

        // 4. Verify
        System.out.println("Read " + readCards.size() + " cards:");
        for (CardBufferObject card : readCards) {
            PoliticsStats s = card.getStats();
            System.out.println("- " + card.getName() + " Stats[Fac:" + s.getStats(PoliticsStats.FACILITY) +
                    ", Env:" + s.getStats(PoliticsStats.ENVIRONMENT) +
                    ", Eco:" + s.getStats(PoliticsStats.ECONOMY) + "]");
        }
    }
}
