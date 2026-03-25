package Dummy;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Cards.Stream.CardBufferObject;
import Core.Cards.Stream.CardReader;
import Core.Cards.Stream.CardWriter;
import Core.GameScreens.MainMenu;
import Core.Player.Player;
import Core.UI.CardHolderUI;
import Core.ZhuzheeGame;
import Core.UI.Shop;
import ZhuzheeEngine.Application;
import ZhuzheeEngine.Scene.Canvas;
import ZhuzheeEngine.Scene.Scene2D;
import ZhuzheeEngine.Screen;
import Core.Cards.PolicyCardA;
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
            // scene.add(this);

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
        String imageFolder = "OOPgame/Assets/ImageForCards/";
        cards.add(new PolicyCardA("Kuy Sega", 0, 0,imageFolder + "gay.png",10));
        cards.add(new PolicyCardA("Red Policy", 100, 0,imageFolder + "gay.png", 5));
        cards.add(new PolicyCardA("Blue Policy", 200, 0,imageFolder + "gay.png",1));
        new Shop(ZhuzheeGame.MAIN_SCENE,cards);
    }

    private Point mousePoint = new Point();
    public void TestingCamera(Scene2D MAIN_SCENE){
        // ใช้ AWTEventListener เพื่อดักจับ Input ของเมาส์แบบ Global ไม่ว่าจะชี้อยู่บน Map หรือ UI ตัวไหนก็จะขยับกล้องได้
        long eventMask = AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK;

        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            // เช็ค MouseWheel ก่อน เพราะมันสืบทอดมาจาก MouseEvent
            if (event instanceof MouseWheelEvent e) {
                var cam = MAIN_SCENE.getCamera();
                cam.setZoom(cam.getZoom() - e.getWheelRotation() * ZhuzheeEngine.Application.getDeltaTime());
            }
            // แล้วค่อยเช็ค MouseEvent ธรรมดา
            else if (event instanceof MouseEvent e) {
                // อัปเดตตำแหน่งตั้งต้นตอนเริ่มกดเมาส์กลาง
                if (e.getID() == MouseEvent.MOUSE_PRESSED && javax.swing.SwingUtilities.isMiddleMouseButton(e)) {
                    mousePoint = e.getLocationOnScreen();
                }
                // คอยขยับกล้องเวลาลากเมาส์กลาง
                else if (e.getID() == MouseEvent.MOUSE_DRAGGED && javax.swing.SwingUtilities.isMiddleMouseButton(e)) {
                    int dx = e.getLocationOnScreen().x - mousePoint.x;
                    int dy = e.getLocationOnScreen().y - mousePoint.y;

                    MAIN_SCENE.getCamera().translate(-dx, -dy);
                    mousePoint = e.getLocationOnScreen(); // ใช้ LocationOnScreen เพื่อป้องกันหน้าจอกระตุกเมื่อเมาส์ลากข้ามระหว่าง Component
                    MAIN_SCENE.repaint();
                }
            }
        }, eventMask);
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
