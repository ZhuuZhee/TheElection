package Dummy;

import Core.Cards.ActionCard;
import Core.Cards.AllArcanaCards.TheFoolCard;
import Core.Cards.CardSlot;
import Core.Cards.PolicyCard;
import Core.GameScreens.MainMenu;
import Core.UI.ActionCardHolderUI;
import Core.ZhuzheeGame;
import Dummy.Maps.City;
import Core.UI.Shop;
import ZhuzheeEngine.Application;
import ZhuzheeEngine.Scene.Camera2D;
import ZhuzheeEngine.Scene.Scene2D;
import ZhuzheeEngine.Screen;
import Core.Cards.PolicyCardA;
import Dummy.Maps.Map;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import Dummy.Maps.PoliticsStats;

public class Tester {
    public static void CardsTestingOnScene(Scene2D scene2D){
//        Citybanna Bkk = new Citybanna("Bangkok", 50, 50, 50);
        City KuyJang = new City("Kuy_Jeng", 50, 50, 50, 100);

        CardSlot cardSlot = new CardSlot(0, 0, 100, 150, KuyJang);
        CardSlot policySlot = new CardSlot(150, 0, 100, 150, KuyJang);
        CardSlot arcanaSlot = new CardSlot(300, 0, 100, 150, KuyJang);

        ActionCard card1 = new ActionCard("Red Dragon", -100, 200, new PoliticsStats(10, 20, 30));
        ActionCard card2 = new ActionCard("Blue Eyes", 100, 200, new PoliticsStats(0, 10, 0));
        PolicyCardA policyCard = new PolicyCardA("Kuy Sega", 250, 200);

        TheFoolCard theFool = new TheFoolCard(arcanaSlot);
//        Citybanna myCity = new Citybanna("Bangkok", 50, 50, 50);
        card2.setDraggable(false); // <--- setDraggable # default true
//        KuyJang.printStats();
//        System.out.println("Put 'kuy sega' in to right slot");
    }

    public static AudioManagerTester audioManagerTester;
    public static void AudioManagerTesterInitialize(){
        audioManagerTester = new AudioManagerTester();
    }

    public static void MainMenuTest() {
        Screen.ChangeScreen(new MainMenu());
    }
    public static void CardHolderUITest(Scene2D scene2D){
        new ActionCardHolderUI(scene2D);
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
                cam.setZoom(cam.getZoom() + e.getWheelRotation() * Application.getDeltaTime());
            }
        });
    }

}
