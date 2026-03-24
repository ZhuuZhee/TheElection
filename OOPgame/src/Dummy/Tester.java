package Dummy;

import Core.Cards.ActionCard;
import Core.Cards.AllArcanaCards.TheFoolCard;
import Core.Cards.CardSlot;
import Core.Cards.PolicyCard;
import Core.Cards.Stream.CardBufferObject;
import Core.Cards.Stream.CardReader;
import Core.Cards.Stream.CardWriter;
import Core.GameScreens.MainMenu;
import Core.UI.CardHolderUI;
import Core.ZhuzheeGame;
import Dummy.Maps.City;
import Core.UI.Shop;
import ZhuzheeEngine.Application;
import ZhuzheeEngine.Scene.Canvas;
import ZhuzheeEngine.Scene.Scene2D;
import ZhuzheeEngine.Screen;
import Core.Cards.PolicyCardA;
import Dummy.Maps.Map;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Dummy.Maps.PoliticsStats;

import javax.swing.*;

public class Tester {
    public static void CardsTestingOnScene(Scene2D scene2D){
//      Citybanna Bkk = new Citybanna("Bangkok", 50, 50, 50);
        String imageFolder = "OOPgame/Assets/ImageForCards/";

        City KuyJang = new City("Kuy_Jeng", 50, 50, 50, 100);

        CardSlot cardSlot = new CardSlot(0, 0, 100, 150, KuyJang);
        CardSlot policySlot = new CardSlot(150, 0, 100, 150, KuyJang);
        CardSlot arcanaSlot = new CardSlot(300, 0, 100, 150, KuyJang);

        ActionCard card1 = new ActionCard("Red Dragon", -100, 200, new PoliticsStats(10, 20, 30),imageFolder + "red_dragon.png",10);
        ActionCard card2 = new ActionCard("Blue Eyes", 100, 200, new PoliticsStats(0, 10, 0),imageFolder + "blue_dragon.png",2);
        PolicyCardA policyCard = new PolicyCardA("Kuy Sega", 250, 200,imageFolder + "gay.png", 3);

        TheFoolCard theFool = new TheFoolCard(arcanaSlot);
//        Citybanna myCity = new Citybanna("Bangkok", 50, 50, 50);
        card2.setDraggable(false); // <--- setDraggable # default true
//        KuyJang.printStats();
//        System.out.println("Put 'kuy sega' in to right slot");
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
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String filePath = "OOPgame/Assets/cards_test_data.json";
                    ArrayList<CardBufferObject> cards = (ArrayList<CardBufferObject>) CardReader.readActionCards(filePath);
                    int index = new Random().nextInt(cards.size());
                    hand.addCard(new ActionCard(cards.get(index),0,0));
                }
            });

            add(button);
            scene.add(this);

            onResize(scene.getWidth(),scene.getHeight());
            setVisible(true);
            scene.revalidate();
        }
        @Override
        protected void onResize(int width, int height) {
            // ยึดตำแหน่งไว้ที่ด้านล่างของหน้าจอเสมอ
            setBounds(24, 24, 164, 24);
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

    private Point mousePoint = new Point();
    public void TestingCamera(Scene2D MAIN_SCENE){

        //dragging camera
        MAIN_SCENE.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (javax.swing.SwingUtilities.isMiddleMouseButton(e)) {
                    // Calculate delta x,y
                    int dx = e.getX() - mousePoint.x;
                    int dy = e.getY() - mousePoint.y;

                    // Update camera based on pixel movement
                    MAIN_SCENE.getCamera().translate(-dx, -dy);

                    // Update reference point
                    mousePoint = e.getPoint();
                    MAIN_SCENE.repaint();
                }
            }
        });
        //update mouse position on new dragging
        MAIN_SCENE.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (javax.swing.SwingUtilities.isMiddleMouseButton(e)) {
                    mousePoint = e.getPoint();
                }
            }
        });

        //zooming
        MAIN_SCENE.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                var cam = MAIN_SCENE.getCamera();
                System.out.println(cam.getZoom());
                cam.setZoom(cam.getZoom() - e.getWheelRotation() * Application.getDeltaTime());
            }
        });
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
            System.out.println("- " + card.getName() + " Stats[Fac:" + s.getStats(PoliticsStats.Facility) +
                    ", Env:" + s.getStats(PoliticsStats.Environment) +
                    ", Eco:" + s.getStats(PoliticsStats.Economy) + "]");
        }
    }
}
