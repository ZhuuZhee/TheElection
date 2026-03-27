package Dummy;

import Core.Cards.ActionCard;
import Core.Cards.AllArcanaCards.TheFoolCard;
import Core.Cards.Card;
import Core.Cards.CardSlot;
import Core.Cards.Stream.CardBufferObject;
import Core.Cards.Stream.CardReader;
import Core.Cards.Stream.CardWriter;
import Core.Cards.Stream.PolicyCardRegistry;
import Core.GameScreens.MainMenu;
import Core.Player.Player;
import Core.UI.CardHolderUI;
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
    public static TesterUI CardTesterUI(Scene2D scene){
        return new TesterUI(scene);
    }

    public static AudioManagerTester audioManagerTester;
    public static void AudioManagerTesterInitialize(){
        audioManagerTester = new AudioManagerTester();
    }

    public static void MainMenuTest() {
        Screen.ChangeScreen(new MainMenu());
    }

    public static Map MapTest() {
        return new Map();
    }

    public static void ShopTest() {
        new Shop(ZhuzheeGame.MAIN_SCENE);
    }

    public static void TestArcanaCard() {
        if (ZhuzheeGame.ARCANA_CARD_UI != null) {
            // ArcanaCard requires a CardSlot for positioning in constructor,
            // but since it will be in a Holder UI, we can use a dummy position.
            // We create a temporary CardSlot at (0,0) just to satisfy the constructor.
            CardSlot dummySlot = new CardSlot(0, 0, 100, 150, null);
            TheFoolCard foolCard = new TheFoolCard(ZhuzheeGame.ARCANA_CARD_UI.getX(), ZhuzheeGame.ARCANA_CARD_UI.getY());
            ZhuzheeGame.ARCANA_CARD_UI.addCard(foolCard);
        }
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

    public static class TesterUI extends Canvas{
        CardHolderUI hand;
        CardHolderUI policyhand;
        CardBufferObject[] actionCards;
        private JButton drawActionCardBtn;
        private JButton drawPolicyCardBtn;
        private JButton endTurn;
        public TesterUI(Scene2D scene){
            super(scene);
            actionCards = loadsActionCard();
            AudioManager.getInstance().loadSound("draw","draw.WAV");

            hand = ZhuzheeGame.DEVLOPMENT_CARD_HAND;
            policyhand = ZhuzheeGame.POLICY_CARD_HAND;

            setLayout(new FlowLayout(FlowLayout.TRAILING));
            drawActionCardBtn = new JButton("Draw Card");
            drawPolicyCardBtn = new JButton("Draw Policy");
            endTurn = new JButton("End Turn");

            drawActionCardBtn.addActionListener(drawCardAction);
            add(drawActionCardBtn);

            drawPolicyCardBtn.addActionListener(drawCardAction);
            add(drawPolicyCardBtn);

            endTurn.addMouseListener(ZhuzheeGame.MOUSE_HOVER_SFX);
            endTurn.addActionListener(endTurnAction);
            add(endTurn);

            onResize(scene.getWidth(),scene.getHeight());
            setAnchors(1,1);
            setOpaque(false);
            setVisible(true);
            scene.revalidate();
        }
        private CardBufferObject[] loadsActionCard(){
            return CardReader.getLoadedCards().toArray(new CardBufferObject[0]);
        }
        ActionListener endTurnAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ZhuzheeGame.CLIENT.endTurn();
            }
        };
        ActionListener drawCardAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AudioManager.getInstance().playSound("draw");
                if (e.getSource() == drawActionCardBtn) {
                    drawDevCard();
                } else {
                    drawPolicyCard();
                }
            }
        };

        private void drawPolicyCard() {
            Card policyCard = PolicyCardRegistry.rollCards(1).getFirst();

            if (policyhand.isFull()) {
                Card firstPolicyCard = policyhand.getCards().getFirst();

                // 4. ลบออกจาก UI และทำลาย Object ทิ้งเพื่อไม่ให้ค้างในหน่วยความจำหรือบนแผนที่
                policyhand.removeCard(firstPolicyCard);
                GameObject.Destroy(firstPolicyCard);

                System.out.println("Policy full! Removed: " + firstPolicyCard.getName());
            }

            policyhand.addCard(policyCard);
        }

        private void drawDevCard() {
            int index = new Random().nextInt(actionCards.length);
            Card card = new ActionCard(actionCards[index],0,0);
            if(!hand.addCard(card))
                GameObject.Destroy(card);
        }
    }
}
